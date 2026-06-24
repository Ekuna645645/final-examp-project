package ge.btu.flowershop.data.model

import com.google.firebase.firestore.Exclude

/** One chat message tied to an order (customer ↔ courier). Stored in Firestore `messages`. */
data class ChatMessage(
    @get:Exclude val id: String = "",
    val orderId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "",
    val text: String = "",
    val createdAt: Long = 0L,
)
