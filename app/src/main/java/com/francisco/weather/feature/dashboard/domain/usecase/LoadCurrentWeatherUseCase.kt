package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.dashboard.domain.LocationProvider
import com.francisco.weather.feature.dashboard.domain.model.ResolvedWeather
import com.francisco.weather.feature.forecast.domain.ForecastRepository
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoadCurrentWeatherUseCase @Inject constructor(
    private val forecastRepository: ForecastRepository,
    private val cacheRepository: CachedWeatherRepository,
    private val locationProvider: LocationProvider,
) {
    suspend operator fun invoke(): Result<ResolvedWeather> {
        val coords = locationProvider.currentLocation()
        val query = coords?.let { "${it.latitude},${it.longitude}" } ?: "auto:ip"
        return forecastRepository.getForecast(query)
            .onSuccess { cacheRepository.save(it) }
            .map { ResolvedWeather(forecast = it, isApproximate = coords == null) }
    }
}
