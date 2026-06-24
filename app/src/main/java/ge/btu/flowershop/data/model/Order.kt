package ge.btu.flowershop.data.model

import com.google.firebase.firestore.Exclude

/** Lifecycle of an order. PLACED -> ACCEPTED -> OUT_FOR_DELIVERY -> DELIVERED (or CANCELLED). */
enum class OrderStatus {
    PLACED, ACCEPTED, OUT_FOR_DELIVERY, DELIVERED, CANCELLED;

    val label: String
        get() = when (this) {
            PLACED -> "Placed"
            ACCEPTED -> "Accepted"
            OUT_FOR_DELIVERY -> "Out for delivery"
            DELIVERED -> "Delivered"
            CANCELLED -> "Cancelled"
        }

    val isTerminal: Boolean get() = this == DELIVERED || this == CANCELLED

    companion object {
        fun from(value: String?): OrderStatus =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: PLACED

        /** The happy-path steps shown in a progress timeline. */
        val pipeline = listOf(PLACED, ACCEPTED, OUT_FOR_DELIVERY, DELIVERED)
    }
}

/** A line item snapshot stored inside an order (so it survives product edits). */
data class OrderItem(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val imageUrl: String = "",
) {
    @get:Exclude
    val lineTotal: Double get() = price * quantity
}

/** A customer order, stored in Firestore at `orders/{id}`. */
data class Order(
    @get:Exclude val id: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val items: List<OrderItem> = emptyList(),
    val total: Double = 0.0,
    val status: String = OrderStatus.PLACED.name,
    val deliveryAddress: String = "",
    val phone: String = "",
    val courierId: String = "",
    val courierName: String = "",
    val paymentStatus: String = "PAID",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
) {
    @get:Exclude
    val orderStatus: OrderStatus get() = OrderStatus.from(status)

    @get:Exclude
    val itemCount: Int get() = items.sumOf { it.quantity }

    @get:Exclude
    val totalLabel: String get() = "$%.2f".format(total)

    @get:Exclude
    val shortId: String get() = if (id.length >= 5) "#${id.takeLast(5).uppercase()}" else "#$id"
}
