package com.example

import android.app.Application
import com.example.audio.SoundEngine
import com.example.clipboard.FXClipboardManager
import com.example.data.db.AppDatabase
import com.example.data.settings.SettingsRepository
import com.example.language.LanguagePackManager
import com.example.language.TransliterationEngine
import com.example.suggestions.OfflineDictionarySuggestionEngine

class FXApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Warm up and initialize singletons
        AppDatabase.getInstance(this)
        SettingsRepository.getInstance(this)
        SoundEngine.getInstance(this)
        LanguagePackManager.getInstance(this)
        FXClipboardManager.getInstance(this)
    }
}
