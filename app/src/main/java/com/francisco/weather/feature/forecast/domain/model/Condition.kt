package com.francisco.weather.feature.forecast.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Condition(
    val text: String,
    val iconUrl: String,
    val code: Int,
)
