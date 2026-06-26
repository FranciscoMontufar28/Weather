package com.francisco.weather.core.data.local.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedWeatherDao {

    @Query("SELECT * FROM cached_weather WHERE id = 0")
    fun observe(): Flow<CachedWeatherEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CachedWeatherEntity)
}
