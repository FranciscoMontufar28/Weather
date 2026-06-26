package com.francisco.weather.core.data.local.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {

    @Query("SELECT * FROM recent_searches ORDER BY savedAt DESC LIMIT 10")
    fun observeRecent(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RecentSearchEntity)

    @Query("DELETE FROM recent_searches")
    suspend fun clearAll()
}
