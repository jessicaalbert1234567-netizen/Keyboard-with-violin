package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "language_packs")
data class LanguagePackEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val nativeName: String,
    val version: Int,
    val downloadStatus: String, // INSTALLED, AVAILABLE, DOWNLOADING, DOWNLOADED, UPDATE_AVAILABLE
    val localFilePath: String? = null,
    val fileSizeFormatted: String = "1.2 MB",
    val sha256: String = "",
    val downloadUrl: String = ""
)
