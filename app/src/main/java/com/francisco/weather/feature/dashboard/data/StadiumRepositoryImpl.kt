package com.francisco.weather.feature.dashboard.data

import com.francisco.weather.core.data.local.stadium.StadiumDao
import com.francisco.weather.core.network.WeatherApi
import com.francisco.weather.feature.dashboard.data.mapper.toBaseEntity
import com.francisco.weather.feature.dashboard.data.mapper.toDomain
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
                    val event = runCatching { api.sports(latLonQuery).nextEvent() }
                        .getOrNull()
                        ?: runCatching { api.sports(stadium.city).nextEvent() }
                            .getOrNull()

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
            }.awaitAll()
        }
    }

}
