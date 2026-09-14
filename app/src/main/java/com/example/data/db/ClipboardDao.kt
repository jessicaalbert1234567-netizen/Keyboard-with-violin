package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {
    @Query("SELECT * FROM clipboard_entries ORDER BY isPinned DESC, timestamp DESC")
    fun getAllEntries(): Flow<List<ClipboardEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ClipboardEntry): Long

    @Update
    suspend fun update(entry: ClipboardEntry)

    @Query("DELETE FROM clipboard_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM clipboard_entries WHERE isPinned = 0")
    suspend fun clearUnpinned()

    @Query("DELETE FROM clipboard_entries")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM clipboard_entries")
    suspend fun getCount(): Int

    @Query("DELETE FROM clipboard_entries WHERE id NOT IN (SELECT id FROM clipboard_entries ORDER BY isPinned DESC, timestamp DESC LIMIT :limit)")
    suspend fun trimEntries(limit: Int)
}
