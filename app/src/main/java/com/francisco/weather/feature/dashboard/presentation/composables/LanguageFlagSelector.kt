package com.francisco.weather.feature.dashboard.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.francisco.weather.core.i18n.AppLanguage
import com.francisco.weather.core.i18n.LocalLocaleController
import com.francisco.weather.core.i18n.LocaleManager
import com.francisco.weather.core.ui.theme.WeatherTheme

/**
 * Collapsed flag icon (current language) in the Dashboard top bar.
 * Tapping opens a [DropdownMenu] listing all available languages.
 * Selecting one calls [LocaleManager.setLanguage], which recomposes
 * [ProvideAppLocale] and updates every stringResource() in the tree instantly.
 *
 * No parameters — reads [LocalLocaleController] directly (no prop-drilling through
 * DashboardScreen or DashboardLandscape).
 */
@Composable
internal fun LanguageFlagSelector(modifier: Modifier = Modifier) {
    val controller = LocalLocaleController.current
    val current    = controller.language
    var expanded   by remember { mutableStateOf(false) }

    // Capture @Composable color tokens outside lambdas.
    val glassStroke = WeatherTheme.Colors.glassStroke
    val onSky       = WeatherTheme.Colors.onSky

    Box(modifier) {
        // Collapsed: show the current flag, clipped to a circle with a glass border.
        Image(
            painter            = painterResource(current.flagRes),
            contentDescription = stringResource(current.labelRes),
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .size(WeatherTheme.IconSize.large)
                .clip(CircleShape)
                .border(WeatherTheme.Border.thin, glassStroke, CircleShape)
                .clickable { expanded = true },
        )

        // Dropdown: one item per supported language.
        DropdownMenu(
            expanded          = expanded,
            onDismissRequest  = { expanded = false },
        ) {
            AppLanguage.entries.forEach { lang ->
                DropdownMenuItem(
                    leadingIcon = {
                        Image(
                            painter            = painterResource(lang.flagRes),
                            contentDescription = null,
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier
                                .size(WeatherTheme.IconSize.medium)
                                .clip(CircleShape),
                        )
                    },
                    text = {
                        Text(
                            text       = stringResource(lang.labelRes),
                            fontWeight = if (lang == current) FontWeight.Bold else FontWeight.Normal,
                            color      = onSky,
                        )
                    },
                    onClick = {
                        controller.setLanguage(lang)
                        expanded = false
                    },
                )
            }
        }
    }
}
