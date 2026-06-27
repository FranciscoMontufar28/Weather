package com.francisco.weather.feature.dashboard.presentation

import androidx.lifecycle.viewModelScope
import com.francisco.weather.core.bloc.BlocViewModel
import com.francisco.weather.core.domain.recent.RecentSearchRepository
import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    override val factory: DashboardBlocFactory,
    private val recentRepository: RecentSearchRepository,
    private val stadiumRepository: StadiumRepository,
    private val cachedWeatherRepository: CachedWeatherRepository,
) : BlocViewModel<DashboardEvent, DashboardState>(DashboardState()) {

    init {
        viewModelScope.launch {
            recentRepository.observeRecent().collect { recents ->
                safeUpdateState { it.copy(recentSearches = recents) }
            }
        }

        // Always read stadiums from local (local-first); syncs after GetRemoteStadiums event
        viewModelScope.launch {
            stadiumRepository.observeStadiums().collect { stadiums ->
                safeUpdateState { it.copy(stadiums = stadiums) }
            }
        }

        // Seed UI with cached weather immediately; won't overwrite a fresh remote result
        viewModelScope.launch {
            cachedWeatherRepository.observeCached().collect { cached ->
                safeUpdateState { state ->
                    if (state.currentWeather == null && cached != null) {
                        // If permission was already resolved as denied when cache emits, mark approx
                        val approx = state.isApproxLocation ||
                            (state.locationResolved && !state.locationPermissionGranted)
                        state.copy(currentWeather = cached, isApproxLocation = approx)
                    } else {
                        state
                    }
                }
            }
        }

        // Trigger "backend" sync on launch — list arrives via Room Flow above
        onEvent(DashboardEvent.GetRemoteStadiums)
    }

    override fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.LocationPermissionResult -> viewModelScope.launch {
                val st = _state.value
                safeUpdateState {
                    it.copy(
                        locationPermissionGranted = event.granted,
                        locationResolved = true,
                        isApproxLocation = when {
                            event.granted -> false
                            st.currentWeather != null -> true
                            else -> it.isApproxLocation
                        },
                    )
                }
                if (!event.granted && st.currentWeather == null && !st.isLoadingWeather) {
                    safeUpdateState { it.copy(isLoadingWeather = true) }
                    onEvent(DashboardEvent.LoadCurrentWeather("auto:ip"))
                }
            }
            is DashboardEvent.GpsStateChanged -> viewModelScope.launch {
                safeUpdateState { it.copy(isGpsEnabled = event.enabled) }
            }
            else -> super.onEvent(event)
        }
    }
}
