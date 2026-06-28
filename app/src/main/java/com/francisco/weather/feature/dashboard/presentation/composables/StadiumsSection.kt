package com.francisco.weather.feature.dashboard.presentation.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.francisco.weather.R
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import java.util.Locale
import kotlin.math.roundToInt

@Composable
internal fun WorldCupStadiumsSection(
    stadiums: List<WorldCupStadium>,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.stadium_section_title),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = sky.textMuted,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.medium),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(stadiums, key = { it.name }) { stadium ->
                StadiumCard(stadium = stadium, sky = sky)
            }
        }
    }
}

@Composable
private fun StadiumCard(
    stadium: WorldCupStadium,
    sky: SkyColors,
) {
    // Derive the country name from the ISO 3166-1 alpha-2 country code so it auto-localizes
    // when the user switches language (e.g. "United States" → "Estados Unidos").
    val locale = LocalConfiguration.current.locales[0]
    val countryName = remember(stadium.countryCode, locale) {
        Locale.Builder().setRegion(stadium.countryCode).build()
            .getDisplayCountry(locale)
            .takeIf { it.isNotBlank() } ?: stadium.country
    }

    Card(
        shape = RoundedCornerShape(WeatherTheme.Radius.medium),
        colors = CardDefaults.cardColors(containerColor = WeatherTheme.Colors.glassFill),
        border = BorderStroke(WeatherTheme.Border.thin, WeatherTheme.Colors.glassStroke),
        modifier = Modifier.width(200.dp),
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(stadium.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stadium.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall),
                modifier = Modifier.padding(
                    horizontal = WeatherTheme.Size.large,
                    vertical = WeatherTheme.Size.medium
                ),
            ) {
                Text(
                    text = stadium.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = WeatherTheme.Colors.onSky,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${stadium.city}, $countryName",
                    fontSize = 11.sp,
                    color = sky.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (stadium.tempC != null && stadium.conditionIconUrl != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(WeatherTheme.Size.xSmall),
                        modifier = Modifier.padding(top = WeatherTheme.Size.xSmall),
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(stadium.conditionIconUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = stadium.conditionText,
                            modifier = Modifier.size(WeatherTheme.IconSize.medium),
                        )
                        Text(
                            text = "${stadium.tempC.roundToInt()}°",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WeatherTheme.Colors.onSky,
                        )
                    }
                }
                if (stadium.matchName != null) {
                    HorizontalDivider(
                        color = WeatherTheme.Colors.glassStroke,
                        thickness = WeatherTheme.Border.hairline,
                        modifier = Modifier.padding(vertical = WeatherTheme.Size.small),
                    )
                    Text(
                        text = stringResource(R.string.stadium_next_match),
                        fontSize = 9.sp,
                        color = sky.textMuted,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp,
                    )
                    Spacer(Modifier.height(WeatherTheme.Size.xSmall))
                    Text(
                        text = stadium.matchName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WeatherTheme.Colors.onSky,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (stadium.matchTournament != null) {
                        Text(
                            text = stadium.matchTournament,
                            fontSize = 10.sp,
                            color = sky.accent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (stadium.matchStart != null) {
                        Text(
                            text = stadium.matchStart.formatMatchStart(),
                            fontSize = 10.sp,
                            color = sky.textMuted,
                        )
                    }
                }
            }
        }
    }
}

private fun String.formatMatchStart(): String = try {
    val parts = this.split(" ")
    if (parts.size == 2) {
        val dateParts = parts[0].split("-")
        "${dateParts[2]}/${dateParts[1]} ${parts[1]}"
    } else this
} catch (_: Exception) {
    this
}
