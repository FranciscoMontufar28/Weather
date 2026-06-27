package com.francisco.weather.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme

/**
 * Full-screen sky-gradient wrapper with a transparent [Scaffold].
 * [topBar] is rendered by the Scaffold; [content] receives the Scaffold's innerPadding.
 * contentWindowInsets = 0 so insets are handled explicitly:
 *   • SkyTopBar adds windowInsetsPadding(statusBars) internally.
 *   • Each screen's LazyColumn uses innerPadding for contentPadding.
 */
@Composable
fun SkyScaffold(
    sky: SkyColors,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(0f to sky.top, 0.55f to sky.mid, 1f to sky.bottom),
            ),
    ) {
        Scaffold(
            topBar = topBar,
            containerColor = Color.Transparent,
            contentColor = WeatherTheme.Colors.onSky,
            contentWindowInsets = WindowInsets(0),
        ) { innerPadding -> content(innerPadding) }
    }
}

/**
 * Custom top bar that draws transparently over the sky gradient.
 *
 * - [onBack] = null  →  primary screen (Dashboard), no back button.
 * - [onBack] != null →  secondary screen; shows ← icon button.
 * - [overline]       →  small muted line ABOVE the title (greeting on Dashboard).
 * - [subtitle]       →  small muted line BELOW the title (hint on Search).
 */
@Composable
fun SkyTopBar(
    sky: SkyColors,
    title: String,
    modifier: Modifier = Modifier,
    overline: String? = null,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                start = if (onBack != null) WeatherTheme.Size.xSmall else WeatherTheme.Size.large,
                end = WeatherTheme.Size.large,
                top = WeatherTheme.Size.xMedium,
                bottom = WeatherTheme.Size.xMedium,
            ),
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = WeatherTheme.Colors.onSky,
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (onBack != null) WeatherTheme.Size.xSmall else WeatherTheme.Size.none),
        ) {
            if (overline != null) {
                Text(
                    text = overline,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = sky.textMuted,
                )
            }
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = WeatherTheme.Colors.onSky,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = sky.textMuted,
                )
            }
        }
    }
}
