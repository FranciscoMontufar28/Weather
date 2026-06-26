package com.francisco.weather.feature.forecast.presentation

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.francisco.weather.core.di.LocalViewModelFactory
import com.francisco.weather.core.ui.sky.GlassFill
import com.francisco.weather.core.ui.sky.GlassStrong
import com.francisco.weather.core.ui.sky.GlassStroke
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.sky.SkyTextPrimary
import com.francisco.weather.core.ui.sky.computeSkyColors
import com.francisco.weather.core.ui.sky.rememberSkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.Astro
import com.francisco.weather.feature.forecast.domain.model.Condition
import com.francisco.weather.feature.forecast.domain.model.CurrentWeather
import com.francisco.weather.feature.forecast.domain.model.DayWeather
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import com.francisco.weather.feature.forecast.domain.model.HourWeather
import com.francisco.weather.feature.forecast.domain.model.WeatherAlert
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val RadiusLg = 32.dp
private val RadiusMd = 20.dp

// ── Entry point ───────────────────────────────────────────────────────────────

@Composable
fun ForecastScreen(
    locationQuery: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = LocalViewModelFactory.current
    val viewModel: ForecastViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsState()
    val sky = rememberSkyColors()

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(locationQuery) {
        viewModel.onEvent(ForecastEvent.Load(locationQuery))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(0f to sky.top, 0.55f to sky.mid, 1f to sky.bottom),
            ),
    ) {
        when {
            state.isLoading -> LoadingState(sky = sky)

            state.error != null && state.forecast == null -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.onEvent(ForecastEvent.Load(locationQuery)) },
                    sky = sky,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            state.forecast != null -> {
                if (isLandscape) {
                    ForecastLandscape(
                        forecast = state.forecast!!,
                        sky = sky,
                        onBack = onNavigateBack,
                    )
                } else {
                    ForecastPortrait(
                        forecast = state.forecast!!,
                        sky = sky,
                        onBack = onNavigateBack,
                    )
                }
            }
        }
    }
}

// ── Portrait ──────────────────────────────────────────────────────────────────

@Composable
private fun ForecastPortrait(
    forecast: ForecastData,
    sky: SkyColors,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        SkyTopBar(title = forecast.locationName, onBack = onBack)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            // Weather alerts banner — only shows when alerts are present
            if (forecast.alerts.isNotEmpty()) {
                item {
                    AlertsBanner(alerts = forecast.alerts, sky = sky)
                }
            }

            item {
                LocationHeader(forecast = forecast, sky = sky)
            }

            items(forecast.days, key = { it.date }) { day ->
                DayGlassCard(day = day, sky = sky)
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ── Landscape ─────────────────────────────────────────────────────────────────
// Layout: Current Panel (312dp) | Forecast Panel (fill)

@Composable
private fun ForecastLandscape(
    forecast: ForecastData,
    sky: SkyColors,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp, vertical = 2.dp)
            .padding(bottom = 18.dp),
    ) {
        // Left panel — app bar + current weather card
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .width(312.dp)
                .fillMaxHeight(),
        ) {
            LandscapeAppBar(onBack = onBack)
            CurrentWeatherCard(forecast = forecast, sky = sky, modifier = Modifier.weight(1f))
        }

        // Right panel — 3 compact day cards + metrics bar
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            // Alerts inline banner (landscape)
            if (forecast.alerts.isNotEmpty()) {
                AlertsBanner(alerts = forecast.alerts, sky = sky)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "3-Day Forecast",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkyTextPrimary,
                )
            }

            // 3 compact day cards in a horizontal Row (each fills equal weight)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                forecast.days.forEach { day ->
                    CompactDayCard(day = day, sky = sky, modifier = Modifier.weight(1f).fillMaxHeight())
                }
            }

            MetricsBar(current = forecast.current, sky = sky)
        }
    }
}

// ── Top bars ──────────────────────────────────────────────────────────────────

@Composable
private fun SkyTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = SkyTextPrimary,
            )
        }
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkyTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LandscapeAppBar(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth().height(36.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(34.dp)
                .background(GlassFill, CircleShape),
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = SkyTextPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Text(
            text = "Forecast",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SkyTextPrimary,
        )
    }
}

// ── Alert banner ──────────────────────────────────────────────────────────────

