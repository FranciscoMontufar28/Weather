package com.francisco.weather.core.data.local.stadium

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_cup_stadiums")
data class StadiumEntity(
    @PrimaryKey val name: String,
    val city: String,
    val country: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    // Weather fields — null until the first weather sync
    val tempC: Double? = null,
    val conditionText: String? = null,
    val conditionIconUrl: String? = null,
    val weatherUpdatedAt: Long? = null,
)
