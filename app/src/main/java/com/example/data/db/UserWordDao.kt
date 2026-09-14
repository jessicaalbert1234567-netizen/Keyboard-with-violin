package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserWordDao {
    @Query("SELECT word FROM user_words WHERE languageCode = :languageCode AND word LIKE :prefix || '%' ORDER BY frequency DESC, lastUsed DESC LIMIT :limit")
    suspend fun getWordsStartingWith(prefix: String, languageCode: String, limit: Int = 10): List<String>

    @Query("SELECT * FROM user_words WHERE word = :word AND languageCode = :languageCode LIMIT 1")
    suspend fun findWord(word: String, languageCode: String): UserWord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userWord: UserWord): Long

    @Update
    suspend fun update(userWord: UserWord)

    @Query("SELECT * FROM user_words ORDER BY frequency DESC")
    fun getAllWords(): Flow<List<UserWord>>

    @Query("DELETE FROM user_words WHERE id = :id")
    suspend fun deleteWord(id: Long)

    @Query("DELETE FROM user_words")
    suspend fun clearDictionary()
}
