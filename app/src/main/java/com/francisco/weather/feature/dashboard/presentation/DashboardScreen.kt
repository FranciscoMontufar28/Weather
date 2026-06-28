package com.francisco.weather.feature.dashboard.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.francisco.weather.core.di.LocalViewModelFactory
import com.francisco.weather.core.domain.recent.RecentSearch
import com.francisco.weather.core.i18n.LocalLocaleController
import com.francisco.weather.core.ui.components.SkyScaffold
import com.francisco.weather.core.ui.sky.rememberSkyColors
import com.francisco.weather.core.ui.theme.WeatherTheme
import com.francisco.weather.feature.dashboard.presentation.composables.DashboardTopBar
import com.francisco.weather.feature.dashboard.presentation.composables.rememberPromptEnableGps
import com.francisco.weather.feature.dashboard.presentation.composables.screens.DashboardLandscape
import com.francisco.weather.feature.dashboard.presentation.composables.screens.DashboardPortrait

@Composable
fun DashboardScreen(
    onOpenSearch: () -> Unit,
    onOpenForecast: (locationQuery: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val factory = LocalViewModelFactory.current
    val viewModel: DashboardViewModel = viewModel(factory = factory)
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sky = rememberSkyColors()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Re-fetch current weather + stadium weather when the language changes so that
    // condition.text arrives in the newly selected language (via ApiKeyInterceptor's lang= param).
    val language = LocalLocaleController.current.language
    // previousLanguage distinguishes the initial composition from a real language change.
    // init{} owns the initial stadium sync; this effect only re-syncs on actual language changes
    // to avoid a redundant double-sync on startup (init + this effect).
    var previousLanguage by remember { mutableStateOf(language) }
    // Merged effect: re-fetches weather on language change AND on permission/GPS state changes.
    // The use case resolves GPS vs IP internally — the screen never builds the query string.
    LaunchedEffect(language, state.locationPermissionGranted, state.isGpsEnabled) {
        viewModel.onEvent(DashboardEvent.LoadCurrentWeather)
        if (language != previousLanguage) {
            previousLanguage = language
            // Re-sync stadiums so condition.text re-localizes ("Sunny" → "Soleado").
            viewModel.onEvent(DashboardEvent.GetRemoteStadiums)
        }
    }

    // Pide activar los servicios de ubicación; en éxito dispara GpsStateChanged(true), que
    // re-ejecuta el efecto GPS y lanza el forecast con coordenadas precisas.
    // Declarado ANTES de permissionLauncher para evitar referencia adelantada.
    val promptEnableGps = rememberPromptEnableGps {
        viewModel.onEvent(DashboardEvent.GpsStateChanged(true))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onEvent(DashboardEvent.LocationPermissionResult(granted))
        if (granted) {
            val gpsOn = context.isGpsEnabled()
            viewModel.onEvent(DashboardEvent.GpsStateChanged(gpsOn))
            // Permiso recién concedido pero GPS apagado → pedir activarlo sin exigir un segundo
            // toque. Al activarlo, ON_RESUME despacha GpsStateChanged(true) → fetch con GPS.
            if (!gpsOn) promptEnableGps()
        }
    }

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

    val onUseLocation: () -> Unit = {
        when {
            !state.locationPermissionGranted -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            !state.isGpsEnabled -> promptEnableGps()
        }
    }

    val onMyLocationForecast: () -> Unit = {
        state.currentWeather?.locationName?.let(onOpenForecast)
    }
    val onRecentForecast: (RecentSearch) -> Unit = { onOpenForecast(it.name) }
    val onClearRecents: () -> Unit = { viewModel.onEvent(DashboardEvent.ClearRecents) }
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    SkyScaffold(
        sky = sky,
        modifier = modifier,
        topBar = {
            if (!isLandscape) {
                DashboardTopBar(
                    sky = sky,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(start = WeatherTheme.Size.xLarge, end = WeatherTheme.Size.xLarge, top = WeatherTheme.Size.medium),
                )
            }
        },
    ) { innerPadding ->
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
                contentPadding = innerPadding,
            )
        }
    }
}

private fun Context.isGpsEnabled(): Boolean {
    val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        lm.isLocationEnabled
    } else {
        lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
