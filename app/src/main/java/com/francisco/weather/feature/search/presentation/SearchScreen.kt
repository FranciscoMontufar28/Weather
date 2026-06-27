package com.francisco.weather.feature.search.presentation

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.francisco.weather.core.di.LocalViewModelFactory
import com.francisco.weather.core.ui.sky.GlassFill
import com.francisco.weather.core.ui.sky.GlassStrong
import com.francisco.weather.core.ui.sky.GlassStroke
import com.francisco.weather.core.ui.sky.SkyTextPrimary
import com.francisco.weather.core.ui.sky.rememberSkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.search.domain.model.Location

private val RadiusMd = 20.dp
private val RadiusSm = 14.dp

@Composable
fun SearchScreen(
    onLocationSelected: (locationQuery: String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = LocalViewModelFactory.current
    val viewModel: SearchViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsState()
    val sky = rememberSkyColors()
    var queryText by rememberSaveable { mutableStateOf("") }

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.error) {
        if (state.error != null) viewModel.onEvent(SearchEvent.ClearError)
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to sky.top,
                    0.55f to sky.mid,
                    1f to sky.bottom,
                ),
            ),
    ) {
        val onLocationClick: (Location) -> Unit = { loc ->
            viewModel.onEvent(SearchEvent.LocationSelected(loc))
            onLocationSelected(loc.name)
        }
        if (isLandscape) {
            SearchLandscape(
                state = state,
                sky = sky,
                queryText = queryText,
                onQueryChange = {
                    queryText = it
                    viewModel.onEvent(SearchEvent.QueryChanged(it))
                },
                onClear = {
                    queryText = ""
                    viewModel.onEvent(SearchEvent.QueryChanged(""))
                },
                onLocationClick = onLocationClick,
                onNavigateBack = onNavigateBack,
                focusRequester = focusRequester,
            )
        } else {
            SearchPortrait(
                state = state,
                sky = sky,
                queryText = queryText,
                onQueryChange = {
                    queryText = it
                    viewModel.onEvent(SearchEvent.QueryChanged(it))
                },
                onClear = {
                    queryText = ""
                    viewModel.onEvent(SearchEvent.QueryChanged(""))
                },
                onLocationClick = onLocationClick,
                onNavigateBack = onNavigateBack,
                focusRequester = focusRequester,
            )
        }
    }
}

// ── Portrait layout ───────────────────────────────────────────────────────────

@Composable
private fun SearchPortrait(
    state: SearchState,
    sky: com.francisco.weather.core.ui.sky.SkyColors,
    queryText: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onLocationClick: (Location) -> Unit,
    onNavigateBack: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(12.dp))

        SearchHeader(sky = sky, onBack = onNavigateBack)

        Spacer(Modifier.height(22.dp))

        GlassSearchField(
            query = queryText,
            onQueryChange = onQueryChange,
            onClear = onClear,
            sky = sky,
            focusRequester = focusRequester,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(22.dp))

        ResultsPortrait(
            state = state,
            sky = sky,
            onLocationClick = onLocationClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ── Landscape layout ──────────────────────────────────────────────────────────

@Composable
private fun SearchLandscape(
    state: SearchState,
    sky: com.francisco.weather.core.ui.sky.SkyColors,
    queryText: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    onLocationClick: (Location) -> Unit,
    onNavigateBack: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 24.dp, vertical = 14.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .width(312.dp)
                .fillMaxHeight(),
        ) {
            SearchHeader(sky = sky, onBack = onNavigateBack)
            GlassSearchField(
                query = queryText,
                onQueryChange = onQueryChange,
                onClear = onClear,
                sky = sky,
                focusRequester = focusRequester,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        ) {
            if (state.query.isNotBlank()) {
                ResultsLandscape(
                    state = state,
                    sky = sky,
                    onLocationClick = onLocationClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

// ── Shared search controls ────────────────────────────────────────────────────

@Composable
private fun SearchHeader(
    sky: com.francisco.weather.core.ui.sky.SkyColors,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(36.dp).padding(bottom = 4.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = SkyTextPrimary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Search location",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SkyTextPrimary,
                letterSpacing = (-0.5).sp,
            )
            Text(
                text = "Find a city to see its forecast",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = sky.textMuted,
            )
        }
    }
}

@Composable
private fun GlassSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    sky: com.francisco.weather.core.ui.sky.SkyColors,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(RadiusMd),
        colors = CardDefaults.cardColors(containerColor = GlassStrong),
        border = BorderStroke(1.dp, GlassStroke),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 15.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = SkyTextPrimary,
                modifier = Modifier.size(22.dp),
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = SkyTextPrimary,
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
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}

// ── Results — Portrait (single-column list) ───────────────────────────────────

@Composable
private fun ResultsPortrait(
    state: SearchState,
    sky: com.francisco.weather.core.ui.sky.SkyColors,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when {
            state.isLoading -> {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = sky.accent, modifier = Modifier.size(32.dp))
                }
            }

            state.query.isNotBlank() && state.locations.isNotEmpty() -> {
                ResultsSectionLabel(sky = sky)
                Spacer(Modifier.height(12.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
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

            state.query.isNotBlank() && !state.isLoading -> {
                Box(Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No se encontraron resultados",
                        fontSize = 14.sp,
                        color = sky.textMuted,
                    )
                }
            }
        }
    }
}

// ── Results — Landscape (2-column grid) ───────────────────────────────────────

@Composable
private fun ResultsLandscape(
    state: SearchState,
    sky: com.francisco.weather.core.ui.sky.SkyColors,
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
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
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

// ── Shared result row ─────────────────────────────────────────────────────────

@Composable
private fun ResultsSectionLabel(
    sky: com.francisco.weather.core.ui.sky.SkyColors,
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
private fun ResultRow(
    location: Location,
    sky: com.francisco.weather.core.ui.sky.SkyColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(RadiusSm),
        colors = CardDefaults.cardColors(containerColor = GlassFill),
        border = BorderStroke(1.dp, GlassStroke),
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(GlassStrong, CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = sky.accent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SkyTextPrimary,
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

@Preview(name = "Portrait · Day", showBackground = true, widthDp = 412, heightDp = 917)
@Composable
private fun SearchPortraitPreview() {
    WeatherTheme { Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF123A72), Color(0xFF245CA8), Color(0xFF4E92DA))))) }
}
