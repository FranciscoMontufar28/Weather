package com.francisco.weather.feature.dashboard.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.francisco.weather.feature.dashboard.domain.LocationProvider
import com.francisco.weather.feature.dashboard.domain.model.Coordinates
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production [LocationProvider] backed by [com.google.android.gms.location.FusedLocationProviderClient].
 *
 * Returns null (→ IP fallback) when:
 * - ACCESS_FINE_LOCATION permission is not granted, or
 * - GPS/network location is disabled, or
 * - no fix can be obtained from the device (getCurrentLocation + lastLocation both null/fail).
 *
 * Caches the last successful fix in [lastGood] so a transient failure after a prior GPS success
 * still returns plausible coordinates instead of falling back to IP.
 */
@Singleton
class DeviceLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : LocationProvider {

    private val fused = LocationServices.getFusedLocationProviderClient(context)

    @Volatile private var lastGood: Coordinates? = null

    @SuppressLint("MissingPermission") // guarded at runtime by hasFinePermission()
    override suspend fun currentLocation(): Coordinates? {
        if (!hasFinePermission() || !isGpsEnabled()) return null
        return try {
            val loc = fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
                ?: fused.lastLocation.await()
            loc?.let { Coordinates(it.latitude, it.longitude) }
                ?.also { lastGood = it }
                ?: lastGood
        } catch (_: Exception) {
            // SecurityException (permission revoked mid-flight), ApiException, network error, etc.
            lastGood
        }
    }

    private fun hasFinePermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    private fun isGpsEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lm.isLocationEnabled
        } else {
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }
}
