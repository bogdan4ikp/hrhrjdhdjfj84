package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LockScreenDao {
    @Query("SELECT * FROM lock_screen_config WHERE id = 1 LIMIT 1")
    fun getConfigFlow(): Flow<LockScreenConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: LockScreenConfig)

    @Query("DELETE FROM lock_screen_config")
    suspend fun clearConfig()
}
