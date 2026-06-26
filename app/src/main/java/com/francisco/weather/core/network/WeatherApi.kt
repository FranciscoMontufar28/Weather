package com.francisco.weather.core.network

import com.francisco.weather.feature.forecast.data.dto.ForecastResponseDto
import com.francisco.weather.feature.search.data.dto.LocationDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("search.json")
    suspend fun search(@Query("q") query: String): List<LocationDto>

    @GET("forecast.json")
    suspend fun forecast(
        @Query("q") location: String,
        @Query("days") days: Int = 3,
    ): ForecastResponseDto
}
