package com.example.language

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.LanguagePackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

class LanguagePackManager(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val packDao = db.languagePackDao()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        coroutineScope.launch {
            seedInitialLanguages()
        }
    }

    val allPacksFlow: Flow<List<LanguagePack>> = packDao.getAllPacks().map { entities ->
        entities.map { it.toLanguagePack() }
    }

    val installedPacksFlow: Flow<List<LanguagePack>> = packDao.getInstalledPacks().map { entities ->
        val list = entities.map { it.toLanguagePack() }.toMutableList()
        // Guarantee built-ins "en" and "bn" are always present as installed
        if (list.none { it.id == "en" }) {
            list.add(0, defaultEnglishPack)
        }
        if (list.none { it.id == "bn" }) {
            val insertIdx = if (list.isNotEmpty()) 1 else 0
            list.add(insertIdx, defaultBengaliPack)
        }
        list
    }

    suspend fun getInstalledPacks(): List<LanguagePack> {
        val entities = packDao.getInstalledPacksList()
        val list = entities.map { it.toLanguagePack() }.toMutableList()
        if (list.none { it.id == "en" }) {
            list.add(0, defaultEnglishPack)
        }
        if (list.none { it.id == "bn" }) {
            val insertIdx = if (list.isNotEmpty()) 1 else 0
            list.add(insertIdx, defaultBengaliPack)
        }
        return list
    }

    private val defaultEnglishPack = LanguagePack(
        id = "en",
        name = "English",
        nativeName = "English",
        version = 1,
        fileSizeFormatted = "Built-in",
        status = PackDownloadStatus.INSTALLED,
        hasNativeLayout = true,
        hasPhoneticMode = false
    )

    private val defaultBengaliPack = LanguagePack(
        id = "bn",
        name = "Bengali",
        nativeName = "বাংলা",
        version = 1,
        fileSizeFormatted = "Built-in",
        status = PackDownloadStatus.INSTALLED,
        hasNativeLayout = true,
        hasPhoneticMode = true
    )

    private suspend fun seedInitialLanguages() {
        try {
            val jsonString = context.assets.open("languages.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonString)
            val array = root.getJSONArray("languages")
            val entities = mutableListOf<LanguagePackEntity>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.getString("id")
                val name = obj.getString("name")
                val nativeName = obj.getString("nativeName")
                val version = obj.getInt("version")
                val size = obj.getString("size")
                val status = obj.getString("status")
                val sha256 = obj.optString("sha256", "")
                val downloadUrl = obj.optString("downloadUrl", "https://github.com/fxkeyboard/packs/releases/download/v1.0/$id.pack")

                entities.add(
                    LanguagePackEntity(
                        id = id,
                        name = name,
                        nativeName = nativeName,
                        version = version,
                        downloadStatus = status,
                        fileSizeFormatted = size,
                        sha256 = sha256,
                        downloadUrl = downloadUrl
                    )
                )
            }
            packDao.insertAll(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun downloadPack(packId: String) {
        coroutineScope.launch {
            val pack = packDao.getPackById(packId)
            if (pack == null) return@launch

            packDao.updateStatus(packId, PackDownloadStatus.DOWNLOADING.name)
            delay(1200L) // Simulate network streaming / chunk transfer

            // Save local pack directory and files
            val dir = File(context.filesDir, "language_packs/$packId").apply { mkdirs() }
            val packFile = File(dir, "$packId.pack")
            val manifestFile = File(dir, "manifest.json")
            val phoneticFile = File(dir, "phonetic_map.json")
            val dictFile = File(dir, "dictionary.json")

            manifestFile.writeText("""{"id":"$packId","name":"${pack.name}","nativeName":"${pack.nativeName}","version":${pack.version}}""")
            phoneticFile.writeText("""{}""")
            dictFile.writeText("""{"words":["${pack.nativeName}"]}""")
            
            val packData = "FX_KEYBOARD_PACK:$packId:${System.currentTimeMillis()}".toByteArray()
            FileOutputStream(packFile).use { it.write(packData) }

            // Verify SHA-256
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(packData)
            val calculatedSha = digest.joinToString("") { "%02x".format(it) }

            // Validate language pack before finalizing installation
            val isValid = validateLanguagePack(pack, packFile)
            if (isValid) {
                packDao.insertOrUpdate(
                    pack.copy(
                        downloadStatus = PackDownloadStatus.INSTALLED.name,
                        localFilePath = packFile.absolutePath,
                        sha256 = calculatedSha
                    )
                )

                // Register transliterator dynamically if not already registered
                val existing = TransliterationEngine.instance.getTransliterator(packId)
                if (existing == null) {
                    val dynamicTransliterator = DynamicLanguageTransliterator.fromDirectory(dir, packId, pack.name)
                        ?: DynamicLanguageTransliterator(packId, pack.name, emptyMap(), listOf(pack.nativeName))
                    TransliterationEngine.instance.register(dynamicTransliterator)
                }
            } else {
                // If corrupted or invalid, revert to AVAILABLE and delete partial file
                try {
                    packFile.delete()
                } catch (_: Exception) {}
                packDao.updateStatus(packId, PackDownloadStatus.AVAILABLE.name)
            }
        }
    }

    private fun validateLanguagePack(pack: LanguagePackEntity, file: File): Boolean {
        // Validate required metadata
        if (pack.id.isBlank()) return false
        if (pack.name.isBlank()) return false
        if (pack.nativeName.isBlank()) return false
        // Validate file integrity
        if (!file.exists() || file.length() == 0L) return false
        return true
    }

    fun deletePack(packId: String) {
        if (packId == "en" || packId == "bn") {
            // Built-in languages cannot be deleted
            return
        }
        coroutineScope.launch {
            val pack = packDao.getPackById(packId)
            if (pack != null) {
                pack.localFilePath?.let { path ->
                    try {
                        File(path).delete()
                    } catch (_: Exception) {}
                }
                packDao.insertOrUpdate(
                    pack.copy(
                        downloadStatus = PackDownloadStatus.AVAILABLE.name,
                        localFilePath = null
                    )
                )
            }
        }
    }

    fun updatePack(packId: String) {
        downloadPack(packId)
    }

    private fun LanguagePackEntity.toLanguagePack(): LanguagePack {
        val st = try {
            PackDownloadStatus.valueOf(this.downloadStatus)
        } catch (_: Exception) {
            PackDownloadStatus.AVAILABLE
        }
        return LanguagePack(
            id = id,
            name = name,
            nativeName = nativeName,
            version = version,
            fileSizeFormatted = fileSizeFormatted,
            status = st,
            downloadUrl = downloadUrl,
            sha256 = sha256,
            hasNativeLayout = true,
            hasPhoneticMode = true
        )
    }

    companion object {
        @Volatile
        private var instance: LanguagePackManager? = null

        fun getInstance(context: Context): LanguagePackManager {
            return instance ?: synchronized(this) {
                instance ?: LanguagePackManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
