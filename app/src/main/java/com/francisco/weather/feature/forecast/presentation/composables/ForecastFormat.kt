package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

internal val DATE_PARSER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/**
 * Converts an ISO date string ("yyyy-MM-dd") to a human-readable day label.
 *
 * • Today    → locale-aware string ("Today" / "Hoy")
 * • Tomorrow → locale-aware string ("Tomorrow" / "Mañana")
 * • Other    → formatted day name via [Locale.getDefault()] (e.g. "lunes, 15 jun")
 *
 * Reads [Locale.getDefault()] at call-time; [LocaleManager.setLanguage] calls
 * [Locale.setDefault] so this automatically reflects the in-app language selection.
 */
internal fun String.toDayLabel(): String = try {
    val date   = LocalDate.parse(this, DATE_PARSER)
    val today  = LocalDate.now()
    val locale = Locale.getDefault()
    when (date) {
        today              -> if (locale.language == "es") "Hoy"    else "Today"
        today.plusDays(1)  -> if (locale.language == "es") "Mañana" else "Tomorrow"
        else -> date.format(DateTimeFormatter.ofPattern("EEEE, d MMM", locale))
            .replaceFirstChar { it.uppercaseChar() }
    }
} catch (_: Exception) { this }

/**
 * Short label for horizontal compact cards ("Today" / "Hoy" / abbreviated weekday).
 * Uses [Locale.getDefault()] so weekday abbreviations auto-localize (Mon → lun).
 */
internal fun String.toShortDayLabel(): String = try {
    val date   = LocalDate.parse(this, DATE_PARSER)
    val locale = Locale.getDefault()
    when (date) {
        LocalDate.now() -> if (locale.language == "es") "Hoy" else "Today"
        else -> date.format(DateTimeFormatter.ofPattern("EEE", locale))
            .replaceFirstChar { it.uppercaseChar() }
    }
} catch (_: Exception) { this }

internal fun uvColor(uv: Double): Color = when {
    uv <= 2  -> Color(0xFF66BB6A)
    uv <= 5  -> Color(0xFFFFEE58)
    uv <= 7  -> Color(0xFFFFA726)
    uv <= 10 -> Color(0xFFEF5350)
    else     -> Color(0xFFAB47BC)
}
