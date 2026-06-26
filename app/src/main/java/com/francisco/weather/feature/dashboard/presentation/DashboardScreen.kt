package com.francisco.weather.feature.dashboard.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.francisco.weather.core.di.LocalViewModelFactory
import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.core.ui.sky.GlassFill
import com.francisco.weather.feature.dashboard.domain.model.WorldCupStadium
import com.francisco.weather.core.ui.sky.GlassStrong
import com.francisco.weather.core.ui.sky.GlassStroke
import com.francisco.weather.core.ui.sky.SkyColors
import com.francisco.weather.core.ui.sky.SkyTextPrimary
import com.francisco.weather.core.ui.sky.rememberSkyColors
import com.francisco.weather.feature.forecast.domain.model.ForecastData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    onOpenSearch: () -> Unit,
    onOpenForecast: (locationQuery: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = LocalViewModelFactory.current
    val viewModel: DashboardViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsState()
    val sky = rememberSkyColors()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var currentLocationQuery by remember { mutableStateOf<String?>(null) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onEvent(DashboardEvent.LocationPermissionResult(granted))
        if (granted) viewModel.onEvent(DashboardEvent.GpsStateChanged(context.isGpsEnabled()))
    }

    val gpsSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { /* GPS state refreshed by ON_RESUME observer */ }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val granted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                viewModel.onEvent(DashboardEvent.LocationPermissionResult(granted))
                if (granted) viewModel.onEvent(DashboardEvent.GpsStateChanged(context.isGpsEnabled()))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(state.locationPermissionGranted, state.isGpsEnabled) {
        if (state.locationPermissionGranted && state.isGpsEnabled &&
            state.currentWeather == null && !state.isLoadingWeather
        ) {
            @Suppress("MissingPermission")
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    val q = if (loc != null) "${loc.latitude},${loc.longitude}" else null
                    if (q != null) {
                        currentLocationQuery = q
                        viewModel.onEvent(DashboardEvent.LoadCurrentWeather(q))
                    } else {
                        fusedLocationClient.lastLocation.addOnSuccessListener { last ->
                            if (last != null) {
                                val lq = "${last.latitude},${last.longitude}"
                                currentLocationQuery = lq
                                viewModel.onEvent(DashboardEvent.LoadCurrentWeather(lq))
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    fusedLocationClient.lastLocation.addOnSuccessListener { last ->
                        if (last != null) {
                            val lq = "${last.latitude},${last.longitude}"
                            currentLocationQuery = lq
                            viewModel.onEvent(DashboardEvent.LoadCurrentWeather(lq))
                        }
                    }
                }
        }
    }

    val onUseLocation: () -> Unit = {
        when {
            !state.locationPermissionGranted -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            !state.isGpsEnabled -> {
                val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).build()
                val settingsReq = LocationSettingsRequest.Builder().addLocationRequest(req).build()
                LocationServices.getSettingsClient(context)
                    .checkLocationSettings(settingsReq)
                    .addOnSuccessListener {
                        viewModel.onEvent(DashboardEvent.GpsStateChanged(true))
                    }
                    .addOnFailureListener { ex ->
                        if (ex is ResolvableApiException) {
                            try {
                                gpsSettingsLauncher.launch(
                                    IntentSenderRequest.Builder(ex.resolution).build()
                                )
                            } catch (_: Exception) {
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        } else {
                            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }
                    }
            }
        }
    }

    val onMyLocationForecast: () -> Unit = { currentLocationQuery?.let { onOpenForecast(it) } }
    val onRecentForecast: (RecentSearch) -> Unit = { onOpenForecast(it.name) }
    val onClearRecents: () -> Unit = { viewModel.onEvent(DashboardEvent.ClearRecents) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

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
        if (isLandscape) {
            DashboardLandscape(
                state = state,
                sky = sky,
                context = context,
                onOpenSearch = onOpenSearch,
                onUseLocation = onUseLocation,
                onOpenForecast = onMyLocationForecast,
                onOpenRecentForecast = onRecentForecast,
                onClearRecents = onClearRecents,
            )
        } else {
            DashboardPortrait(
                state = state,
                sky = sky,
                context = context,
                onOpenSearch = onOpenSearch,
                onUseLocation = onUseLocation,
                onOpenForecast = onMyLocationForecast,
                onOpenRecentForecast = onRecentForecast,
                onClearRecents = onClearRecents,
            )
        }
    }
}

// ── Portrait layout ───────────────────────────────────────────────────────────

@Composable
private fun DashboardPortrait(
    state: DashboardState,
    sky: SkyColors,
    context: Context,
    onOpenSearch: () -> Unit,
    onUseLocation: () -> Unit,
    onOpenForecast: () -> Unit,
    onOpenRecentForecast: (RecentSearch) -> Unit,
    onClearRecents: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        DashboardTopBar(sky = sky)

        SearchPill(sky = sky, onClick = onOpenSearch, modifier = Modifier.fillMaxWidth())

        MyLocationCard(
            state = state,
            sky = sky,
            context = context,
            onUseLocation = onUseLocation,
            onOpenForecast = onOpenForecast,
            showMetricsInline = false,
            modifier = Modifier.fillMaxWidth(),
        )

        if (state.currentWeather != null) {
            MetricsCard(
                forecast = state.currentWeather!!,
                sky = sky,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.recentSearches.isNotEmpty()) {
            RecentSearchesSection(
                recents = state.recentSearches,
                sky = sky,
                onClear = onClearRecents,
                onSelect = onOpenRecentForecast,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.stadiums.isNotEmpty()) {
            WorldCupStadiumsSection(
                stadiums = state.stadiums,
                sky = sky,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}

// ── Landscape layout ──────────────────────────────────────────────────────────

@Composable
private fun DashboardLandscape(
    state: DashboardState,
    sky: SkyColors,
    context: Context,
    onOpenSearch: () -> Unit,
    onUseLocation: () -> Unit,
    onOpenForecast: () -> Unit,
    onOpenRecentForecast: (RecentSearch) -> Unit,
    onClearRecents: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // Left pane — fixed 344dp: greeting + My Location card (with metrics inside)
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .width(344.dp)
                .fillMaxHeight(),
        ) {
            DashboardTopBar(sky = sky)
            MyLocationCard(
                state = state,
                sky = sky,
                context = context,
                onUseLocation = onUseLocation,
                onOpenForecast = onOpenForecast,
                showMetricsInline = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

        // Right pane — fill remaining width: search pill + recent grid (2 columns)
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
        ) {
            SearchPill(sky = sky, onClick = onOpenSearch, modifier = Modifier.fillMaxWidth())

            if (state.recentSearches.isNotEmpty()) {
                RecentHeader(sky = sky, onClear = onClearRecents)

                state.recentSearches.chunked(2).forEach { pair ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        RecentRow(
                            recent = pair[0],
                            sky = sky,
                            onClick = { onOpenRecentForecast(pair[0]) },
                            modifier = Modifier.weight(1f),
                        )
                        if (pair.size == 2) {
                            RecentRow(
                                recent = pair[1],
                                sky = sky,
                                onClick = { onOpenRecentForecast(pair[1]) },
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            if (state.stadiums.isNotEmpty()) {
                WorldCupStadiumsSection(
                    stadiums = state.stadiums,
                    sky = sky,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun DashboardTopBar(
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    val greeting = remember {
        when {
            LocalTime.now().hour < 12 -> "Good morning"
            LocalTime.now().hour < 18 -> "Good afternoon"
            else -> "Good evening"
        }
    }
    val dateText = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH))
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
                color = SkyTextPrimary,
            )
        }
    }
}

// ── Search pill ───────────────────────────────────────────────────────────────

@Composable
private fun SearchPill(
    sky: SkyColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassStrong),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassStroke),
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = SkyTextPrimary,
                modifier = Modifier.size(22.dp),
            )
            Text(
                text = "Search for a place",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = sky.textMuted,
            )
        }
    }
}

// ── My Location card ──────────────────────────────────────────────────────────

@Composable
private fun MyLocationCard(
    state: DashboardState,
    sky: SkyColors,
    context: Context,
    onUseLocation: () -> Unit,
    onOpenForecast: () -> Unit,
    showMetricsInline: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassStrong),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassStroke),
        modifier = modifier,
    ) {
        // Landscape + weather loaded: fill card height, weather top, metrics bottom
        if (showMetricsInline && state.currentWeather != null) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .fillMaxSize(),
            ) {
                WeatherContent(
                    forecast = state.currentWeather!!,
                    sky = sky,
                    context = context,
                    onClick = onOpenForecast,
                )
                MetricsRow(
                    forecast = state.currentWeather!!,
                    sky = sky,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            Box(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                when {
                    state.currentWeather != null -> {
                        WeatherContent(
                            forecast = state.currentWeather!!,
                            sky = sky,
                            context = context,
                            onClick = onOpenForecast,
                        )
                    }
                    state.isLoadingWeather -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                        ) {
                            CircularProgressIndicator(color = sky.accent)
                        }
                    }
                    state.weatherError != null -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                text = state.weatherError!!,
                                fontSize = 14.sp,
                                color = SkyTextPrimary,
                            )
                            Text(
                                text = "Retry",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = sky.accent,
                                modifier = Modifier.clickable(onClick = onUseLocation),
                            )
                        }
                    }
                    // Permission not yet resolved — show spinner to avoid the flash
                    !state.locationResolved -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                        ) {
                            CircularProgressIndicator(color = sky.accent)
                        }
                    }
                    // Permission granted + GPS on — location/weather in flight
                    state.locationPermissionGranted && state.isGpsEnabled -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                        ) {
                            CircularProgressIndicator(color = sky.accent)
                        }
                    }
                    else -> {
                        EnableLocationPrompt(sky = sky, onEnable = onUseLocation)
                    }
                }
            }
        }
    }
}

