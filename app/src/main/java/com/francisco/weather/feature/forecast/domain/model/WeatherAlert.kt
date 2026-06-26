package com.francisco.weather.feature.forecast.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherAlert(
    val headline: String,
    val event: String,
    val severity: String, // "Moderate", "Severe", "Extreme"
    val areas: String,
    val desc: String,
)
