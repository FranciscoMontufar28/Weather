package com.francisco.weather.feature.dashboard.data

import com.francisco.weather.core.data.local.stadium.StadiumDao
import com.francisco.weather.core.data.local.stadium.StadiumEntity
import com.francisco.weather.core.network.WeatherApi
import com.francisco.weather.feature.dashboard.domain.StadiumRepository
import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import com.francisco.weather.feature.forecast.domain.ForecastRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val WEATHER_TTL_MS = 5 * 60 * 1000L   // 5 minutes
private const val SPORTS_TTL_MS  = 5 * 60 * 1000L   // 5 minutes

class StadiumRepositoryImpl @Inject constructor(
    private val dao: StadiumDao,
    private val remote: StadiumRemoteDataSource,
    private val forecastRepository: ForecastRepository,
    private val api: WeatherApi,
) : StadiumRepository {

    override fun observeStadiums(): Flow<List<WorldCupStadium>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun syncFromRemote() {
        val base = remote.fetch()

        // Insert base rows without overwriting any cached weather/sports data
        dao.insertAllIgnore(base.map { it.toBaseEntity() })

        val now = System.currentTimeMillis()

        // TTL guard: skip weather fetch if refreshed less than 5 min ago
        val lastWeather = dao.lastWeatherUpdate()
        if (lastWeather == null || now - lastWeather >= WEATHER_TTL_MS) {
            // Fetch weather for all stadiums concurrently
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

        // TTL guard: skip sports fetch if refreshed less than 5 min ago
        val lastSports = dao.lastSportsUpdate()
        if (lastSports == null || now - lastSports >= SPORTS_TTL_MS) {
            // Fetch sports for all stadiums concurrently with lat,lon → city fallback
            coroutineScope {
                base.map { stadium ->
                    async {
                        runCatching {
                            val latLonQuery = "${stadium.latitude},${stadium.longitude}"
                            val event = api.sports(latLonQuery).nextEvent()
                                ?: api.sports(stadium.city).nextEvent()

                            if (event != null) {
                                dao.updateSports(
                                    name = stadium.name,
                                    matchName = event.match,
                                    matchTournament = event.tournament,
                                    matchStart = event.start,
                                    ts = now,
                                )
                            }
                        }
                        // runCatching absorbs individual failures — other stadiums still update
                    }
                }.awaitAll()
            }
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
        matchName = matchName,
        matchTournament = matchTournament,
        matchStart = matchStart,
    )

    private fun WorldCupStadium.toBaseEntity() = StadiumEntity(
        name = name,
        city = city,
        country = country,
        countryCode = countryCode,
        latitude = latitude,
        longitude = longitude,
        imageUrl = imageUrl,
        // Leave weather + sports null — IGNORE strategy preserves existing cached data
        tempC = null,
        conditionText = null,
        conditionIconUrl = null,
        weatherUpdatedAt = null,
        matchName = null,
        matchTournament = null,
        matchStart = null,
        sportsUpdatedAt = null,
    )
}
