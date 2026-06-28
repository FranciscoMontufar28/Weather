package com.francisco.weather.feature.dashboard.domain.model

import com.francisco.weather.feature.forecast.domain.model.ForecastData

/**
 * Result of [com.francisco.weather.feature.dashboard.domain.usecase.LoadCurrentWeatherUseCase].
 * @param isApproximate true when the location was resolved via IP fallback (no GPS fix available).
 */
data class ResolvedWeather(
    val forecast: ForecastData,
    val isApproximate: Boolean,
)
