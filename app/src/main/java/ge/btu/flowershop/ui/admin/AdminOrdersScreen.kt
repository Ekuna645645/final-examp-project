package ge.btu.flowershop.ui.admin

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.btu.flowershop.data.model.Order
import ge.btu.flowershop.data.model.OrderStatus
import ge.btu.flowershop.ui.OrderViewModel
import ge.btu.flowershop.ui.common.EmptyState
import ge.btu.flowershop.ui.common.ScreenHeader
import ge.btu.flowershop.ui.components.OrderStatusChip
import ge.btu.flowershop.ui.components.OrderStatusTimeline

/** "Orders" tab for admins: a live board of every order. */
@Composable
fun AdminOrdersScreen(orderViewModel: OrderViewModel, onOpen: (String) -> Unit) {
    val orders by orderViewModel.allOrders.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Live orders", "${orders.size} total")
        if (orders.isEmpty()) {
            EmptyState(
                title = "No orders yet",
                message = "Orders placed by customers show up here in real time.",
                icon = Icons.AutoMirrored.Outlined.ReceiptLong,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(orders, key = { it.id }) { order ->
                    AdminOrderRow(order, onClick = { onOpen(order.id) })
                }
            }
        }
    }
}

@Composable
private fun AdminOrderRow(order: Order, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(order.shortId, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OrderStatusChip(order.orderStatus)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "${order.customerName.ifBlank { "Customer" }}  ·  ${order.itemCount} item(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                order.totalLabel,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/** Admin order detail with status controls (advance / cancel). Reads the live order. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(orderId: String, orderViewModel: OrderViewModel, onBack: () -> Unit) {
    val orders by orderViewModel.allOrders.collectAsStateWithLifecycle()
    val order = orders.firstOrNull { it.id == orderId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(order?.shortId ?: "Order") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        if (order == null) {
            EmptyState("Order not found", "It may have been removed.", modifier = Modifier.padding(padding))
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) { OrderStatusTimeline(order.orderStatus) }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Customer", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(order.customerName.ifBlank { "—" }, style = MaterialTheme.typography.titleMedium)
                    if (order.deliveryAddress.isNotBlank()) Text(order.deliveryAddress, style = MaterialTheme.typography.bodyMedium)
                    if (order.phone.isNotBlank()) Text(order.phone, style = MaterialTheme.typography.bodyMedium)
                    if (order.courierName.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text("Courier: ${order.courierName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Items", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    order.items.forEach { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                            Text("${item.quantity}×  ${item.name}", Modifier.weight(1f))
                            Text("$%.2f".format(item.lineTotal))
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Text("Total", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text(order.totalLabel, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            if (!order.orderStatus.isTerminal) {
                val index = OrderStatus.pipeline.indexOf(order.orderStatus)
                val next = OrderStatus.pipeline.getOrNull(index + 1)
                if (next != null) {
                    Button(onClick = { orderViewModel.setStatus(order.id, next) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Advance to ${next.label}")
                    }
                }
                OutlinedButton(
                    onClick = { orderViewModel.cancel(order.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                ) {
                    Text("Cancel order")
                }
            }
        }
    }
}
