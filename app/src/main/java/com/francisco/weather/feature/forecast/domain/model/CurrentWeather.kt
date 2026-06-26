package com.francisco.weather.feature.forecast.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeather(
    val tempC: Double,
    val condition: Condition,
    val humidity: Int,
    val windKph: Double,
    val feelsLikeC: Double,
)
