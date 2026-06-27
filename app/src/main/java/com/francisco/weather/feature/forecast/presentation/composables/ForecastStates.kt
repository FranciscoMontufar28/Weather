package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme

@Composable
internal fun LoadingState(sky: SkyColors) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = sky.accent)
    }
}

@Composable
internal fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(WeatherTheme.Size.xHuge),
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = sky.accent,
            modifier = Modifier.size(48.dp),
        )
        Spacer(Modifier.height(WeatherTheme.Size.medium))
        Text(text = message, fontSize = 15.sp, color = sky.textMuted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(WeatherTheme.Size.large))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = WeatherTheme.Colors.glassStroke)) {
            Text("Reintentar", color = WeatherTheme.Colors.onSky)
        }
    }
}

@Composable
internal fun GlassCard(
    modifier: Modifier = Modifier,
    fill: Color = WeatherTheme.Colors.glassFill,
    shape: Shape = RoundedCornerShape(WeatherTheme.Radius.medium),
    content: @Composable () -> Unit,
) {
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = fill),
        border = BorderStroke(WeatherTheme.Border.thin, WeatherTheme.Colors.glassStroke),
        modifier = modifier,
    ) { content() }
}
