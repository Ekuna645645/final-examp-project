package ge.btu.flowershop.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ge.btu.flowershop.data.model.Ticket
import ge.btu.flowershop.data.model.TicketStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/** Complaint tickets backed by Firestore `tickets`, falling back to [LocalTicketStore]. */
class TicketRepository {

    private val db: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    val isConfigured: Boolean get() = db != null

    private fun collection() = db?.collection(TICKETS)

    private fun queryFlow(query: Query): Flow<List<Ticket>> = callbackFlow {
        val registration = query.addSnapshotListener { snapshot, _ ->
            val tickets = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Ticket::class.java)?.copy(id = doc.id)
            }?.sortedByDescending { it.createdAt } ?: emptyList()
            trySend(tickets)
        }
        awaitClose { registration.remove() }
    }

    fun observeAll(): Flow<List<Ticket>> =
        collection()?.let { queryFlow(it) }
            ?: LocalTicketStore.tickets.map { list -> list.sortedByDescending { it.createdAt } }

    fun observeForCustomer(uid: String): Flow<List<Ticket>> =
        collection()?.let { queryFlow(it.whereEqualTo("customerId", uid)) }
            ?: LocalTicketStore.tickets.map { list ->
                list.filter { it.customerId == uid }.sortedByDescending { it.createdAt }
            }

    suspend fun create(ticket: Ticket): String {
        val stamped = ticket.copy(createdAt = System.currentTimeMillis())
        val col = collection() ?: return LocalTicketStore.create(stamped)
        val ref = col.document()
        ref.set(stamped).await()
        return ref.id
    }

    suspend fun resolve(ticketId: String, reply: String) {
        val col = collection()
        if (col == null) {
            LocalTicketStore.update(ticketId) { it.copy(status = TicketStatus.RESOLVED.name, reply = reply) }
            return
        }
        col.document(ticketId).update(mapOf("status" to TicketStatus.RESOLVED.name, "reply" to reply)).await()
    }

    private companion object {
        const val TICKETS = "tickets"
    }
}
