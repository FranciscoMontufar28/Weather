package com.francisco.weather.feature.dashboard.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.francisco.weather.core.bloc.BlocViewModel
import com.francisco.weather.feature.dashboard.domain.usecase.ObserveDashboardUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    override val factory: DashboardBlocFactory,
    private val observeDashboard: ObserveDashboardUseCase,
) : BlocViewModel<DashboardEvent, DashboardState>(DashboardState()) {

    // Survives configuration changes (ViewModel lifetime). Set once the GPS location is
    // resolved; the language-change LaunchedEffect uses it to re-fetch in the new locale.
    // Null means no GPS fix yet (IP-fallback path or no permission).
    var resolvedLocationQuery: String? = null
        private set

    fun onLocationResolved(query: String) {
        Log.d("magnus", "onLocationResolved → $query")
        resolvedLocationQuery = query
    }

    init {
        // Single reactive pipeline: combines recents + stadiums + cachedWeather into one
        // Flow<DashboardData>. The DB is the single source of truth; every write (search,
        // sync, weather load) triggers this collector automatically.
        viewModelScope.launch {
            observeDashboard().collect { data ->
                val withMatch = data.stadiums.count { it.matchName != null }
                Log.d("magnus", "observeDashboard EMIT → weather=${data.cachedWeather?.locationName ?: "null"} (${data.cachedWeather?.region ?: ""}), stadiums=${data.stadiums.size} (withMatch=$withMatch), recents=${data.recentSearches.size}")
                safeUpdateState { state ->
                    state.copy(
                        recentSearches = data.recentSearches,
                        stadiums       = data.stadiums,
                        currentWeather = data.cachedWeather,
                        // The observer is the sole writer of currentWeather and the sole
                        // one that clears the spinner — but only when real data arrives,
                        // to avoid flashing EnableLocationPrompt on an empty-cache start.
                        isLoadingWeather = if (data.cachedWeather != null) false else state.isLoadingWeather,
                    )
                }
            }
        }
        // One-shot sync: API → writes DB → ends. The result surfaces via observeDashboard().
        // Runs once per ViewModel lifetime (init is not re-invoked on config change).
        onEvent(DashboardEvent.GetRemoteStadiums())
    }
}
