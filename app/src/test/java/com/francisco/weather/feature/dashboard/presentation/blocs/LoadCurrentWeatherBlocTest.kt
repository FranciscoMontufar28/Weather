package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.feature.dashboard.domain.usecase.LoadCurrentWeatherUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoadCurrentWeatherBlocTest {

    private lateinit var loadCurrentWeather: LoadCurrentWeatherUseCase
    private lateinit var bloc: LoadCurrentWeatherBloc

    private val sampleForecast = mockk<ForecastData>(relaxed = true)

    @Before
    fun setUp() {
        loadCurrentWeather = mockk()
        bloc = LoadCurrentWeatherBloc(loadCurrentWeather)
    }

    @Test
    fun `success — bloc does NOT set currentWeather (observer owns it) and keeps isLoadingWeather`() = runTest {
        coEvery { loadCurrentWeather("Bogota") } returns Result.success(sampleForecast)

        var state = DashboardState()
        bloc.handleEvent(
            event = DashboardEvent.LoadCurrentWeather("Bogota"),
            updateState = { reducer -> state = reducer(state) },
        )

        // currentWeather is written exclusively by the observeCachedWeather() collector —
        // the bloc must NOT touch it.
        assertNull(state.currentWeather)
        // isLoadingWeather is cleared by the observer when Room emits the saved data,
        // not by the load bloc on success.
        assertTrue(state.isLoadingWeather)
        assertNull(state.weatherErrorRes)
        assertFalse(state.isApproxLocation)
    }

    @Test
    fun `auto ip query sets isApproxLocation true on success`() = runTest {
        coEvery { loadCurrentWeather("auto:ip") } returns Result.success(sampleForecast)

        var state = DashboardState()
        bloc.handleEvent(
            event = DashboardEvent.LoadCurrentWeather("auto:ip"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertTrue(state.isApproxLocation)
        // currentWeather still null — the observer will set it once Room emits
        assertNull(state.currentWeather)
        assertTrue(state.isLoadingWeather)
    }

    @Test
    fun `failure sets weatherErrorRes and clears loading`() = runTest {
        coEvery { loadCurrentWeather(any()) } returns Result.failure(RuntimeException("timeout"))

        var state = DashboardState()
        bloc.handleEvent(
            event = DashboardEvent.LoadCurrentWeather("Bogota"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertFalse(state.isLoadingWeather)
        assertNull(state.currentWeather)
        assertNotNull(state.weatherErrorRes)
    }
}
