package com.francisco.weather.feature.dashboard.presentation

import com.francisco.weather.core.bloc.BaseEvent

sealed class DashboardEvent : BaseEvent {
    data class LoadCurrentWeather(val locationQuery: String) : DashboardEvent()
    data class LocationPermissionResult(val granted: Boolean) : DashboardEvent()
    data class GpsStateChanged(val enabled: Boolean) : DashboardEvent()
    data object ClearRecents : DashboardEvent()
    /** @param force bypass the 5-minute TTL guard so weather re-fetches immediately (e.g. on language change). */
    data class GetRemoteStadiums(val force: Boolean = false) : DashboardEvent()
}