@Composable
private fun AlertsBanner(
    alerts: List<WeatherAlert>,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    val first = alerts.first()
    var expanded by remember { mutableStateOf(false) }

    val bannerColor = when (first.severity.lowercase()) {
        "extreme" -> Color(0xFFD32F2F).copy(alpha = 0.85f)
        "severe"  -> Color(0xFFE64A19).copy(alpha = 0.85f)
        else      -> Color(0xFFF57F17).copy(alpha = 0.80f)
    }

    Card(
        shape = RoundedCornerShape(RadiusMd),
        colors = CardDefaults.cardColors(containerColor = bannerColor),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = first.event.ifBlank { first.headline },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (alerts.size > 1) {
                    Text(
                        text = "+${alerts.size - 1}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    if (first.areas.isNotBlank()) {
                        Text(
                            text = first.areas,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(
                        text = first.desc.take(300).trimEnd() + if (first.desc.length > 300) "…" else "",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
            }
        }
    }
}

// ── Portrait cards ────────────────────────────────────────────────────────────

@Composable
private fun LocationHeader(
    forecast: ForecastData,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    GlassCard(shape = RoundedCornerShape(RadiusMd), modifier = modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 16.dp),
        ) {
            Text(
                text = forecast.locationName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = SkyTextPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (forecast.region.isNotBlank()) "${forecast.region}, ${forecast.country}" else forecast.country,
                fontSize = 14.sp,
                color = sky.textMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DayGlassCard(
    day: DayWeather,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    GlassCard(shape = RoundedCornerShape(RadiusMd), modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = day.date.toDayLabel(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = sky.accent,
            )
            Spacer(Modifier.height(12.dp))

            // Condition row
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(day.condition.iconUrl).crossfade(true).build(),
                    contentDescription = day.condition.text,
                    modifier = Modifier.size(60.dp),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = day.condition.text,
                    fontSize = 15.sp,
                    color = SkyTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${day.avgTempC.toInt()}°",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkyTextPrimary,
                    )
                    // UV badge
                    if (day.uv > 0) {
                        Text(
                            text = "UV ${day.uv.toInt()}",
                            fontSize = 10.sp,
                            color = uvColor(day.uv),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = GlassStroke, thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            // Min / Avg / Max + rain chip row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TempChip("Mín", day.minTempC, sky)
                TempChip("Prom", day.avgTempC, sky)
                TempChip("Máx", day.maxTempC, sky)
                if (day.chanceOfRain > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = sky.textMuted,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(text = "${day.chanceOfRain}%", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SkyTextPrimary)
                        Text(text = "Lluvia", fontSize = 12.sp, color = sky.textMuted)
                    }
                }
            }

            // Hourly strip
            if (day.hours.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = GlassStroke, thickness = 0.5.dp)
                Spacer(Modifier.height(8.dp))
                HourlyStrip(hours = day.hours, sky = sky)
            }

            // Astro row
            if (day.astro.sunrise.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = GlassStroke, thickness = 0.5.dp)
                Spacer(Modifier.height(8.dp))
                AstroRow(astro = day.astro, sky = sky)
            }
        }
    }
}

@Composable
private fun TempChip(label: String, temp: Double, sky: SkyColors, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(text = label, fontSize = 12.sp, color = sky.textMuted)
        Text(text = "${temp.toInt()}°C", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SkyTextPrimary)
    }
}

// ── Hourly strip ──────────────────────────────────────────────────────────────

@Composable
private fun HourlyStrip(
    hours: List<HourWeather>,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(hours, key = { it.time }) { hour ->
            HourChip(hour = hour, sky = sky)
        }
    }
}

@Composable
private fun HourChip(
    hour: HourWeather,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    // Extract HH:mm from "yyyy-MM-dd HH:mm"
    val timeLabel = hour.time.takeLast(5)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(GlassFill, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(text = timeLabel, fontSize = 10.sp, color = sky.textMuted)
        Spacer(Modifier.height(4.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(hour.condition.iconUrl).crossfade(true).build(),
            contentDescription = hour.condition.text,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.height(4.dp))
        Text(text = "${hour.tempC.toInt()}°", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SkyTextPrimary)
        if (hour.chanceOfRain > 0) {
            Text(text = "${hour.chanceOfRain}%", fontSize = 10.sp, color = Color(0xFF90CAF9))
        }
    }
}

// ── Astro row ─────────────────────────────────────────────────────────────────

@Composable
private fun AstroRow(
    astro: Astro,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        AstroItem(icon = Icons.Default.WbSunny, label = "Amanecer", value = astro.sunrise, sky = sky)
        AstroItem(icon = Icons.Default.WbSunny, label = "Atardecer", value = astro.sunset, sky = sky, iconTint = Color(0xFFFFB74D))
        AstroItem(icon = Icons.Default.DarkMode, label = astro.moonPhase.ifBlank { "Luna" }, value = "${astro.moonIllumination}%", sky = sky)
    }
}

