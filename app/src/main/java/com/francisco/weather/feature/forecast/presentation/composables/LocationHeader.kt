package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.ForecastData

@Composable
internal fun LocationHeader(
    forecast: ForecastData,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    GlassCard(shape = RoundedCornerShape(WeatherTheme.Radius.medium), modifier = modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(vertical = WeatherTheme.Size.huge, horizontal = WeatherTheme.Size.large),
        ) {
            Text(
                text = forecast.locationName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = WeatherTheme.Colors.onSky,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(WeatherTheme.Size.xSmall))
            Text(
                text = if (forecast.region.isNotBlank()) "${forecast.region}, ${forecast.country}" else forecast.country,
                fontSize = 14.sp,
                color = sky.textMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}
