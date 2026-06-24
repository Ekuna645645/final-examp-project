package ge.btu.flowershop.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.btu.flowershop.data.model.OrderStatus
import ge.btu.flowershop.ui.OrderViewModel
import ge.btu.flowershop.ui.ProductViewModel
import ge.btu.flowershop.ui.common.ScreenHeader

/** "Stats" tab for admins: revenue, order counts and an orders-by-status bar chart. */
@Composable
fun AdminStatsScreen(orderViewModel: OrderViewModel, productViewModel: ProductViewModel) {
    val orders by orderViewModel.allOrders.collectAsStateWithLifecycle()
    val products by productViewModel.products.collectAsStateWithLifecycle()

    val revenue = orders.filter { it.orderStatus != OrderStatus.CANCELLED }.sumOf { it.total }
    val delivered = orders.count { it.orderStatus == OrderStatus.DELIVERED }
    val active = orders.count { !it.orderStatus.isTerminal }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ScreenHeader("Statistics", "Your shop at a glance")
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Revenue (paid)", "$%.2f".format(revenue), Modifier.weight(1f))
                StatCard("Orders", "${orders.size}", Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Delivered", "$delivered", Modifier.weight(1f))
                StatCard("Products", "${products.size}", Modifier.weight(1f))
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Orders by status", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))
                    val maxCount = OrderStatus.entries.maxOf { s -> orders.count { it.orderStatus == s } }.coerceAtLeast(1)
                    OrderStatus.entries.forEach { status ->
                        StatusBar(status.label, orders.count { it.orderStatus == status }, maxCount)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Active orders right now", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    Text(
                        "$active",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(2.dp))
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StatusBar(label: String, count: Int, maxCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(110.dp))
        Box(
            Modifier
                .weight(1f)
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(fraction = count / maxCount.toFloat())
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
        Text(
            "$count",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(28.dp).padding(start = 8.dp),
        )
    }
}
