package com.francisco.weather.feature.dashboard.presentation.blocs

import com.francisco.weather.feature.dashboard.domain.usecase.LoadCurrentWeatherUseCase
import com.francisco.weather.feature.dashboard.presentation.DashboardEvent
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
    fun `success updates state with forecast and clears loading`() = runTest {
        coEvery { loadCurrentWeather("Bogota") } returns Result.success(sampleForecast)

        var state = DashboardState()
        bloc.handleEvent(
            event = DashboardEvent.LoadCurrentWeather("Bogota"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertEquals(sampleForecast, state.currentWeather)
        assertFalse(state.isLoadingWeather)
        assertNull(state.weatherError)
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
        assertEquals(sampleForecast, state.currentWeather)
        assertFalse(state.isLoadingWeather)
    }

    @Test
    fun `failure sets weatherError and clears loading`() = runTest {
        coEvery { loadCurrentWeather(any()) } returns Result.failure(RuntimeException("timeout"))

        var state = DashboardState()
        bloc.handleEvent(
            event = DashboardEvent.LoadCurrentWeather("Bogota"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertFalse(state.isLoadingWeather)
        assertNull(state.currentWeather)
        assertNotNull(state.weatherError)
        assertTrue(state.weatherError!!.isNotBlank())
    }
}
