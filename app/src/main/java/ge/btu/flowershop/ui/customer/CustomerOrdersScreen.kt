package ge.btu.flowershop.ui.customer

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
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.Order
import ge.btu.flowershop.ui.OrderViewModel
import ge.btu.flowershop.ui.common.EmptyState
import ge.btu.flowershop.ui.common.ScreenHeader
import ge.btu.flowershop.ui.components.OrderStatusChip
import ge.btu.flowershop.ui.components.OrderStatusTimeline

/** "Orders" tab: the customer's current and past orders with a live status timeline. */
@Composable
fun CustomerOrdersScreen(orderViewModel: OrderViewModel, user: AppUser) {
    val ordersFlow = remember(user.uid) { orderViewModel.ordersForCustomer(user.uid) }
    val orders by ordersFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Your orders", if (orders.isEmpty()) "No orders yet" else "${orders.size} order(s)")
        if (orders.isEmpty()) {
            EmptyState(
                title = "No orders yet",
                message = "Place an order from the Shop tab and track it here.",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(orders, key = { it.id }) { order -> CustomerOrderCard(order) }
            }
        }
    }
}

@Composable
private fun CustomerOrderCard(order: Order) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(order.shortId, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OrderStatusChip(order.orderStatus)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "${order.itemCount} item(s)  ·  ${order.totalLabel}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (order.courierName.isNotBlank()) {
                Text(
                    "Courier: ${order.courierName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(14.dp))
            OrderStatusTimeline(order.orderStatus)
        }
    }
}
