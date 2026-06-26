package com.francisco.weather.feature.forecast.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HourWeather(
    val time: String,          // "2026-06-26 15:00"
    val tempC: Double,
    val condition: Condition,
    val chanceOfRain: Int,     // 0-100
    val isDay: Boolean,
)