@Composable
private fun EnableLocationPrompt(
    sky: SkyColors,
    onEnable: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = sky.accent,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "MY LOCATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = sky.textMuted,
            )
        }
        Text(
            text = "Enable location to see local weather",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = SkyTextPrimary,
        )
        Card(
            shape = RoundedCornerShape(50.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0x33000000)),
            border = androidx.compose.foundation.BorderStroke(1.dp, sky.accent),
            modifier = Modifier.clickable(onClick = onEnable),
        ) {
            Text(
                text = "Enable Location",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = sky.accent,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun WeatherContent(
    forecast: ForecastData,
    sky: SkyColors,
    context: Context,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = sky.accent,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = "MY LOCATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = sky.textMuted,
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = forecast.locationName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SkyTextPrimary,
                )
                Text(
                    text = forecast.country,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = sky.textMuted,
                )
                Text(
                    text = forecast.current.condition.text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SkyTextPrimary,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("https:${forecast.current.condition.iconUrl}")
                        .crossfade(true)
                        .build(),
                    contentDescription = forecast.current.condition.text,
                    modifier = Modifier.size(54.dp),
                )
                Text(
                    text = "${forecast.current.tempC.roundToInt()}°",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkyTextPrimary,
                )
            }
        }
    }
}

// ── Metrics ───────────────────────────────────────────────────────────────────

