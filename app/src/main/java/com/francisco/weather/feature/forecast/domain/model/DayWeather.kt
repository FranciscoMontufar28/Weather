package com.francisco.weather.feature.forecast.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DayWeather(
    val date: String,           // "yyyy-MM-dd"
    val avgTempC: Double,
    val maxTempC: Double,
    val minTempC: Double,
    val condition: Condition,
)
