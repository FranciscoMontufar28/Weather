package com.francisco.weather.feature.forecast.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ForecastData(
    val locationName: String,
    val region: String,
    val country: String,
    /** 3 days: today (index 0) + next 2 days */
    val days: List<DayWeather>,
    /** Current real-time conditions (used in landscape detail panel) */
    val current: CurrentWeather,
)
