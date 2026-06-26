package com.francisco.weather.feature.forecast.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Astro(
    val sunrise: String,       // "05:47 AM"
    val sunset: String,        // "06:10 PM"
    val moonPhase: String,     // "Waxing Gibbous"
    val moonIllumination: Int, // 93 (%)
)
