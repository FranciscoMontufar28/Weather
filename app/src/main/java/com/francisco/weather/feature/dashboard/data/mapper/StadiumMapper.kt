package com.francisco.weather.feature.dashboard.data.mapper

import com.francisco.weather.core.data.local.stadium.StadiumEntity
import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium

internal fun StadiumEntity.toDomain() = WorldCupStadium(
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

internal fun WorldCupStadium.toBaseEntity() = StadiumEntity(
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