@Composable
private fun MetricsRow(
    forecast: ForecastData,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        MetricItem(
            icon = Icons.Default.WaterDrop,
            label = "Humidity",
            value = "${forecast.current.humidity}%",
            sky = sky,
        )
        MetricItem(
            icon = Icons.Default.Air,
            label = "Wind",
            value = "${forecast.current.windKph.roundToInt()} km/h",
            sky = sky,
        )
        MetricItem(
            icon = Icons.Default.Thermostat,
            label = "Feels like",
            value = "${forecast.current.feelsLikeC.roundToInt()}°",
            sky = sky,
        )
    }
}

@Composable
private fun MetricsCard(
    forecast: ForecastData,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassFill),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassStroke),
        modifier = modifier,
    ) {
        MetricsRow(
            forecast = forecast,
            sky = sky,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
        )
    }
}

@Composable
private fun MetricItem(
    icon: ImageVector,
    label: String,
    value: String,
    sky: SkyColors,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = sky.textMuted,
            modifier = Modifier.size(20.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SkyTextPrimary,
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = sky.textMuted,
            )
        }
    }
}

// ── Recent Searches ───────────────────────────────────────────────────────────

@Composable
private fun RecentHeader(
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
            text = "RECENT SEARCHES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = sky.textMuted,
        )
        Text(
            text = "Clear",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = sky.accent,
            modifier = Modifier.clickable(onClick = onClear),
        )
    }
}

@Composable
private fun RecentSearchesSection(
    recents: List<RecentSearch>,
    sky: SkyColors,
    onClear: () -> Unit,
    onSelect: (RecentSearch) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        RecentHeader(sky = sky, onClear = onClear)
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            recents.forEach { recent ->
                RecentRow(recent = recent, sky = sky, onClick = { onSelect(recent) })
            }
        }
    }
}

@Composable
private fun RecentRow(
    recent: RecentSearch,
    sky: SkyColors,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GlassFill),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassStroke),
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
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
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = sky.accent,
                    modifier = Modifier.size(18.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recent.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SkyTextPrimary,
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
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// ── World Cup Stadiums ────────────────────────────────────────────────────────

@Composable
private fun WorldCupStadiumsSection(
    stadiums: List<WorldCupStadium>,
    sky: SkyColors,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        Text(
            text = "FIFA WORLD CUP 2026",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = sky.textMuted,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassFill),
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassStroke),
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
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stadium.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkyTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${stadium.city}, ${stadium.country}",
                    fontSize = 11.sp,
                    color = sky.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (stadium.tempC != null && stadium.conditionIconUrl != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp),
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https:${stadium.conditionIconUrl}")
                                .crossfade(true)
                                .build(),
                            contentDescription = stadium.conditionText,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = "${stadium.tempC.roundToInt()}°",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SkyTextPrimary,
                        )
                    }
                }
            }
        }
    }
}

// ── Extensions ────────────────────────────────────────────────────────────────

private fun Context.isGpsEnabled(): Boolean {
    val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        lm.isLocationEnabled
    } else {
        lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
