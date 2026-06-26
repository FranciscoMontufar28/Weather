package com.francisco.weather.feature.dashboard.domain.model

data class WorldCupStadium(
    val name: String,
    val city: String,
    val country: String,
    val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String,
    // Weather — null until first sync
    val tempC: Double? = null,
    val conditionText: String? = null,
    val conditionIconUrl: String? = null,
    // Sports — null until first sync (or no upcoming match)
    val matchName: String? = null,
    val matchTournament: String? = null,
    val matchStart: String? = null,
)
