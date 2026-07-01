package ge.btu.flowershop.ui.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

private fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

/**
 * While composed, asks for location permission and streams the device GPS position via
 * FusedLocationProvider, calling [onLocation] with each (lat, lng). Stops on dispose.
 */
@Composable
fun LocationStreamingEffect(onLocation: (Double, Double) -> Unit) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(hasLocationPermission(context)) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { result -> granted = result || hasLocationPermission(context) }

    LaunchedEffect(Unit) {
        if (!granted) launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    DisposableEffect(granted) {
        if (!granted) return@DisposableEffect onDispose { }
        val client = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { onLocation(it.latitude, it.longitude) }
            }
        }
        runCatching { client.requestLocationUpdates(request, callback, Looper.getMainLooper()) }
        onDispose { client.removeLocationUpdates(callback) }
    }
}
