package com.francisco.weather.feature.forecast.domain.usecase

import com.francisco.weather.feature.forecast.domain.ForecastRepository
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetForecastUseCase @Inject constructor(
    private val repository: ForecastRepository,
) {
    suspend operator fun invoke(locationQuery: String): Result<ForecastData> =
        repository.getForecast(locationQuery)
}
