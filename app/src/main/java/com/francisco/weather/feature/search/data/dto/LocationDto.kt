package com.francisco.weather.feature.search.data.dto

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
