package com.francisco.weather.feature.dashboard.presentation.composables

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import kotlin.math.roundToInt

@Composable
internal fun MyLocationCard(
    state: DashboardState,
    sky: SkyColors,
    context: Context,
    onUseLocation: () -> Unit,
    onOpenForecast: () -> Unit,
    showMetricsInline: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val currentWeather = state.currentWeather
    val weatherError = state.weatherError

    Card(
        shape = RoundedCornerShape(WeatherTheme.Radius.medium),
        colors = CardDefaults.cardColors(containerColor = WeatherTheme.Colors.glassStrong),
        border = BorderStroke(WeatherTheme.Border.thin, WeatherTheme.Colors.glassStroke),
        modifier = modifier,
    ) {
        if (showMetricsInline && currentWeather != null) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = WeatherTheme.Size.xLarge, vertical = WeatherTheme.Size.large)
                    .fillMaxSize(),
            ) {
                WeatherContent(
                    forecast = currentWeather,
                    sky = sky,
                    context = context,
                    onClick = onOpenForecast,
                )
                MetricsRow(
                    forecast = currentWeather,
                    sky = sky,
                    modifier = Modifier.padding(top = WeatherTheme.Size.xMedium),
                )
            }
        } else {
            Box(modifier = Modifier.padding(WeatherTheme.Size.xLarge).fillMaxWidth()) {
                when {
                    currentWeather != null -> {
                        Column {
                            WeatherContent(
                                forecast = currentWeather,
                                sky = sky,
                                context = context,
                                onClick = onOpenForecast,
                            )
                            if (state.isApproxLocation) {
                                Spacer(Modifier.height(WeatherTheme.Size.xMedium))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(
                                        text = "📍 Ubicación aproximada (IP)",
                                        fontSize = 11.sp,
                                        color = sky.textMuted,
                                    )
                                    Text(
                                        text = "Mejorar precisión",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = sky.accent,
                                        modifier = Modifier.clickable(onClick = onUseLocation),
                                    )
                                }
                            }
                        }
                    }
                    state.isLoadingWeather -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                        ) {
                            CircularProgressIndicator(color = sky.accent)
                        }
                    }
                    weatherError != null -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xMedium),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = weatherError,
                                fontSize = 14.sp,
                                color = WeatherTheme.Colors.onSky,
                            )
                            Text(
                                text = "Retry",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sky.accent,
                                modifier = Modifier.clickable(onClick = onUseLocation),
                            )
                        }
                    }
                    !state.locationResolved -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                        ) {
                            CircularProgressIndicator(color = sky.accent)
                        }
                    }
                    state.locationPermissionGranted && state.isGpsEnabled -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                        ) {
                            CircularProgressIndicator(color = sky.accent)
                        }
                    }
                    else -> {
                        EnableLocationPrompt(sky = sky, onEnable = onUseLocation)
                    }
                }
            }
        }
    }
}

@Composable
private fun EnableLocationPrompt(
    sky: SkyColors,
    onEnable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.small),
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = sky.accent,
                modifier = Modifier.size(WeatherTheme.IconSize.small),
            )
            Text(
                text = "MY LOCATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = sky.textMuted,
            )
        }
        Text(
            text = "No se pudo obtener el clima. Activa la ubicación para ver el pronóstico local.",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = WeatherTheme.Colors.onSky,
        )
        Card(
            shape = RoundedCornerShape(WeatherTheme.Radius.full),
            colors = CardDefaults.cardColors(containerColor = Color(0x33000000)),
            border = BorderStroke(WeatherTheme.Border.thin, sky.accent),
            modifier = Modifier.clickable(onClick = onEnable),
        ) {
            Text(
                text = "Usar ubicación",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = sky.accent,
                modifier = Modifier.padding(horizontal = WeatherTheme.Size.xLarge, vertical = WeatherTheme.Size.medium),
            )
        }
    }
}

@Composable
private fun WeatherContent(
    forecast: ForecastData,
    sky: SkyColors,
    context: Context,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall),
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.small),
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = sky.accent,
                modifier = Modifier.size(WeatherTheme.IconSize.small),
            )
            Text(
                text = "MY LOCATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = sky.textMuted,
            )
        }
        Spacer(Modifier.height(WeatherTheme.Size.xSmall))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall)) {
                Text(
                    text = forecast.locationName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WeatherTheme.Colors.onSky,
                )
                Text(
                    text = forecast.country,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = sky.textMuted,
                )
                Text(
                    text = forecast.current.condition.text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WeatherTheme.Colors.onSky,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(forecast.current.condition.iconUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = forecast.current.condition.text,
                    modifier = Modifier.size(WeatherTheme.IconSize.weather),
                )
                Text(
                    text = "${forecast.current.tempC.roundToInt()}°",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    color = WeatherTheme.Colors.onSky,
                )
            }
        }
    }
}
