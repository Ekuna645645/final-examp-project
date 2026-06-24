package ge.btu.flowershop.data

import ge.btu.flowershop.data.model.Ticket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** In-memory ticket store used as a fallback before Firebase is configured (process singleton). */
object LocalTicketStore {

    private var counter = 1
    private val _tickets = MutableStateFlow<List<Ticket>>(emptyList())
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    @Synchronized
    fun create(ticket: Ticket): String {
        val id = "ticket-${counter++}"
        _tickets.value = _tickets.value + ticket.copy(id = id)
        return id
    }

    @Synchronized
    fun update(id: String, transform: (Ticket) -> Ticket) {
        _tickets.value = _tickets.value.map { if (it.id == id) transform(it) else it }
    }
}
