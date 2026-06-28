package com.francisco.weather.feature.dashboard.presentation.blocs

import android.util.Log
import com.francisco.weather.R
import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.core.network.toErrorRes
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
    Log.d("magnus", "loadWeather START query=$locationQuery")
    updateState { it.copy(isLoadingWeather = true, weatherErrorRes = null) }
    val isIpFallback = locationQuery == "auto:ip"
    loadCurrentWeather(locationQuery).fold(
        onSuccess = { forecast ->
            Log.d("magnus", "loadWeather SUCCESS → location=${forecast.locationName}, region=${forecast.region}, isIpFallback=$isIpFallback")
            updateState { it.copy(isApproxLocation = isIpFallback) }
        },
        onFailure = { error ->
            Log.d("magnus", "loadWeather FAILURE query=$locationQuery error=${error.message}")
            updateState {
                it.copy(
                    isLoadingWeather = false,
                    weatherErrorRes  = error.toErrorRes(R.string.error_weather_load),
                )
            }
        },
    )
}
