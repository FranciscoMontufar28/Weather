package com.francisco.weather.feature.dashboard.presentation

import com.francisco.weather.core.bloc.BaseState
import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import com.francisco.weather.feature.forecast.domain.model.ForecastData

data class DashboardState(
    val currentWeather: ForecastData? = null,
    val isLoadingWeather: Boolean = false,
    val weatherError: String? = null,
    val locationPermissionGranted: Boolean = false,
    val locationResolved: Boolean = false,
    val isGpsEnabled: Boolean = false,
    val recentSearches: List<RecentSearch> = emptyList(),
    val stadiums: List<WorldCupStadium> = emptyList(),
    val isLoadingStadiums: Boolean = false,
    /** True when location was resolved via IP (permission denied / GPS unavailable). */
    val isApproxLocation: Boolean = false,
) : BaseState
