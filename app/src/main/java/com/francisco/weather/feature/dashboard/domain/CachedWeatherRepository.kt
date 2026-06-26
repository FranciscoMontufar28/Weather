package com.francisco.weather.feature.dashboard.domain

import com.francisco.weather.feature.forecast.domain.model.ForecastData
import kotlinx.coroutines.flow.Flow

interface CachedWeatherRepository {
    fun observeCached(): Flow<ForecastData?>
    suspend fun save(data: ForecastData)
}
