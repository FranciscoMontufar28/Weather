package com.francisco.weather.feature.dashboard.presentation

import com.francisco.weather.core.bloc.BlocViewModel
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    override val factory: DashboardBlocFactory,
) : BlocViewModel<DashboardEvent, DashboardState>(DashboardState()) {

    init {
        onEvent(DashboardEvent.ObserveRecents)
        onEvent(DashboardEvent.ObserveStadiums)
        onEvent(DashboardEvent.ObserveCachedWeather)
        onEvent(DashboardEvent.GetRemoteStadiums)
    }
}
