package com.francisco.weather.feature.forecast.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DayWeather(
    val date: String,                               // "yyyy-MM-dd"
    val avgTempC: Double,
    val maxTempC: Double,
    val minTempC: Double,
    val condition: Condition,
    val uv: Double = 0.0,
    val maxWindKph: Double = 0.0,
    val chanceOfRain: Int = 0,                      // 0-100
    val totalPrecipMm: Double = 0.0,
    val astro: Astro = Astro("", "", "", 0),
    val hours: List<HourWeather> = emptyList(),     // 24 entries
)
