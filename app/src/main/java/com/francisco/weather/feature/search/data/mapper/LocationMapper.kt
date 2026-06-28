package com.francisco.weather.feature.search.data.mapper

import com.francisco.weather.feature.search.data.dto.LocationDto
import com.francisco.weather.feature.search.domain.model.Location

fun LocationDto.toDomain(): Location = Location(
    id = id,
    name = name,
    region = region,
    country = country,
)
