package ge.btu.flowershop.ui.customer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ge.btu.flowershop.data.model.Order
import ge.btu.flowershop.ui.common.OsmMap
import ge.btu.flowershop.ui.common.rememberGeocoded
import org.osmdroid.util.GeoPoint

/** Customer live-tracking map: shows the courier's reported position + the delivery address. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerTrackingScreen(order: Order, onBack: () -> Unit) {
    val courier = if (order.hasCourierLocation) GeoPoint(order.courierLat, order.courierLng) else null
    val destination = rememberGeocoded(order.deliveryAddress)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track ${order.shortId}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        if (courier != null) "${order.courierName.ifBlank { "Your courier" }} is on the way 🚚"
                        else "Waiting for the courier to start sharing their location…",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            if (courier != null || destination != null) {
                OsmMap(courier = courier, destination = destination, modifier = Modifier.fillMaxSize())
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "The map appears once the courier is en route.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