@Composable
private fun AstroItem(
    icon: ImageVector,
    label: String,
    value: String,
    sky: SkyColors,
    iconTint: Color = Color(0xFFFFE082),
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        Column {
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SkyTextPrimary)
            Text(text = label, fontSize = 10.sp, color = sky.textMuted)
        }
    }
}

// ── Landscape cards ───────────────────────────────────────────────────────────

/** Current weather glass card — big temp, condition, icon (left panel in landscape). */
@Composable
private fun CurrentWeatherCard(
    forecast: ForecastData,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    val today = forecast.days.firstOrNull()
    val current = forecast.current

    GlassCard(fill = GlassStrong, shape = RoundedCornerShape(RadiusLg), modifier = modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp),
        ) {
            Text(
                text = forecast.locationName,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SkyTextPrimary,
            )
            Text(
                text = buildString {
                    if (forecast.region.isNotBlank()) append("${forecast.region}, ")
                    append(forecast.country)
                },
                fontSize = 12.sp,
                color = sky.textMuted,
            )

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(current.condition.iconUrl).crossfade(true).build(),
                    contentDescription = current.condition.text,
                    modifier = Modifier.size(62.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${current.tempC.toInt()}°",
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkyTextPrimary,
                )
            }

            Spacer(Modifier.height(2.dp))
            Text(text = current.condition.text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = SkyTextPrimary)

            if (today != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Average ${today.avgTempC.toInt()}°C  ·  H:${today.maxTempC.toInt()}°  L:${today.minTempC.toInt()}°",
                    fontSize = 12.sp,
                    color = sky.textMuted,
                )
            }
        }
    }
}

/** Compact vertical day card used in the right panel (landscape). */
@Composable
private fun CompactDayCard(
    day: DayWeather,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    GlassCard(shape = RoundedCornerShape(RadiusMd), modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 12.dp),
        ) {
            Text(text = day.date.toShortDayLabel(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SkyTextPrimary)
            Spacer(Modifier.height(8.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(day.condition.iconUrl).crossfade(true).build(),
                contentDescription = day.condition.text,
                modifier = Modifier.size(44.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = day.condition.text,
                fontSize = 11.sp,
                color = sky.textMuted,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Text(text = "${day.avgTempC.toInt()}°C", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SkyTextPrimary)
            if (day.uv > 0) {
                Spacer(Modifier.height(4.dp))
                Text(text = "UV ${day.uv.toInt()}", fontSize = 10.sp, color = uvColor(day.uv), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/** Bottom metrics bar: humidity · wind · feels like · UV. */
@Composable
private fun MetricsBar(
    current: CurrentWeather,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    GlassCard(shape = RoundedCornerShape(RadiusMd), modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(12.dp),
        ) {
            MetricItem(icon = Icons.Default.WaterDrop, value = "${current.humidity}%", label = "Humidity", sky = sky)
            MetricItem(icon = Icons.Default.Air, value = "${current.windKph.toInt()} km/h", label = "Wind", sky = sky)
            MetricItem(icon = Icons.Default.Thermostat, value = "${current.feelsLikeC.toInt()}°C", label = "Feels like", sky = sky)
            if (current.uv > 0) {
                MetricItem(icon = Icons.Default.WbSunny, value = "UV ${current.uv.toInt()}", label = "Índice UV", sky = sky, tint = uvColor(current.uv))
            }
        }
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    value: String,
    label: String,
    sky: SkyColors,
    tint: Color = sky.textMuted,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SkyTextPrimary)
            Text(text = label, fontSize = 10.sp, color = sky.textMuted)
        }
    }
}

// ── UV color helper ───────────────────────────────────────────────────────────

private fun uvColor(uv: Double): Color = when {
    uv <= 2  -> Color(0xFF66BB6A)   // Low — green
    uv <= 5  -> Color(0xFFFFEE58)   // Moderate — yellow
    uv <= 7  -> Color(0xFFFFA726)   // High — orange
    uv <= 10 -> Color(0xFFEF5350)   // Very high — red
    else     -> Color(0xFFAB47BC)   // Extreme — purple
}

// ── Shared states ─────────────────────────────────────────────────────────────

@Composable
private fun LoadingState(sky: SkyColors) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = sky.accent)
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(32.dp),
    ) {
        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = sky.accent, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(12.dp))
        Text(text = message, fontSize = 15.sp, color = sky.textMuted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = GlassStroke)) {
            Text("Reintentar", color = SkyTextPrimary)
        }
    }
}

