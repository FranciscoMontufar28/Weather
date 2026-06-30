package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.R
import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.core.network.toErrorRes
import timber.log.Timber
import com.francisco.weather.feature.dashboard.domain.usecase.LoadCurrentWeatherUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class LoadCurrentWeatherBloc(
    private val loadCurrentWeather: LoadCurrentWeatherUseCase,
) : BaseBloc<DashboardEvent.LoadCurrentWeather, DashboardState>() {

    override suspend fun handleEvent(
        event: DashboardEvent.LoadCurrentWeather,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        updateState { it.copy(isLoadingWeather = true, weatherErrorRes = null) }
        loadCurrentWeather().fold(
            onSuccess = { resolved ->
                updateState { it.copy(isApproxLocation = resolved.isApproximate) }
            },
            onFailure = { error ->
                Timber.e(error, "Failed to load current weather")
                updateState {
                    it.copy(
                        isLoadingWeather = false,
                        weatherErrorRes  = error.toErrorRes(R.string.error_weather_load),
                    )
                }
            },
        )
    }
}
