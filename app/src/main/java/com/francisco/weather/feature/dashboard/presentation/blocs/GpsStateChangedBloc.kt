package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.core.bloc.BaseBloc
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState

class GpsStateChangedBloc : BaseBloc<DashboardEvent.GpsStateChanged, DashboardState>() {

    override suspend fun handleEvent(
        event: DashboardEvent.GpsStateChanged,
        updateState: suspend ((DashboardState) -> DashboardState) -> Unit,
    ) {
        updateState { it.copy(isGpsEnabled = event.enabled) }
    }
}
