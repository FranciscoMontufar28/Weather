package com.francisco.weather.feature.dashboard.data

import com.francisco.weather.core.data.local.weather.CachedWeatherDao
import com.francisco.weather.core.data.local.weather.CachedWeatherEntity
import com.francisco.weather.feature.dashboard.domain.CachedWeatherRepository
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class CachedWeatherRepositoryImpl @Inject constructor(
    private val dao: CachedWeatherDao,
    private val json: Json,
) : CachedWeatherRepository {

    override fun observeCached(): Flow<ForecastData?> =
        dao.observe().map { entity ->
            entity?.let {
                try {
                    json.decodeFromString<ForecastData>(it.json)
                } catch (_: Exception) {
                    null
                }
            }
        }

    override suspend fun save(data: ForecastData) {
        dao.upsert(
            CachedWeatherEntity(
                json = json.encodeToString(data),
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }
}
