package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveCachedWeatherUseCase @Inject constructor(
    private val repository: CachedWeatherRepository,
) {
    operator fun invoke(): Flow<ForecastData?> = repository.observeCached()
}
