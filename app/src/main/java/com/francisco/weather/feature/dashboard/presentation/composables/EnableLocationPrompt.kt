package com.francisco.weather.feature.dashboard.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.francisco.weather.R
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.sky.computeSkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme

@Composable
internal fun EnableLocationPrompt(
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
                text = stringResource(R.string.location_my_location),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = sky.textMuted,
            )
        }
        Text(
            text = stringResource(R.string.location_enable_prompt),
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
                text = stringResource(R.string.location_use_location),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = sky.accent,
                modifier = Modifier.padding(
                    horizontal = WeatherTheme.Size.xLarge,
                    vertical = WeatherTheme.Size.medium
                ),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF245CA8)
@Composable
private fun PreviewEnableLocationPrompt() {
    WeatherTheme {
        EnableLocationPrompt(
            sky = computeSkyColors(13f),
            onEnable = {},
        )
    }
}