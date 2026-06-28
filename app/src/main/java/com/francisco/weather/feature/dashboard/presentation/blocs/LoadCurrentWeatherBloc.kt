package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.domain.usecase.LoadCurrentWeatherUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class LoadCurrentWeatherBloc(
    private val loadCurrentWeather: LoadCurrentWeatherUseCase,
) : BaseBloc<DashboardEvent.LoadCurrentWeather, DashboardState>() {

    override val tag = "LoadCurrentWeatherBloc"

    override suspend fun handleEvent(
        event: DashboardEvent.LoadCurrentWeather,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        loadWeatherInto(event.locationQuery, loadCurrentWeather, updateState)
    }
}

internal suspend fun loadWeatherInto(
    locationQuery: String,
    loadCurrentWeather: LoadCurrentWeatherUseCase,
    updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
) {
    updateState { it.copy(isLoadingWeather = true, weatherError = null) }
    val isIpFallback = locationQuery == "auto:ip"
    loadCurrentWeather(locationQuery).fold(
        onSuccess = { forecast ->
            updateState {
                it.copy(
                    currentWeather = forecast,
                    isLoadingWeather = false,
                    weatherError = null,
                    isApproxLocation = isIpFallback,
                )
            }
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
