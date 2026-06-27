package com.francisco.weather.feature.search.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme

@Composable
internal fun GlassSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    sky: SkyColors,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(WeatherTheme.Radius.medium),
        colors = CardDefaults.cardColors(containerColor = WeatherTheme.Colors.glassStrong),
        border = BorderStroke(WeatherTheme.Border.thin, WeatherTheme.Colors.glassStroke),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
            modifier = Modifier.padding(horizontal = WeatherTheme.Size.xLarge, vertical = WeatherTheme.Size.large),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = WeatherTheme.Colors.onSky,
                modifier = Modifier.size(WeatherTheme.IconSize.medium),
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = WeatherTheme.Colors.onSky,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                cursorBrush = SolidColor(sky.accent),
                decorationBox = { inner ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "City, country…",
                                fontSize = 17.sp,
                                color = sky.textMuted,
                            )
                        }
                        inner()
                    }
                },
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
            )
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(20.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = sky.textMuted,
                        modifier = Modifier.size(WeatherTheme.IconSize.medium),
                    )
                }
            }
        }
    }
}
