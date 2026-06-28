package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.forecast.domain.ForecastRepository
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadCurrentWeatherUseCase @Inject constructor(
    private val forecastRepository: ForecastRepository,
    private val cacheRepository: CachedWeatherRepository,
) {
    suspend operator fun invoke(locationQuery: String): Result<ForecastData> =
        forecastRepository.getForecast(locationQuery).onSuccess { cacheRepository.save(it) }
}
