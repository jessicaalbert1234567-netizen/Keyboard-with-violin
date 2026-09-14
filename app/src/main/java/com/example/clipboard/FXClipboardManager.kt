package com.example.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.ClipboardEntry
import com.example.data.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FXClipboardManager(private val context: Context) {
    private val systemClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    private val db = AppDatabase.getInstance(context)
    private val dao = db.clipboardDao()
    private val settingsRepo = SettingsRepository.getInstance(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        systemClipboard?.addPrimaryClipChangedListener {
            checkAndStoreClip()
        }
    }

    val entriesFlow: Flow<List<ClipboardEntry>> = dao.getAllEntries()

    private fun checkAndStoreClip() {
        scope.launch {
            val settings = settingsRepo.settingsFlow.first()
            if (!settings.clipboardHistoryEnabled) return@launch

            val clip = systemClipboard?.primaryClip
            if (clip != null && clip.itemCount > 0) {
                val text = clip.getItemAt(0).coerceToText(context)?.toString()?.trim()
                if (!text.isNullOrBlank() && text.length <= 1000) {
                    dao.insert(ClipboardEntry(text = text))
                    dao.trimEntries(settings.maxClipboardItems)
                }
            }
        }
    }

    fun copyToClipboard(text: String) {
        val clip = ClipData.newPlainText("FX Keyboard", text)
        systemClipboard?.setPrimaryClip(clip)
    }

    fun pinEntry(entry: ClipboardEntry) {
        scope.launch {
            dao.update(entry.copy(isPinned = !entry.isPinned))
        }
    }

    fun deleteEntry(id: Long) {
        scope.launch {
            dao.deleteById(id)
        }
    }

    fun clearUnpinned() {
        scope.launch {
            dao.clearUnpinned()
        }
    }

    fun clearAll() {
        scope.launch {
            dao.clearAll()
        }
    }

    companion object {
        @Volatile
        private var instance: FXClipboardManager? = null

        fun getInstance(context: Context): FXClipboardManager {
            return instance ?: synchronized(this) {
                instance ?: FXClipboardManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
