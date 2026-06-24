package ge.btu.flowershop.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ge.btu.flowershop.data.model.Ticket
import ge.btu.flowershop.data.model.TicketStatus
import ge.btu.flowershop.ui.TicketViewModel
import ge.btu.flowershop.ui.common.EmptyState
import ge.btu.flowershop.ui.common.ScreenHeader
import ge.btu.flowershop.ui.components.TicketCard

/** "Tickets" tab for admins: every complaint ticket, with reply & resolve. */
@Composable
fun AdminTicketsScreen(ticketViewModel: TicketViewModel) {
    val tickets by ticketViewModel.allTickets.collectAsStateWithLifecycle()
    var resolving by remember { mutableStateOf<Ticket?>(null) }
    val openCount = tickets.count { it.ticketStatus == TicketStatus.OPEN }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Support tickets", "$openCount open · ${tickets.size} total")
        if (tickets.isEmpty()) {
            EmptyState(
                title = "No tickets",
                message = "Customer complaint tickets show up here.",
                icon = Icons.Outlined.SupportAgent,
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(tickets, key = { it.id }) { ticket ->
                    TicketCard(ticket, showCustomer = true, onResolve = { resolving = ticket })
                }
            }
        }
    }

    resolving?.let { ticket ->
        var reply by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { resolving = null },
            title = { Text("Reply & resolve") },
            text = {
                Column {
                    Text(ticket.subject)
                    OutlinedTextField(
                        value = reply,
                        onValueChange = { reply = it },
                        label = { Text("Reply to the customer") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        ticketViewModel.resolve(ticket.id, reply.trim())
                        resolving = null
                    },
                    enabled = reply.isNotBlank(),
                ) { Text("Resolve") }
            },
            dismissButton = { TextButton(onClick = { resolving = null }) { Text("Cancel") } },
        )
    }
}
