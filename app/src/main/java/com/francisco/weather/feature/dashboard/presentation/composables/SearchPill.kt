package com.francisco.weather.feature.dashboard.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme

@Composable
internal fun SearchPill(
    sky: SkyColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(WeatherTheme.Radius.medium),
        colors = CardDefaults.cardColors(containerColor = WeatherTheme.Colors.glassStrong),
        border = BorderStroke(WeatherTheme.Border.thin, WeatherTheme.Colors.glassStroke),
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
            modifier = Modifier.padding(horizontal = WeatherTheme.Size.large, vertical = WeatherTheme.Size.large),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = WeatherTheme.Colors.onSky,
                modifier = Modifier.size(WeatherTheme.IconSize.medium),
            )
            Text(
                text = "Search for a place",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = sky.textMuted,
            )
        }
    }
}
