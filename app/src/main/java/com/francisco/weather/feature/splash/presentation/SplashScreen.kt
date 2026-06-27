package com.francisco.weather.feature.splash.presentation

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.francisco.weather.core.ui.theme.SkyBlue
import com.francisco.weather.core.ui.theme.SkyBlueDark
import com.francisco.weather.core.ui.theme.SkyBlueLight
import com.francisco.weather.core.ui.theme.WeatherTheme
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 2000L
private const val ANIMATION_DURATION_MS = 800

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS, easing = EaseOutCubic),
        label = "splash_alpha",
    )

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.7f,
        animationSpec = tween(durationMillis = ANIMATION_DURATION_MS, easing = EaseOutCubic),
        label = "splash_scale",
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(SPLASH_DURATION_MS)
        onSplashFinished()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(SkyBlueDark, SkyBlue, SkyBlueLight),
                ),
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alpha)
                .scale(scale),
        ) {
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = "Logo de la app",
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(100.dp),
            )

            Spacer(Modifier.height(WeatherTheme.Size.huge))

            Text(
                text = "Breezy",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White,
                ),
            )

            Text(
                text = "Weather",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Light,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.85f),
                ),
            )

            Spacer(Modifier.height(WeatherTheme.Size.xMedium))

            Text(
                text = "Tu clima, siempre claro.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    WeatherTheme {
        SplashScreen(onSplashFinished = {})
    }
}
