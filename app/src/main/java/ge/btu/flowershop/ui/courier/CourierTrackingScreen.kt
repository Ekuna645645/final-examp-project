package ge.btu.flowershop.ui.courier

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ge.btu.flowershop.data.model.Order
import ge.btu.flowershop.ui.OrderViewModel
import ge.btu.flowershop.ui.common.LocationStreamingEffect
import ge.btu.flowershop.ui.common.OsmMap
import ge.btu.flowershop.ui.common.rememberGeocoded
import org.osmdroid.util.GeoPoint

/** Courier map: streams live GPS to the order so the customer can watch the delivery. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierTrackingScreen(order: Order, orderViewModel: OrderViewModel, onBack: () -> Unit) {
    var myLocation by remember {
        mutableStateOf(if (order.hasCourierLocation) GeoPoint(order.courierLat, order.courierLng) else null)
    }
    val destination = rememberGeocoded(order.deliveryAddress)

    LocationStreamingEffect { lat, lng ->
        myLocation = GeoPoint(lat, lng)
        orderViewModel.updateCourierLocation(order.id, lat, lng)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deliver ${order.shortId}") },
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
                    Icon(Icons.Filled.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        if (myLocation != null) "Sharing your live location with the customer"
                        else "Getting your GPS location… (allow the permission)",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            OsmMap(courier = myLocation, destination = destination, modifier = Modifier.fillMaxSize())
        }
    }
}
