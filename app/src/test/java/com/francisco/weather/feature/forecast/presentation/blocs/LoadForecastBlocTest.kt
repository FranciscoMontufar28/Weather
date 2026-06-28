package com.francisco.weather.feature.forecast.presentation.blocs

import com.francisco.weather.core.network.WeatherError
import com.francisco.weather.feature.forecast.domain.model.Astro
import com.francisco.weather.feature.forecast.domain.model.Condition
import com.francisco.weather.feature.forecast.domain.model.CurrentWeather
import com.francisco.weather.feature.forecast.domain.model.DayWeather
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import com.francisco.weather.feature.forecast.domain.usecase.GetForecastUseCase
import com.francisco.weather.feature.forecast.presentation.ForecastEvent
import com.francisco.weather.feature.forecast.presentation.ForecastState
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
import java.io.IOException

class LoadForecastBlocTest {

    private lateinit var getForecast: GetForecastUseCase
    private lateinit var bloc: LoadForecastBloc

    private val sampleForecast = ForecastData(
        locationName = "Bogotá",
        region = "Bogota D.C.",
        country = "Colombia",
        days = listOf(
            DayWeather(
                date = "2025-06-25",
                avgTempC = 14.5,
                maxTempC = 18.0,
                minTempC = 10.0,
                condition = Condition("Nublado", "https://cdn.weatherapi.com/weather/64x64/day/119.png", 1006),
                astro = Astro("05:47 AM", "06:10 PM", "Waxing Gibbous", 93),
            ),
        ),
        current = CurrentWeather(
            tempC = 15.0,
            condition = Condition("Nublado", "https://cdn.weatherapi.com/weather/64x64/day/119.png", 1006),
            humidity = 72,
            windKph = 8.0,
            feelsLikeC = 13.5,
        ),
    )

    @Before
    fun setUp() {
        getForecast = mockk()
        bloc = LoadForecastBloc(getForecast)
    }

    @Test
    fun `successful load updates state with forecast data`() = runTest {
        coEvery { getForecast("Bogota") } returns Result.success(sampleForecast)

        var state = ForecastState()
        bloc.handleEvent(
            event = ForecastEvent.Load("Bogota"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertNotNull(state.forecast)
        assertEquals("Bogotá", state.forecast!!.locationName)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals("Bogota", state.locationQuery)
    }

    @Test
    fun `network failure sets error and clears loading`() = runTest {
        val cause = IOException("timeout")
        coEvery { getForecast(any()) } returns Result.failure(WeatherError.Network(cause))

        var state = ForecastState()
        bloc.handleEvent(
            event = ForecastEvent.Load("Unknown"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertFalse(state.isLoading)
        assertNull(state.forecast)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("conectar", ignoreCase = true))
    }

    @Test
    fun `http error sets error with message`() = runTest {
        coEvery { getForecast(any()) } returns Result.failure(WeatherError.Http(400, "Bad Request"))

        var state = ForecastState()
        bloc.handleEvent(
            event = ForecastEvent.Load("bad"),
            updateState = { reducer -> state = reducer(state) },
        )

        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }
}
