package com.francisco.weather.feature.dashboard.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francisco.weather.R
import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme

@Composable
internal fun RecentHeader(
    sky: SkyColors,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.dashboard_recent_searches),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = sky.textMuted,
        )
        Text(
            text = stringResource(R.string.action_clear),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = sky.accent,
            modifier = Modifier.clickable(onClick = onClear),
        )
    }
}

@Composable
internal fun RecentSearchesSection(
    recents: List<RecentSearch>,
    sky: SkyColors,
    onClear: () -> Unit,
    onSelect: (RecentSearch) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
        modifier = modifier,
    ) {
        RecentHeader(sky = sky, onClear = onClear)
        Column(
            verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
            modifier = Modifier.fillMaxWidth(),
        ) {
            recents.forEach { recent ->
                RecentRow(recent = recent, sky = sky, onClick = { onSelect(recent) })
            }
        }
    }
}

@Composable
internal fun RecentRow(
    recent: RecentSearch,
    sky: SkyColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(WeatherTheme.Radius.small),
        colors = CardDefaults.cardColors(containerColor = WeatherTheme.Colors.glassFill),
        border = BorderStroke(WeatherTheme.Border.thin, WeatherTheme.Colors.glassStroke),
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
            modifier = Modifier.padding(horizontal = WeatherTheme.Size.large, vertical = WeatherTheme.Size.large),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(WeatherTheme.Colors.glassStrong, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = sky.accent,
                    modifier = Modifier.size(WeatherTheme.IconSize.medium),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recent.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WeatherTheme.Colors.onSky,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (recent.region.isNotBlank()) {
                    Text(
                        text = "${recent.region}, ${recent.country}",
                        fontSize = 12.sp,
                        color = sky.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    Text(
                        text = recent.country,
                        fontSize = 12.sp,
                        color = sky.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = sky.textMuted,
                modifier = Modifier.size(WeatherTheme.IconSize.small),
            )
        }
    }
}

@Composable
internal fun RecentSearchesGrid(
    recents: List<RecentSearch>,
    sky: SkyColors,
    onSelect: (RecentSearch) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
        modifier = modifier.fillMaxWidth(),
    ) {
        recents.chunked(2).forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
                modifier = Modifier.fillMaxWidth(),
            ) {
                RecentRow(
                    recent = pair[0],
                    sky = sky,
                    onClick = { onSelect(pair[0]) },
                    modifier = Modifier.weight(1f),
                )
                if (pair.size == 2) {
                    RecentRow(
                        recent = pair[1],
                        sky = sky,
                        onClick = { onSelect(pair[1]) },
                        modifier = Modifier.weight(1f),
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
