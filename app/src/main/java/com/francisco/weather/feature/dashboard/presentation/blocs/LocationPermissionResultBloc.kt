package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

/**
 * Updates permission flags when the user grants or denies ACCESS_FINE_LOCATION.
 *
 * Weather loading is NOT triggered here. The merged LaunchedEffect in DashboardScreen
 * observes [DashboardState.locationPermissionGranted] and re-fires [DashboardEvent.LoadCurrentWeather],
 * which lets the use case decide GPS vs IP without any string constants in the presentation layer.
 */
class LocationPermissionResultBloc : BaseBloc<DashboardEvent.LocationPermissionResult, DashboardState>() {

    override val tag = "LocationPermissionResultBloc"

    override suspend fun handleEvent(
        event: DashboardEvent.LocationPermissionResult,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        updateState { st ->
            st.copy(
                locationPermissionGranted = event.granted,
                locationResolved          = true,
            )
        }
    }
}
