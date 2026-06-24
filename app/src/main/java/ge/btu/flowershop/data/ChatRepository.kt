package ge.btu.flowershop.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ge.btu.flowershop.data.model.ChatMessage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/** Per-order chat backed by Firestore `messages`, falling back to [LocalChatStore]. */
class ChatRepository {

    private val db: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()

    private fun collection() = db?.collection(MESSAGES)

    private fun queryFlow(query: Query): Flow<List<ChatMessage>> = callbackFlow {
        val registration = query.addSnapshotListener { snapshot, _ ->
            val messages = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(ChatMessage::class.java)?.copy(id = doc.id)
            }?.sortedBy { it.createdAt } ?: emptyList()
            trySend(messages)
        }
        awaitClose { registration.remove() }
    }

    fun observe(orderId: String): Flow<List<ChatMessage>> =
        collection()?.let { queryFlow(it.whereEqualTo("orderId", orderId)) }
            ?: LocalChatStore.messages.map { list ->
                list.filter { it.orderId == orderId }.sortedBy { it.createdAt }
            }

    suspend fun send(message: ChatMessage): String {
        val stamped = message.copy(createdAt = System.currentTimeMillis())
        val col = collection() ?: return LocalChatStore.send(stamped)
        val ref = col.document()
        ref.set(stamped).await()
        return ref.id
    }

    private companion object {
        const val MESSAGES = "messages"
    }
}
