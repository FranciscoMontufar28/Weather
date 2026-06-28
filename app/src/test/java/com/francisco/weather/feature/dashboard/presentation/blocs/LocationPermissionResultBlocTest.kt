package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.feature.dashboard.domain.usecase.LoadCurrentWeatherUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocationPermissionResultBlocTest {

    private lateinit var loadCurrentWeather: LoadCurrentWeatherUseCase
    private lateinit var bloc: LocationPermissionResultBloc

    private val sampleForecast = mockk<ForecastData>(relaxed = true)

    @Before
    fun setUp() {
        loadCurrentWeather = mockk()
        bloc = LocationPermissionResultBloc(loadCurrentWeather)
    }

    @Test
    fun `granted true sets permission, clears isApproxLocation, no IP load`() = runTest {
        var state = DashboardState(isApproxLocation = true)
        bloc.handleEvent(
            event = DashboardEvent.LocationPermissionResult(granted = true),
            updateState = { reducer -> state = reducer(state) },
        )

        assertTrue(state.locationPermissionGranted)
        assertTrue(state.locationResolved)
        assertFalse(state.isApproxLocation)
        coVerify(exactly = 0) { loadCurrentWeather(any()) }
    }

    @Test
    fun `granted false with no weather triggers IP fallback load`() = runTest {
        coEvery { loadCurrentWeather("auto:ip") } returns Result.success(sampleForecast)

        var state = DashboardState()
        bloc.handleEvent(
            event = DashboardEvent.LocationPermissionResult(granted = false),
            updateState = { reducer -> state = reducer(state) },
        )

        coVerify(exactly = 1) { loadCurrentWeather("auto:ip") }
        assertFalse(state.locationPermissionGranted)
        assertTrue(state.locationResolved)
        assertEquals(sampleForecast, state.currentWeather)
        assertTrue(state.isApproxLocation)
    }

    @Test
    fun `granted false but weather already loaded — no IP fallback, isApprox set true`() = runTest {
        val existingForecast = mockk<ForecastData>(relaxed = true)
        var state = DashboardState(currentWeather = existingForecast)
        bloc.handleEvent(
            event = DashboardEvent.LocationPermissionResult(granted = false),
            updateState = { reducer -> state = reducer(state) },
        )

        coVerify(exactly = 0) { loadCurrentWeather(any()) }
        assertFalse(state.locationPermissionGranted)
        assertTrue(state.locationResolved)
        assertTrue(state.isApproxLocation)
    }
}
