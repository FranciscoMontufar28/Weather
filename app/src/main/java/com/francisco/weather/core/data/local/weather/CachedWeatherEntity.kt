package com.francisco.weather.core.data.local.weather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_weather")
data class CachedWeatherEntity(
    @PrimaryKey val id: Int = 0,
    val json: String,
    val updatedAt: Long,
)
