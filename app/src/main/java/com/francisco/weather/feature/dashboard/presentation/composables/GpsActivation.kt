package com.francisco.weather.feature.dashboard.presentation.composables

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

/**
 * Crea y recuerda el callback que pide activar los servicios de ubicación del dispositivo.
 * En éxito (ya activos, o el usuario los enciende en el diálogo del sistema) invoca [onGpsEnabled];
 * el refresco real del estado GPS lo hace el observer ON_RESUME de la pantalla.
 * Declarado ANTES de permissionLauncher en la pantalla para evitar referencia adelantada.
 */
@Composable
fun rememberPromptEnableGps(onGpsEnabled: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val gpsSettingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { /* Estado GPS refrescado por el observer ON_RESUME */ }

    return {
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).build()
        val settingsReq = LocationSettingsRequest.Builder().addLocationRequest(req).build()
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(settingsReq)
            .addOnSuccessListener { onGpsEnabled() }
            .addOnFailureListener { ex ->
                if (ex is ResolvableApiException) {
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
}
