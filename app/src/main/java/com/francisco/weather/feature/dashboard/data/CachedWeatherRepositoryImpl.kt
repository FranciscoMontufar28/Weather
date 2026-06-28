package com.francisco.weather.feature.dashboard.data

import android.util.Log
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
            }.also { forecast ->
                Log.d("magnus", "observeCached EMIT → location=${forecast?.locationName ?: "null"}, region=${forecast?.region ?: "null"}")
            }
        }

    override suspend fun save(data: ForecastData) {
        Log.d("magnus", "cache SAVE → location=${data.locationName}, region=${data.region}")
        dao.upsert(
            CachedWeatherEntity(
                json = json.encodeToString(data),
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }
}
