package com.francisco.weather.feature.search.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.search.domain.model.Location
import com.francisco.weather.feature.search.presentation.SearchState

@Composable
internal fun ResultsLandscape(
    state: SearchState,
    sky: SkyColors,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = sky.accent, modifier = Modifier.size(28.dp))
                }
            }

            state.locations.isNotEmpty() -> {
                ResultsSectionLabel(sky = sky)
                Spacer(Modifier.height(WeatherTheme.Size.medium))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
                    horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.locations, key = { it.id }) { location ->
                        ResultRow(
                            location = location,
                            sky = sky,
                            onClick = { onLocationClick(location) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ResultsSectionLabel(
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "RESULTS",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = sky.textMuted,
        letterSpacing = 1.5.sp,
        modifier = modifier,
    )
}

@Composable
internal fun ResultRow(
    location: Location,
    sky: SkyColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(WeatherTheme.Radius.small),
        colors = CardDefaults.cardColors(containerColor = WeatherTheme.Colors.glassFill),
        border = BorderStroke(WeatherTheme.Border.thin, WeatherTheme.Colors.glassStroke),
        modifier = modifier.clickable(onClick = onClick),
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
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = sky.accent,
                    modifier = Modifier.size(WeatherTheme.IconSize.medium),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WeatherTheme.Colors.onSky,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (location.region.isNotBlank()) {
                    Text(
                        text = "${location.region}, ${location.country}",
                        fontSize = 12.sp,
                        color = sky.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    Text(
                        text = location.country,
                        fontSize = 12.sp,
                        color = sky.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
