package com.francisco.weather.feature.dashboard.presentation

import com.francisco.weather.core.bloc.BaseEvent

sealed class DashboardEvent : BaseEvent {
    data object LoadCurrentWeather : DashboardEvent()
    data class LocationPermissionResult(val granted: Boolean) : DashboardEvent()
    data class GpsStateChanged(val enabled: Boolean) : DashboardEvent()
    data object ClearRecents : DashboardEvent()
    data object GetRemoteStadiums : DashboardEvent()
}
