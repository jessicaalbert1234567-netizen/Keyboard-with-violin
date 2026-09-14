package com.example.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_words",
    indices = [Index(value = ["word", "languageCode"], unique = true)]
)
data class UserWord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val languageCode: String,
    val frequency: Int = 1,
    val lastUsed: Long = System.currentTimeMillis()
)
