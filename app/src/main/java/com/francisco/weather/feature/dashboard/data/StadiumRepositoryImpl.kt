package com.francisco.weather.feature.dashboard.data

import com.francisco.weather.core.data.local.stadium.StadiumDao
import com.francisco.weather.core.data.local.stadium.StadiumEntity
import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import com.francisco.weather.feature.forecast.domain.ForecastRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val WEATHER_TTL_MS = 5 * 60 * 1000L // 5 minutes

class StadiumRepositoryImpl @Inject constructor(
    private val dao: StadiumDao,
    private val remote: StadiumRemoteDataSource,
    private val forecastRepository: ForecastRepository,
) : StadiumRepository {

    override fun observeStadiums(): Flow<List<WorldCupStadium>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun syncFromRemote() {
        val base = remote.fetch()

        // Insert base rows without overwriting any cached weather data
        dao.insertAllIgnore(base.map { it.toBaseEntity() })

        // TTL guard: skip weather fetch if refreshed less than 5 min ago
        val lastUpdate = dao.lastWeatherUpdate()
        if (lastUpdate != null && System.currentTimeMillis() - lastUpdate < WEATHER_TTL_MS) return

        // Fetch weather for all stadiums concurrently; each update emits via the Room Flow
        val now = System.currentTimeMillis()
        coroutineScope {
            base.map { stadium ->
                async {
                    forecastRepository
                        .getForecast("${stadium.latitude},${stadium.longitude}")
                        .onSuccess { forecast ->
                            dao.updateWeather(
                                name = stadium.name,
                                tempC = forecast.current.tempC,
                                text = forecast.current.condition.text,
                                icon = forecast.current.condition.iconUrl,
                                ts = now,
                            )
                        }
                }
            }.awaitAll()
        }
    }

    private fun StadiumEntity.toDomain() = WorldCupStadium(
        name = name,
        city = city,
        country = country,
        countryCode = countryCode,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrl,
        tempC = tempC,
        conditionText = conditionText,
        conditionIconUrl = conditionIconUrl,
    )

    private fun WorldCupStadium.toBaseEntity() = StadiumEntity(
        name = name,
        city = city,
        country = country,
        countryCode = countryCode,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrl,
        // Leave weather fields null — IGNORE strategy preserves existing weather if row exists
        tempC = null,
        conditionText = null,
        conditionIconUrl = null,
        weatherUpdatedAt = null,
    )
}
