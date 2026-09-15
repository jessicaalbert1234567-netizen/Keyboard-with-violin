package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LanguagePackDao {
    @Query("SELECT * FROM language_packs ORDER BY name ASC")
    fun getAllPacks(): Flow<List<LanguagePackEntity>>

    @Query("SELECT * FROM language_packs WHERE id = :id LIMIT 1")
    suspend fun getPackById(id: String): LanguagePackEntity?

    @Query("SELECT * FROM language_packs WHERE downloadStatus IN ('INSTALLED', 'DOWNLOADED')")
    fun getInstalledPacks(): Flow<List<LanguagePackEntity>>

    @Query("SELECT * FROM language_packs WHERE downloadStatus IN ('INSTALLED', 'DOWNLOADED')")
    suspend fun getInstalledPacksList(): List<LanguagePackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(pack: LanguagePackEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(packs: List<LanguagePackEntity>)

    @Update
    suspend fun update(pack: LanguagePackEntity)

    @Query("UPDATE language_packs SET downloadStatus = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM language_packs WHERE id = :id")
    suspend fun delete(id: String)
}
