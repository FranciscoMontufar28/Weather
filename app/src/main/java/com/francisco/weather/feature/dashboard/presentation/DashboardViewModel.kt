package com.francisco.weather.feature.dashboard.presentation

import androidx.lifecycle.viewModelScope
import com.francisco.weather.core.bloc.BlocViewModel
import com.francisco.weather.feature.dashboard.domain.usecase.ObserveDashboardUseCase
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    override val factory: DashboardBlocFactory,
    private val observeDashboard: ObserveDashboardUseCase,
) : BlocViewModel<DashboardEvent, DashboardState>(DashboardState()) {

    init {
        // Single reactive pipeline: combines recents + stadiums + cachedWeather into one
        // Flow<DashboardData>. The DB is the single source of truth; every write (search,
        // sync, weather load) triggers this collector automatically.
        viewModelScope.launch {
            observeDashboard().collect { data ->
                safeUpdateState { state ->
                    state.copy(
                        recentSearches = data.recentSearches,
                        stadiums       = data.stadiums.filter { it.matchName != null },
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
        onEvent(DashboardEvent.GetRemoteStadiums)
    }
}
