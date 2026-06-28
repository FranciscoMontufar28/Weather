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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.francisco.weather.R
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.dashboard.presentation.DashboardState
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import kotlin.math.roundToInt

@Composable
internal fun MyLocationCard(
    modifier: Modifier = Modifier,
    state: DashboardState,
    sky: SkyColors,
    context: Context,
    onUseLocation: () -> Unit,
    onOpenForecast: () -> Unit,
    showMetricsInline: Boolean = false,
) {
    val currentWeather = state.currentWeather
    val weatherErrorRes = state.weatherErrorRes

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
                    .padding(
                        horizontal = WeatherTheme.Size.xLarge,
                        vertical = WeatherTheme.Size.large
                    )
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
            Box(
                modifier = Modifier
                    .padding(WeatherTheme.Size.xLarge)
                    .fillMaxWidth()
            ) {
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
                                        text = stringResource(R.string.location_approx_ip),
                                        fontSize = 11.sp,
                                        color = sky.textMuted,
                                    )
                                    Text(
                                        text = stringResource(R.string.location_improve_accuracy),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                        ) {
                            CircularProgressIndicator(color = sky.accent)
                        }
                    }

                    weatherErrorRes != null -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xMedium),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = stringResource(weatherErrorRes),
                                fontSize = 14.sp,
                                color = WeatherTheme.Colors.onSky,
                            )
                            Text(
                                text = stringResource(R.string.action_retry),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                        ) {
                            CircularProgressIndicator(color = sky.accent)
                        }
                    }

                    state.locationPermissionGranted && state.isGpsEnabled -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
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
