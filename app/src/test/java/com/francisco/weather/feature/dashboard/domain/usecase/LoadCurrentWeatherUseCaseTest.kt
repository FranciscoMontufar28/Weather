package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.dashboard.domain.LocationProvider
import com.francisco.weather.feature.dashboard.domain.model.Coordinates
import com.francisco.weather.feature.forecast.domain.ForecastRepository
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoadCurrentWeatherUseCaseTest {

    private lateinit var forecastRepository: ForecastRepository
    private lateinit var cacheRepository: CachedWeatherRepository
    private lateinit var locationProvider: LocationProvider
    private lateinit var useCase: LoadCurrentWeatherUseCase

    private val sampleForecast = mockk<ForecastData>(relaxed = true)

    @Before
    fun setUp() {
        forecastRepository = mockk()
        cacheRepository = mockk(relaxed = true)
        locationProvider = mockk()
        useCase = LoadCurrentWeatherUseCase(forecastRepository, cacheRepository, locationProvider)
    }

    @Test
    fun `GPS fix available — queries with lat,lon and returns non-approximate result`() = runTest {
        coEvery { locationProvider.currentLocation() } returns Coordinates(40.0, -74.0)
        coEvery { forecastRepository.getForecast("40.0,-74.0") } returns Result.success(sampleForecast)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertFalse(result.getOrThrow().isApproximate)
        coVerify(exactly = 1) { cacheRepository.save(sampleForecast) }
    }

    @Test
    fun `no GPS fix — queries with auto-ip and returns approximate result`() = runTest {
        coEvery { locationProvider.currentLocation() } returns null
        coEvery { forecastRepository.getForecast("auto:ip") } returns Result.success(sampleForecast)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isApproximate)
        coVerify(exactly = 1) { cacheRepository.save(sampleForecast) }
    }

    @Test
    fun `failure — returns Result failure and does not save to cache`() = runTest {
        coEvery { locationProvider.currentLocation() } returns null
        coEvery { forecastRepository.getForecast("auto:ip") } returns Result.failure(RuntimeException("net error"))

        val result = useCase()

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { cacheRepository.save(any()) }
    }
}
