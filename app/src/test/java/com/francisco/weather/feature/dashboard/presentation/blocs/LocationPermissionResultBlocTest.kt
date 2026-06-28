package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocationPermissionResultBlocTest {

    private lateinit var bloc: LocationPermissionResultBloc

    @Before
    fun setUp() {
        bloc = LocationPermissionResultBloc()
    }

    @Test
    fun `granted true — sets locationPermissionGranted true and locationResolved`() = runTest {
        var state = DashboardState(isApproxLocation = true)
        bloc.handleEvent(
            event = DashboardEvent.LocationPermissionResult(granted = true),
            updateState = { reducer -> state = reducer(state) },
        )

        assertTrue(state.locationPermissionGranted)
        assertTrue(state.locationResolved)
    }

    @Test
    fun `granted false — sets locationPermissionGranted false and locationResolved`() = runTest {
        var state = DashboardState()
        bloc.handleEvent(
            event = DashboardEvent.LocationPermissionResult(granted = false),
            updateState = { reducer -> state = reducer(state) },
        )

        assertFalse(state.locationPermissionGranted)
        assertTrue(state.locationResolved)
        // isApproxLocation is now owned by LoadCurrentWeatherBloc via ResolvedWeather.isApproximate.
        // This bloc only sets permission flags; weather re-fetch is triggered by the screen effect.
        assertFalse(state.isApproxLocation)
    }

    @Test
    fun `does not modify currentWeather or isLoadingWeather`() = runTest {
        val existingForecast = mockk<ForecastData>(relaxed = true)
        var state = DashboardState(currentWeather = existingForecast, isLoadingWeather = true)
        bloc.handleEvent(
            event = DashboardEvent.LocationPermissionResult(granted = false),
            updateState = { reducer -> state = reducer(state) },
        )

        // Bloc only owns permission flags — weather state is owned by the use case / observer
        assertTrue(state.currentWeather === existingForecast)
        assertTrue(state.isLoadingWeather)
        assertFalse(state.locationPermissionGranted)
        assertTrue(state.locationResolved)
    }
}
