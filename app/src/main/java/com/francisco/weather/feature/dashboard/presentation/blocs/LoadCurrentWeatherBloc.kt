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
        Log.d("magnus", "loadWeather START")
        updateState { it.copy(isLoadingWeather = true, weatherErrorRes = null) }
        loadCurrentWeather().fold(
            onSuccess = { resolved ->
                Log.d("magnus", "loadWeather SUCCESS → location=${resolved.forecast.locationName}, isApprox=${resolved.isApproximate}")
                updateState { it.copy(isApproxLocation = resolved.isApproximate) }
            },
            onFailure = { error ->
                Log.d("magnus", "loadWeather FAILURE error=${error.message}")
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
