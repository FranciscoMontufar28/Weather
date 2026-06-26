package com.francisco.weather.core.data.local.stadium

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StadiumDao {

    @Query("SELECT * FROM world_cup_stadiums")
    fun observeAll(): Flow<List<StadiumEntity>>

    /** Inserts base stadium rows without overwriting cached weather data. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(entities: List<StadiumEntity>)

    /** Returns the timestamp of the most recent weather fetch, or null if none yet. */
    @Query("SELECT MAX(weatherUpdatedAt) FROM world_cup_stadiums")
    suspend fun lastWeatherUpdate(): Long?

    /** Updates only the weather columns for a single stadium row. */
    @Query(
        """UPDATE world_cup_stadiums
           SET tempC = :tempC,
               conditionText = :text,
               conditionIconUrl = :icon,
               weatherUpdatedAt = :ts
           WHERE name = :name""",
    )
    suspend fun updateWeather(name: String, tempC: Double, text: String, icon: String, ts: Long)
}
