package com.francisco.weather.feature.dashboard.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.francisco.weather.R
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
internal fun DashboardTopBar(
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    // Greeting: no remember needed — stringResource() is already reactive to locale changes.
    val greeting = when {
        LocalTime.now().hour < 12 -> stringResource(R.string.dashboard_greeting_morning)
        LocalTime.now().hour < 18 -> stringResource(R.string.dashboard_greeting_afternoon)
        else                      -> stringResource(R.string.dashboard_greeting_evening)
    }

    // Date: re-computed when locale or the date pattern string changes.
    val locale      = LocalConfiguration.current.locales[0]
    val datePattern = stringResource(R.string.dashboard_date_pattern)
    val dateText    = remember(locale, datePattern) {
        LocalDate.now()
            .format(DateTimeFormatter.ofPattern(datePattern, locale))
            .replaceFirstChar { it.uppercaseChar() }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall)) {
            Text(
                text = greeting,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = sky.textMuted,
            )
            Text(
                text = dateText,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = WeatherTheme.Colors.onSky,
            )
        }

        // Language selector: collapsed flag → dropdown with all available languages.
        LanguageFlagSelector()
    }
}
