package ge.btu.flowershop.ui.common

import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * An OpenStreetMap (osmdroid) map rendered through Compose interop — no XML layout, no
 * findViewById, no API key. Shows a courier pin and/or a destination pin.
 */
@Composable
fun OsmMap(
    courier: GeoPoint?,
    destination: GeoPoint?,
    modifier: Modifier = Modifier,
    zoom: Double = 15.0,
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        // osmdroid requires a user agent before it will fetch tiles.
        val prefs = context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        Configuration.getInstance().load(context, prefs)
        Configuration.getInstance().userAgentValue = context.packageName
        onDispose { }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(zoom)
                (courier ?: destination)?.let { controller.setCenter(it) }
            }
        },
        update = { map ->
            map.overlays.clear()
            destination?.let {
                map.overlays.add(
                    Marker(map).apply {
                        position = it
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Delivery address"
                    },
                )
            }
            courier?.let {
                map.overlays.add(
                    Marker(map).apply {
                        position = it
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Courier"
                    },
                )
            }
            (courier ?: destination)?.let { map.controller.animateTo(it) }
            map.invalidate()
        },
        onRelease = { it.onDetach() },
    )
}

/** Best-effort geocode of a free-text address to a map point (null if it can't be resolved). */
@Composable
fun rememberGeocoded(address: String): GeoPoint? {
    val context = LocalContext.current
    var point by remember(address) { mutableStateOf<GeoPoint?>(null) }
    LaunchedEffect(address) {
        if (address.isBlank()) return@LaunchedEffect
        point = withContext(Dispatchers.IO) {
            runCatching {
                @Suppress("DEPRECATION")
                Geocoder(context).getFromLocationName(address, 1)
                    ?.firstOrNull()
                    ?.let { GeoPoint(it.latitude, it.longitude) }
            }.getOrNull()
        }
    }
    return point
}
