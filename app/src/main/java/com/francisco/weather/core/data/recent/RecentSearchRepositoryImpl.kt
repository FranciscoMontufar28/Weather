package com.francisco.weather.core.data.recent

import com.francisco.weather.core.data.local.recent.RecentSearchDao
import com.francisco.weather.core.data.local.recent.RecentSearchEntity
import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.core.domain.recent.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentSearchRepositoryImpl @Inject constructor(
    private val dao: RecentSearchDao,
) : RecentSearchRepository {

    override fun observeRecent(): Flow<List<RecentSearch>> =
        dao.observeRecent().map { entities -> entities.map { it.toDomain() } }

    override suspend fun add(name: String, region: String, country: String) {
        dao.upsert(
            RecentSearchEntity(
                name = name,
                region = region,
                country = country,
                savedAt = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun clear() {
        dao.clearAll()
    }

    private fun RecentSearchEntity.toDomain() = RecentSearch(
        name = name,
        region = region,
        country = country,
    )
}
