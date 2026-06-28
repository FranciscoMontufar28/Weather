package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.domain.usecase.ObserveCachedWeatherUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class ObserveCachedWeatherBloc(
    private val observeCachedWeather: ObserveCachedWeatherUseCase,
) : BaseBloc<DashboardEvent.ObserveCachedWeather, DashboardState>() {

    override val tag = "ObserveCachedWeatherBloc"

    override suspend fun handleEvent(
        event: DashboardEvent.ObserveCachedWeather,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        observeCachedWeather().collect { cached ->
            updateState { state ->
                if (state.currentWeather == null && cached != null) {
                    // Seed UI with cache; mark approx if permission was already resolved as denied
                    val approx = state.isApproxLocation ||
                        (state.locationResolved && !state.locationPermissionGranted)
                    state.copy(currentWeather = cached, isApproxLocation = approx)
                } else {
                    state
                }
            }
        }
    }
}
