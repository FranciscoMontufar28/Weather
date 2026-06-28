package com.francisco.weather.feature.search.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francisco.weather.R
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme

@Composable
internal fun SearchHeader(
    sky: SkyColors,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(36.dp).padding(bottom = WeatherTheme.Size.xSmall),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.action_back),
                tint = WeatherTheme.Colors.onSky,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall)) {
            Text(
                text = stringResource(R.string.search_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = WeatherTheme.Colors.onSky,
                letterSpacing = (-0.5).sp,
            )
            Text(
                text = stringResource(R.string.search_subtitle),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = sky.textMuted,
            )
        }
    }
}
