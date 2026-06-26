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

    /** Inserts base stadium rows without overwriting cached weather/sports data. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(entities: List<StadiumEntity>)

    /** Returns the timestamp of the most recent weather fetch, or null if none yet. */
    @Query("SELECT MAX(weatherUpdatedAt) FROM world_cup_stadiums")
    suspend fun lastWeatherUpdate(): Long?

    /** Returns the timestamp of the most recent sports fetch, or null if none yet. */
    @Query("SELECT MAX(sportsUpdatedAt) FROM world_cup_stadiums")
    suspend fun lastSportsUpdate(): Long?

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

    /** Updates only the sports columns for a single stadium row. */
    @Query(
        """UPDATE world_cup_stadiums
           SET matchName = :matchName,
               matchTournament = :matchTournament,
               matchStart = :matchStart,
               sportsUpdatedAt = :ts
           WHERE name = :name""",
    )
    suspend fun updateSports(
        name: String,
        matchName: String,
        matchTournament: String,
        matchStart: String,
        ts: Long,
    )
}
