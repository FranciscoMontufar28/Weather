package com.francisco.weather.feature.forecast.domain

import com.francisco.weather.feature.forecast.domain.model.ForecastData

interface ForecastRepository {

    /**
     * Returns 3-day weather forecast (today + 2 days) for [locationQuery].
     * Returns [Result.failure] with a [com.francisco.weather.core.network.WeatherError] on error.
     */
    suspend fun getForecast(locationQuery: String): Result<ForecastData>
}
