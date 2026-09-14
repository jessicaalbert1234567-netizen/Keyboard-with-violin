package com.example.language

enum class PackDownloadStatus {
    INSTALLED,
    AVAILABLE,
    DOWNLOADING,
    DOWNLOADED,
    UPDATE_AVAILABLE
}

data class LanguagePack(
    val id: String,
    val name: String,
    val nativeName: String,
    val version: Int,
    val fileSizeFormatted: String,
    val status: PackDownloadStatus,
    val downloadUrl: String = "",
    val sha256: String = "",
    val hasNativeLayout: Boolean = true,
    val hasPhoneticMode: Boolean = true
)
