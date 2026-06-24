package ge.btu.flowershop.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ge.btu.flowershop.data.model.Ticket
import ge.btu.flowershop.data.model.TicketStatus

@Composable
fun TicketStatusChip(status: TicketStatus) {
    val bg = if (status == TicketStatus.OPEN) MaterialTheme.colorScheme.errorContainer else Color(0xFF2E7D32)
    val fg = if (status == TicketStatus.OPEN) MaterialTheme.colorScheme.onErrorContainer else Color.White
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

/**
 * Card for a complaint ticket. [showCustomer] adds the author line (admin view); [onResolve]
 * (when provided and the ticket is open) shows a resolve action.
 */
@Composable
fun TicketCard(ticket: Ticket, showCustomer: Boolean, onResolve: (() -> Unit)? = null) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    ticket.subject.ifBlank { "(no subject)" },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                TicketStatusChip(ticket.ticketStatus)
            }
            if (showCustomer && ticket.customerName.isNotBlank()) {
                Text(
                    "from ${ticket.customerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(ticket.message, style = MaterialTheme.typography.bodyMedium)

            if (ticket.reply.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            "Support reply",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(
                            ticket.reply,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                }
            }

            if (onResolve != null && ticket.ticketStatus == TicketStatus.OPEN) {
                TextButton(onClick = onResolve) { Text("Reply & resolve") }
            }
        }
    }
}
