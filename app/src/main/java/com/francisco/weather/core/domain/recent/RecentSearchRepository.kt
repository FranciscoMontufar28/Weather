package com.francisco.weather.core.domain.recent

import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    fun observeRecent(): Flow<List<RecentSearch>>
    suspend fun add(name: String, region: String, country: String)
    suspend fun clear()
}
