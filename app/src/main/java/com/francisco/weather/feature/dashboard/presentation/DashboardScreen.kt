package com.francisco.weather.feature.dashboard.presentation

import android.Manifest
import android.content.Context
import android.util.Log
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
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
import com.francisco.weather.feature.dashboard.presentation.composables.screens.DashboardLandscape
import com.francisco.weather.feature.dashboard.presentation.composables.screens.DashboardPortrait
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

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
    // init{} owns the initial stadium sync; this effect only re-syncs on actual language changes.
    // Without this guard, GetRemoteStadiums runs twice on startup (init + this effect), causing a
    // TTL race where both reads see lastSports==null and both issue 16+16 concurrent fetches.
    var previousLanguage by remember { mutableStateOf(language) }
    LaunchedEffect(language) {
        // Weather always reloads: on initial composition it provides the IP-fallback baseline;
        // on language change it re-fetches so condition.text arrives in the new locale.
        val q = viewModel.resolvedLocationQuery ?: "auto:ip"
        val changed = language != previousLanguage
        Log.d("magnus", "language effect → lang=$language prev=$previousLanguage resolved=${viewModel.resolvedLocationQuery} q=$q changed=$changed")
        viewModel.onEvent(DashboardEvent.LoadCurrentWeather(q))
        if (changed) {
            previousLanguage = language
            // Force-bypass TTL so stadium condition.text also re-localizes ("Sunny" → "Soleado").
            viewModel.onEvent(DashboardEvent.GetRemoteStadiums(force = true))
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val gpsSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { /* GPS state refreshed by ON_RESUME observer */ }

    // Pide activar los servicios de ubicación del dispositivo. En éxito (ya activos, o el usuario
    // los enciende en el diálogo del sistema) despacha GpsStateChanged(true), que re-ejecuta el
    // efecto GPS y dispara el forecast con coordenadas precisas.
    // Declarado ANTES de permissionLauncher para evitar referencia adelantada.
    val promptEnableGps: () -> Unit = {
        Log.d("magnus", "promptEnableGps → checking location settings")
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).build()
        val settingsReq = LocationSettingsRequest.Builder().addLocationRequest(req).build()
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(settingsReq)
            .addOnSuccessListener {
                Log.d("magnus", "promptEnableGps → settings satisfied → GpsStateChanged(true)")
                viewModel.onEvent(DashboardEvent.GpsStateChanged(true))
            }
            .addOnFailureListener { ex ->
                if (ex is ResolvableApiException) {
                    Log.d("magnus", "promptEnableGps → needs resolution, launching system dialog")
                    try {
                        gpsSettingsLauncher.launch(IntentSenderRequest.Builder(ex.resolution).build())
                    } catch (_: Exception) {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                } else {
                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onEvent(DashboardEvent.LocationPermissionResult(granted))
        if (granted) {
            val gpsOn = context.isGpsEnabled()
            Log.d("magnus", "permission GRANTED → gpsOn=$gpsOn")
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

    LaunchedEffect(state.locationPermissionGranted, state.isGpsEnabled) {
        Log.d("magnus", "GPS LaunchedEffect → permissionGranted=${state.locationPermissionGranted} gpsEnabled=${state.isGpsEnabled} currentWeather=${state.currentWeather?.locationName ?: "null"} isLoading=${state.isLoadingWeather}")
        // Guard removed: currentWeather == null was blocking GPS updates after IP fallback.
        // We always fetch the GPS location when permission + GPS are active so that:
        // 1. GPS overrides the IP-based location with the accurate one.
        // 2. resolvedLocationQuery is stored in the VM for language-change re-fetches.
        if (state.locationPermissionGranted && state.isGpsEnabled) {
            Log.d("magnus", "GPS LaunchedEffect → requesting location...")
            @Suppress("MissingPermission")
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    val q = if (loc != null) "${loc.latitude},${loc.longitude}" else null
                    if (q != null) {
                        viewModel.onLocationResolved(q)
                        viewModel.onEvent(DashboardEvent.LoadCurrentWeather(q))
                    } else {
                        fusedLocationClient.lastLocation.addOnSuccessListener { last ->
                            if (last != null) {
                                val lq = "${last.latitude},${last.longitude}"
                                viewModel.onLocationResolved(lq)
                                viewModel.onEvent(DashboardEvent.LoadCurrentWeather(lq))
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    fusedLocationClient.lastLocation.addOnSuccessListener { last ->
                        if (last != null) {
                            val lq = "${last.latitude},${last.longitude}"
                            viewModel.onLocationResolved(lq)
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
            !state.isGpsEnabled -> promptEnableGps()
        }
    }

    val onMyLocationForecast: () -> Unit = {
        val q = viewModel.resolvedLocationQuery ?: state.currentWeather?.locationName
        q?.let { onOpenForecast(it) }
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
