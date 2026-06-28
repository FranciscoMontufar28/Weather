package com.francisco.weather.feature.dashboard.domain.usecase

import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.core.domain.recent.RecentSearchRepository
import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class ObserveDashboardUseCaseTest {

    private lateinit var recentSearchRepository: RecentSearchRepository
    private lateinit var stadiumRepository: StadiumRepository
    private lateinit var cachedWeatherRepository: CachedWeatherRepository
    private lateinit var useCase: ObserveDashboardUseCase

    private val sampleRecents = listOf(
        RecentSearch(name = "Bogotá", region = "Bogota D.C.", country = "Colombia"),
    )
    private val sampleStadiums = listOf(mockk<WorldCupStadium>(relaxed = true))
    private val sampleForecast = mockk<ForecastData>(relaxed = true)

    @Before
    fun setUp() {
        recentSearchRepository = mockk()
        stadiumRepository = mockk()
        cachedWeatherRepository = mockk()
        useCase = ObserveDashboardUseCase(recentSearchRepository, stadiumRepository, cachedWeatherRepository)
    }

    @Test
    fun `combines all three sources into a single DashboardData emission`() = runTest {
        every { recentSearchRepository.observeRecent() } returns flowOf(sampleRecents)
        every { stadiumRepository.observeStadiums() } returns flowOf(sampleStadiums)
        every { cachedWeatherRepository.observeCached() } returns flowOf(sampleForecast)

        val result = useCase().first()

        assertEquals(sampleRecents, result.recentSearches)
        assertEquals(sampleStadiums, result.stadiums)
        assertEquals(sampleForecast, result.cachedWeather)
    }

    @Test
    fun `cachedWeather is null when cache is empty`() = runTest {
        every { recentSearchRepository.observeRecent() } returns flowOf(emptyList())
        every { stadiumRepository.observeStadiums() } returns flowOf(emptyList())
        every { cachedWeatherRepository.observeCached() } returns flowOf(null)

        val result = useCase().first()

        assertNull(result.cachedWeather)
        assertEquals(emptyList<RecentSearch>(), result.recentSearches)
    }
}
