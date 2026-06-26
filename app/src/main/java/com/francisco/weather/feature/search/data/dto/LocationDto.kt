package com.francisco.weather.feature.search.data.dto

import com.francisco.weather.feature.search.domain.model.Location
import kotlinx.serialization.Serializable

@Serializable
data class LocationDto(
    val id: Int,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String,
)

fun LocationDto.toDomain(): Location = Location(
    id = id,
    name = name,
    region = region,
    country = country,
)
