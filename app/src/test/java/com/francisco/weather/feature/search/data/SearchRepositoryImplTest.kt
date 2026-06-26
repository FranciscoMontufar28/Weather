package com.francisco.weather.feature.search.data

import com.francisco.weather.core.network.WeatherApi
import com.francisco.weather.core.network.WeatherError
import com.francisco.weather.feature.search.data.dto.LocationDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SearchRepositoryImplTest {

    private lateinit var api: WeatherApi
    private lateinit var repository: SearchRepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        repository = SearchRepositoryImpl(api)
    }

    @Test
    fun `search returns mapped domain locations on success`() = runTest {
        val dtos = listOf(
            LocationDto(id = 1, name = "Bogotá", region = "Bogota D.C.", country = "Colombia", lat = 4.6, lon = -74.08, url = "bogota"),
            LocationDto(id = 2, name = "Medellín", region = "Antioquia", country = "Colombia", lat = 6.25, lon = -75.56, url = "medellin"),
        )
        coEvery { api.search("Col") } returns dtos

        val result = repository.search("Col")

        assertTrue(result.isSuccess)
        val locations = result.getOrThrow()
        assertEquals(2, locations.size)
        assertEquals("Bogotá", locations[0].name)
        assertEquals("Colombia", locations[0].country)
        assertEquals("Antioquia", locations[1].region)
    }

    @Test
    fun `search returns WeatherError_Empty when API returns empty list`() = runTest {
        coEvery { api.search("xyz123abc") } returns emptyList()

        val result = repository.search("xyz123abc")

        assertTrue(result.isFailure)
        assertTrue("Expected WeatherError.Empty", result.exceptionOrNull() is WeatherError.Empty)
    }

    @Test
    fun `search returns WeatherError_Network on IOException`() = runTest {
        coEvery { api.search(any()) } throws IOException("No connection")

        val result = repository.search("London")

        assertTrue(result.isFailure)
        assertTrue("Expected WeatherError.Network", result.exceptionOrNull() is WeatherError.Network)
    }

    @Test
    fun `search returns WeatherError_Unknown on unexpected exception`() = runTest {
        coEvery { api.search(any()) } throws RuntimeException("Unexpected")

        val result = repository.search("London")

        assertTrue(result.isFailure)
        assertTrue("Expected WeatherError.Unknown", result.exceptionOrNull() is WeatherError.Unknown)
    }

    @Test
    fun `search error message is non-null`() = runTest {
        coEvery { api.search(any()) } throws IOException("Timeout")

        val result = repository.search("London")

        val error = result.exceptionOrNull()
        assertTrue(error is WeatherError.Network)
        assertNull(result.getOrNull())
    }
}
