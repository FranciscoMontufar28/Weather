package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.francisco.weather.R
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.Astro

@Composable
internal fun AstroRow(
    astro: Astro,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    val moonFallback = stringResource(R.string.astro_moon)
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        AstroItem(icon = Icons.Default.WbSunny, label = stringResource(R.string.astro_sunrise), value = astro.sunrise, sky = sky)
        AstroItem(icon = Icons.Default.WbSunny, label = stringResource(R.string.astro_sunset), value = astro.sunset, sky = sky, iconTint = Color(0xFFFFB74D))
        AstroItem(icon = Icons.Default.DarkMode, label = astro.moonPhase.ifBlank { moonFallback }, value = "${astro.moonIllumination}%", sky = sky)
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
        horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.small),
        modifier = modifier,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(WeatherTheme.IconSize.small))
        Column {
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WeatherTheme.Colors.onSky)
            Text(text = label, fontSize = 10.sp, color = sky.textMuted)
        }
    }
}
