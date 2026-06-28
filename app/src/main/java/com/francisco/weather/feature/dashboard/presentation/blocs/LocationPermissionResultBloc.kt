package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.domain.usecase.LoadCurrentWeatherUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class LocationPermissionResultBloc(
    private val loadCurrentWeather: LoadCurrentWeatherUseCase,
) : BaseBloc<DashboardEvent.LocationPermissionResult, DashboardState>() {

    override val tag = "LocationPermissionResultBloc"

    override suspend fun handleEvent(
        event: DashboardEvent.LocationPermissionResult,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        var shouldLoadIp = false
        updateState { st ->
            shouldLoadIp = !event.granted && st.currentWeather == null && !st.isLoadingWeather
            st.copy(
                locationPermissionGranted = event.granted,
                locationResolved = true,
                isApproxLocation = when {
                    event.granted -> false
                    st.currentWeather != null -> true
                    else -> st.isApproxLocation
                },
            )
        }
        if (shouldLoadIp) loadWeatherInto("auto:ip", loadCurrentWeather, updateState)
    }
}
