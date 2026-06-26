package com.francisco.weather.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.francisco.weather.core.data.local.recent.RecentSearchDao
import com.francisco.weather.core.data.local.recent.RecentSearchEntity
import com.francisco.weather.core.data.local.stadium.StadiumDao
import com.francisco.weather.core.data.local.stadium.StadiumEntity
import com.francisco.weather.core.data.local.weather.CachedWeatherDao
import com.francisco.weather.core.data.local.weather.CachedWeatherEntity

@Database(
    entities = [RecentSearchEntity::class, StadiumEntity::class, CachedWeatherEntity::class],
    version = 4,
    exportSchema = false,
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun stadiumDao(): StadiumDao
    abstract fun cachedWeatherDao(): CachedWeatherDao
}
