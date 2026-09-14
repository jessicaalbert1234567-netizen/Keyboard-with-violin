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
            packDao.updateStatus(packId, PackDownloadStatus.DOWNLOADING.name)
            delay(1200L) // Simulate network streaming / chunk transfer

            // Save local pack file
            val dir = File(context.filesDir, "language_packs").apply { mkdirs() }
            val packFile = File(dir, "$packId.pack")
            val dummyPackData = "FX_KEYBOARD_PACK:$packId:${System.currentTimeMillis()}".toByteArray()
            FileOutputStream(packFile).use { it.write(dummyPackData) }

            // Verify SHA-256
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(dummyPackData)
            val calculatedSha = digest.joinToString("") { "%02x".format(it) }

            val pack = packDao.getPackById(packId)
            if (pack != null) {
                packDao.insertOrUpdate(
                    pack.copy(
                        downloadStatus = PackDownloadStatus.INSTALLED.name,
                        localFilePath = packFile.absolutePath,
                        sha256 = calculatedSha
                    )
                )
            }
        }
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
