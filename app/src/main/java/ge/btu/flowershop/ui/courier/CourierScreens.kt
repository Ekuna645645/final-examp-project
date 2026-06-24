package ge.btu.flowershop.ui.courier

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.Order
import ge.btu.flowershop.data.model.OrderStatus
import ge.btu.flowershop.ui.OrderViewModel
import ge.btu.flowershop.ui.common.EmptyState
import ge.btu.flowershop.ui.common.ScreenHeader
import ge.btu.flowershop.ui.components.OrderStatusChip

/** Orders that are paid + unassigned, which any courier can accept. */
@Composable
fun CourierAvailableScreen(orderViewModel: OrderViewModel, user: AppUser) {
    val orders by orderViewModel.availableOrders.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Available orders", "${orders.size} ready to pick up")
        if (orders.isEmpty()) {
            EmptyState("Nothing available", "New paid orders appear here to accept.", Icons.Outlined.Inbox)
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(orders, key = { it.id }) { order ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(order.shortId, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                Text(order.totalLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(2.dp))
                            Text("${order.itemCount} item(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (order.deliveryAddress.isNotBlank()) {
                                Text("To: ${order.deliveryAddress}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { orderViewModel.acceptOrder(order.id, user.uid, user.name.ifBlank { user.email }) },
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text("Accept delivery") }
                        }
                    }
                }
            }
        }
    }
}

/** Orders assigned to this courier, with pickup / deliver actions. */
@Composable
fun CourierDeliveriesScreen(orderViewModel: OrderViewModel, user: AppUser) {
    val ordersFlow = remember(user.uid) { orderViewModel.ordersForCourier(user.uid) }
    val orders by ordersFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("My deliveries", "${orders.size} assigned")
        if (orders.isEmpty()) {
            EmptyState("No deliveries yet", "Accept an order from the Available tab.", Icons.Filled.LocalShipping)
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(orders, key = { it.id }) { order ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(order.shortId, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                OrderStatusChip(order.orderStatus)
                            }
                            if (order.deliveryAddress.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text("To: ${order.deliveryAddress}", style = MaterialTheme.typography.bodyMedium)
                            }
                            if (order.phone.isNotBlank()) {
                                Text(order.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            when (order.orderStatus) {
                                OrderStatus.ACCEPTED -> {
                                    Spacer(Modifier.height(12.dp))
                                    Button(onClick = { orderViewModel.setStatus(order.id, OrderStatus.OUT_FOR_DELIVERY) }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Picked up — start delivery")
                                    }
                                }
                                OrderStatus.OUT_FOR_DELIVERY -> {
                                    Spacer(Modifier.height(12.dp))
                                    Button(onClick = { orderViewModel.setStatus(order.id, OrderStatus.DELIVERED) }, modifier = Modifier.fillMaxWidth()) {
                                        Text("Mark delivered")
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}
