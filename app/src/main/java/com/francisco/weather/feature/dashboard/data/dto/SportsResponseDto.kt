package com.francisco.weather.feature.dashboard.data.dto

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class SportsResponseDto(
    val football: List<SportEventDto> = emptyList(),
) {
    fun nextEvent(): SportEventDto? {
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val now = LocalDateTime.now()
        return football
            .filter {
                runCatching { LocalDateTime.parse(it.start, fmt) >= now }.getOrDefault(true)
            }
            .minByOrNull {
                runCatching { LocalDateTime.parse(it.start, fmt) }
                    .getOrDefault(LocalDateTime.MAX)
            }
    }
}

@Serializable
data class SportEventDto(
    val stadium: String = "",
    val country: String = "",
    val region: String = "",
    val tournament: String = "",
    val start: String = "",   // "yyyy-MM-dd HH:mm"
    val match: String = "",
)
