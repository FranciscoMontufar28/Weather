package com.francisco.weather.feature.dashboard.data

import android.util.Log
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

        // Fetch sports for all stadiums concurrently with lat,lon → city fallback.
        // Each call is isolated in its own runCatching so a lat,lon failure still attempts the city.
        coroutineScope {
            base.map { stadium ->
                async {
                    val latLonQuery = "${stadium.latitude},${stadium.longitude}"
                    Log.d("magnus", "sports FETCH [${stadium.name}] query=$latLonQuery")
                    val event = runCatching { api.sports(latLonQuery).nextEvent() }
                        .onFailure { Log.d("magnus", "sports ERROR latLon [${stadium.name}] ${it.message}") }
                        .getOrNull()
                        ?: runCatching { api.sports(stadium.city).nextEvent() }
                            .onFailure { Log.d("magnus", "sports ERROR city [${stadium.name}] ${it.message}") }
                            .getOrNull()

                    if (event != null) {
                        Log.d("magnus", "sports HIT [${stadium.name}] → ${event.match} (${event.tournament})")
                        dao.updateSports(
                            name = stadium.name,
                            matchName = event.match,
                            matchTournament = event.tournament,
                            matchStart = event.start,
                            ts = now,
                        )
                    } else {
                        Log.d("magnus", "sports MISS [${stadium.name}] → no event")
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
