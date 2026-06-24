package ge.btu.flowershop.data.model

import com.google.firebase.firestore.Exclude

enum class TicketStatus {
    OPEN, RESOLVED;

    val label: String get() = if (this == OPEN) "Open" else "Resolved"

    companion object {
        fun from(value: String?): TicketStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OPEN
    }
}

/** A customer complaint/support ticket, stored in Firestore at `tickets/{id}`. */
data class Ticket(
    @get:Exclude val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val subject: String = "",
    val message: String = "",
    val status: String = TicketStatus.OPEN.name,
    val reply: String = "",
    val createdAt: Long = 0L,
) {
    @get:Exclude
    val ticketStatus: TicketStatus get() = TicketStatus.from(status)
}
