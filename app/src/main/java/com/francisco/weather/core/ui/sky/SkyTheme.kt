package com.francisco.weather.core.ui.sky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import java.time.LocalTime

/**
 * Resolved per-frame color palette for the sky background and surface elements.
 * Mirrors the Pencil design variables: $sky-top, $sky-mid, $sky-bottom, $accent,
 * $text-muted, and $star-opacity — one value set per time-of-day theme.
 */
data class SkyColors(
    val top: Color,       // $sky-top
    val mid: Color,       // $sky-mid
    val bottom: Color,    // $sky-bottom
    val accent: Color,    // $accent
    val textMuted: Color, // $text-muted
    val starOpacity: Float, // $star-opacity
)

// ── Pencil design-token values per phase ──────────────────────────────────────

private val DAWN = SkyColors(
    top = Color(0xFF243A6B),
    mid = Color(0xFF515F9C),
    bottom = Color(0xFF9C7FB8),
    accent = Color(0xFFFFB37A),
    textMuted = Color(0xFFDCD6F0),
    starOpacity = 0.2f,
)

private val DAY = SkyColors(
    top = Color(0xFF123A72),
    mid = Color(0xFF245CA8),
    bottom = Color(0xFF4E92DA),
    accent = Color(0xFFFFC93C),
    textMuted = Color(0xFFC9DEF7),
    starOpacity = 0f,
)

private val DUSK = SkyColors(
    top = Color(0xFF1C2550),
    mid = Color(0xFF6B3A77),
    bottom = Color(0xFFD9743F),
    accent = Color(0xFFFFD27A),
    textMuted = Color(0xFFF0D2C0),
    starOpacity = 0.4f,
)

private val NIGHT = SkyColors(
    top = Color(0xFF05081A),
    mid = Color(0xFF0C1530),
    bottom = Color(0xFF1A2750),
    accent = Color(0xFFAEC2FF),
    textMuted = Color(0xFF93A6CE),
    starOpacity = 1f,
)

// ── Keyframe timeline (hour as Float, 0..24) ─────────────────────────────────
// Each pair is (hour, target SkyColors). The interpolator blends linearly
// between two adjacent keyframes, giving smooth 1-2 hour transitions.

private val KEYFRAMES: List<Pair<Float, SkyColors>> = listOf(
    0f to NIGHT,     // midnight
    5f to NIGHT,     // still deep night until 5:00
    7f to DAWN,      // fully dawn by 7:00
    12f to DAY,      // noon
    17f to DAY,      // late afternoon still day
    20f to DUSK,     // fully dusk by 20:00
    22f to NIGHT,    // night closes in by 22:00
    24f to NIGHT,    // wrap around
)

// ── Color math ───────────────────────────────────────────────────────────────

private fun lerpSky(a: SkyColors, b: SkyColors, t: Float) = SkyColors(
    top = lerp(a.top, b.top, t),
    mid = lerp(a.mid, b.mid, t),
    bottom = lerp(a.bottom, b.bottom, t),
    accent = lerp(a.accent, b.accent, t),
    textMuted = lerp(a.textMuted, b.textMuted, t),
    starOpacity = a.starOpacity + (b.starOpacity - a.starOpacity) * t,
)

/**
 * Returns the interpolated [SkyColors] for [hourFloat] (0f = midnight, 13.5f = 1:30 PM).
 * Public so it can be unit-tested or driven from a preview parameter.
 */
fun computeSkyColors(hourFloat: Float): SkyColors {
    for (i in 0 until KEYFRAMES.size - 1) {
        val (h0, c0) = KEYFRAMES[i]
        val (h1, c1) = KEYFRAMES[i + 1]
        if (hourFloat <= h1) {
            val t = ((hourFloat - h0) / (h1 - h0)).coerceIn(0f, 1f)
            return lerpSky(c0, c1, t)
        }
    }
    return NIGHT
}

/**
 * Composable that returns the [SkyColors] for the current real-world time.
 * Re-computed once per 5-minute bucket so rapid recompositions are cheap.
 */
@Composable
fun rememberSkyColors(): SkyColors {
    val now = LocalTime.now()
    // bucket to 5-minute slots so the remember key changes at most 288×/day
    val bucket = now.hour * 12 + now.minute / 5
    return remember(bucket) {
        computeSkyColors(now.hour + now.minute / 60f)
    }
}