// ── Reusable glass card ───────────────────────────────────────────────────────

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    fill: Color = GlassFill,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(RadiusMd),
    content: @Composable () -> Unit,
) {
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = fill),
        border = BorderStroke(1.dp, GlassStroke),
        modifier = modifier,
    ) { content() }
}

// ── Date helpers ──────────────────────────────────────────────────────────────

private val DATE_PARSER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

private fun String.toDayLabel(): String = try {
    val date = LocalDate.parse(this, DATE_PARSER)
    val today = LocalDate.now()
    when (date) {
        today -> "Hoy"
        today.plusDays(1) -> "Mañana"
        else -> date.format(DateTimeFormatter.ofPattern("EEEE, d MMM", Locale.forLanguageTag("es")))
            .replaceFirstChar { it.uppercaseChar() }
    }
} catch (_: Exception) { this }

private fun String.toShortDayLabel(): String = try {
    val date = LocalDate.parse(this, DATE_PARSER)
    when (date) {
        LocalDate.now() -> "Today"
        else -> date.format(DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH))
    }
} catch (_: Exception) { this }

// ── Previews ──────────────────────────────────────────────────────────────────

private val PREVIEW_ASTRO = Astro("05:47 AM", "06:10 PM", "Waxing Gibbous", 93)

private val PREVIEW_HOURS = listOf(
    HourWeather("2025-06-25 09:00", 17.0, Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003), 10, true),
    HourWeather("2025-06-25 12:00", 20.0, Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003), 5, true),
    HourWeather("2025-06-25 15:00", 22.0, Condition("Sunny", "https://cdn.weatherapi.com/weather/64x64/day/113.png", 1000), 0, true),
    HourWeather("2025-06-25 18:00", 19.0, Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003), 15, false),
)

private val PREVIEW_FORECAST = ForecastData(
    locationName = "London",
    region = "City of London",
    country = "United Kingdom",
    days = listOf(
        DayWeather("2025-06-25", 19.0, 23.0, 15.0, Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003), uv = 5.0, chanceOfRain = 20, astro = PREVIEW_ASTRO, hours = PREVIEW_HOURS),
        DayWeather("2025-06-26", 17.0, 20.0, 13.0, Condition("Light rain", "https://cdn.weatherapi.com/weather/64x64/day/296.png", 1183), uv = 2.0, chanceOfRain = 80, astro = PREVIEW_ASTRO),
        DayWeather("2025-06-27", 22.0, 26.0, 18.0, Condition("Sunny", "https://cdn.weatherapi.com/weather/64x64/day/113.png", 1000), uv = 8.0, astro = PREVIEW_ASTRO),
    ),
    current = CurrentWeather(
        tempC = 21.0,
        condition = Condition("Partly cloudy", "https://cdn.weatherapi.com/weather/64x64/day/116.png", 1003),
        humidity = 68,
        windKph = 12.0,
        feelsLikeC = 20.0,
        uv = 5.0,
    ),
)

@Preview(name = "Portrait · Day", showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun PortraitDayPreview() {
    val sky = computeSkyColors(13f)
    WeatherTheme {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0f to sky.top, 0.55f to sky.mid, 1f to sky.bottom))) {
            ForecastPortrait(forecast = PREVIEW_FORECAST, sky = sky, onBack = {})
        }
    }
}

@Preview(name = "Landscape · Dusk", showBackground = true, widthDp = 917, heightDp = 412)
@Composable
private fun LandscapeDuskPreview() {
    val sky = computeSkyColors(19.5f)
    WeatherTheme {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0f to sky.top, 0.55f to sky.mid, 1f to sky.bottom))) {
            ForecastLandscape(forecast = PREVIEW_FORECAST, sky = sky, onBack = {})
        }
    }
}
