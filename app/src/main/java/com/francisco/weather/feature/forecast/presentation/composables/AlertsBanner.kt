package com.francisco.weather.feature.forecast.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.forecast.domain.model.WeatherAlert

@Composable
internal fun AlertsBanner(
    alerts: List<WeatherAlert>,
    sky: com.francisco.weather.core.ui.sky.SkyColors,
    modifier: Modifier = Modifier,
) {
    val first = alerts.first()
    var expanded by remember { mutableStateOf(false) }

    val bannerColor = when (first.severity.lowercase()) {
        "extreme" -> Color(0xFFD32F2F).copy(alpha = 0.85f)
        "severe"  -> Color(0xFFE64A19).copy(alpha = 0.85f)
        else      -> Color(0xFFF57F17).copy(alpha = 0.80f)
    }

    Card(
        shape = RoundedCornerShape(WeatherTheme.Radius.medium),
        colors = CardDefaults.cardColors(containerColor = bannerColor),
        border = BorderStroke(WeatherTheme.Border.thin, Color.White.copy(alpha = 0.3f)),
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
    ) {
        Column(modifier = Modifier.padding(WeatherTheme.Size.medium)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(WeatherTheme.Size.xMedium),
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(WeatherTheme.IconSize.medium),
                )
                Text(
                    text = first.event.ifBlank { first.headline },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (alerts.size > 1) {
                    Text(
                        text = "+${alerts.size - 1}",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column {
                    Spacer(Modifier.height(WeatherTheme.Size.xMedium))
                    if (first.areas.isNotBlank()) {
                        Text(
                            text = first.areas,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f),
                        )
                        Spacer(Modifier.height(WeatherTheme.Size.xSmall))
                    }
                    Text(
                        text = first.desc.take(300).trimEnd() + if (first.desc.length > 300) "…" else "",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
            }
        }
    }
}
