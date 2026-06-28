package com.francisco.weather.feature.dashboard.domain.model

import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.feature.forecast.domain.model.ForecastData

/** Reactive snapshot of the 3 Room sources that feed the Dashboard screen. */
data class DashboardData(
    val recentSearches: List<RecentSearch>,
    val stadiums: List<WorldCupStadium>,
    val cachedWeather: ForecastData?,
)
