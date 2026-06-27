package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

internal val DATE_PARSER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

internal fun String.toDayLabel(): String = try {
    val date = LocalDate.parse(this, DATE_PARSER)
    val today = LocalDate.now()
    when (date) {
        today -> "Hoy"
        today.plusDays(1) -> "Mañana"
        else -> date.format(DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.forLanguageTag("es")))
            .replaceFirstChar { it.uppercaseChar() }
    }
} catch (_: Exception) { this }

internal fun String.toShortDayLabel(): String = try {
    val date = LocalDate.parse(this, DATE_PARSER)
    when (date) {
        LocalDate.now() -> "Today"
        else -> date.format(DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH))
    }
} catch (_: Exception) { this }

internal fun uvColor(uv: Double): Color = when {
    uv <= 2  -> Color(0xFF66BB6A)
    uv <= 5  -> Color(0xFFFFEE58)
    uv <= 7  -> Color(0xFFFFA726)
    uv <= 10 -> Color(0xFFEF5350)
    else     -> Color(0xFFAB47BC)
}
