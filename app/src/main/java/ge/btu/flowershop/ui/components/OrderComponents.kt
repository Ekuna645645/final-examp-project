package ge.btu.flowershop.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ge.btu.flowershop.data.model.OrderStatus

private val DeliveredGreen = Color(0xFF2E7D32)

/** A pill showing the order's current status. */
@Composable
fun OrderStatusChip(status: OrderStatus) {
    val bg: Color
    val fg: Color
    when (status) {
        OrderStatus.PLACED -> {
            bg = MaterialTheme.colorScheme.secondaryContainer
            fg = MaterialTheme.colorScheme.onSecondaryContainer
        }
        OrderStatus.ACCEPTED -> {
            bg = MaterialTheme.colorScheme.tertiaryContainer
            fg = MaterialTheme.colorScheme.onTertiaryContainer
        }
        OrderStatus.OUT_FOR_DELIVERY -> {
            bg = MaterialTheme.colorScheme.primaryContainer
            fg = MaterialTheme.colorScheme.onPrimaryContainer
        }
        OrderStatus.DELIVERED -> {
            bg = DeliveredGreen
            fg = Color.White
        }
        OrderStatus.CANCELLED -> {
            bg = MaterialTheme.colorScheme.errorContainer
            fg = MaterialTheme.colorScheme.onErrorContainer
        }
    }
    Surface(color = bg, shape = RoundedCornerShape(50)) {
        Text(
            status.label,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

/** Dots-and-connectors progress through the order pipeline. */
@Composable
fun OrderStatusTimeline(status: OrderStatus, modifier: Modifier = Modifier) {
    if (status == OrderStatus.CANCELLED) {
        Text(
            "This order was cancelled.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = modifier,
        )
        return
    }
    val steps = OrderStatus.pipeline
    val current = steps.indexOf(status).coerceAtLeast(0)
    val active = MaterialTheme.colorScheme.primary
    val inactive = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            steps.forEachIndexed { index, _ ->
                Box(
                    Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(if (index <= current) active else inactive),
                )
                if (index < steps.lastIndex) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(3.dp)
                            .background(if (index < current) active else inactive),
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            status.label,
            style = MaterialTheme.typography.labelMedium,
            color = active,
            fontWeight = FontWeight.Medium,
        )
    }
}
