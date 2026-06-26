package com.francisco.weather.feature.search.domain

import com.francisco.weather.feature.search.domain.model.Location

interface SearchRepository {

    /**
     * Returns a list of matching locations for [query].
     * Returns [Result.failure] with a [com.francisco.weather.core.network.WeatherError] on error.
     */
    suspend fun search(query: String): Result<List<Location>>
}
