package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import com.francisco.weather.feature.forecast.domain.ForecastRepository

class LoadCurrentWeatherBloc(
    private val repository: ForecastRepository,
    private val cacheRepository: CachedWeatherRepository,
) : BaseBloc<DashboardEvent.LoadCurrentWeather, DashboardState>() {

    override val tag = "LoadCurrentWeatherBloc"

    override suspend fun handleEvent(
        event: DashboardEvent.LoadCurrentWeather,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        updateState { it.copy(isLoadingWeather = true, weatherError = null) }

        repository.getForecast(event.locationQuery).fold(
            onSuccess = { forecast ->
                cacheRepository.save(forecast)
                updateState { it.copy(currentWeather = forecast, isLoadingWeather = false, weatherError = null) }
            },
            onFailure = { error ->
                updateState {
                    it.copy(
                        isLoadingWeather = false,
                        weatherError = error.message ?: "Error al cargar el clima",
                    )
                }
            },
        )
    }
}
