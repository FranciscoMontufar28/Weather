package com.francisco.weather.feature.dashboard.presentation

import com.francisco.weather.core.bloc.BaseEvent

sealed class DashboardEvent : BaseEvent {
    data class LoadCurrentWeather(val locationQuery: String) : DashboardEvent()
    data class LocationPermissionResult(val granted: Boolean) : DashboardEvent()
    data class GpsStateChanged(val enabled: Boolean) : DashboardEvent()
    data object ClearRecents : DashboardEvent()
    data object GetRemoteStadiums : DashboardEvent()
    data object ObserveRecents : DashboardEvent()
    data object ObserveStadiums : DashboardEvent()
    data object ObserveCachedWeather : DashboardEvent()
}
