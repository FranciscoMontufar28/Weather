package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.forecast.domain.ForecastRepository
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoadCurrentWeatherUseCaseTest {

    private lateinit var forecastRepository: ForecastRepository
    private lateinit var cacheRepository: CachedWeatherRepository
    private lateinit var useCase: LoadCurrentWeatherUseCase

    private val sampleForecast = mockk<ForecastData>(relaxed = true)

    @Before
    fun setUp() {
        forecastRepository = mockk()
        cacheRepository = mockk(relaxed = true)
        useCase = LoadCurrentWeatherUseCase(forecastRepository, cacheRepository)
    }

    @Test
    fun `success — returns Result success and saves to cache`() = runTest {
        coEvery { forecastRepository.getForecast("Bogota") } returns Result.success(sampleForecast)

        val result = useCase("Bogota")

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { cacheRepository.save(sampleForecast) }
    }

    @Test
    fun `failure — returns Result failure and does not save to cache`() = runTest {
        coEvery { forecastRepository.getForecast("bad") } returns Result.failure(RuntimeException("net error"))

        val result = useCase("bad")

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { cacheRepository.save(any()) }
    }
}
