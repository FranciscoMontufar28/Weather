package com.francisco.weather.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WeatherDestinations {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val SEARCH = "search"
    const val FORECAST = "forecast/{locationQuery}"

    fun forecastRoute(locationQuery: String): String {
        val encoded = URLEncoder.encode(locationQuery, StandardCharsets.UTF_8.toString())
        return "forecast/$encoded"
    }
}
