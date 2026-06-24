package ge.btu.flowershop.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import ge.btu.flowershop.data.model.Order
import ge.btu.flowershop.data.model.OrderStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Orders backed by the Firestore `orders` collection, falling back to [LocalOrderStore]
 * (in-memory) before Firebase is configured.
 */
class OrderRepository {

    private val db: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    val isConfigured: Boolean get() = db != null

    private fun collection() = db?.collection(ORDERS)

    private fun queryFlow(query: Query): Flow<List<Order>> = callbackFlow {
        val registration = query.addSnapshotListener { snapshot, _ ->
            val orders = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Order::class.java)?.copy(id = doc.id)
            }?.sortedByDescending { it.createdAt } ?: emptyList()
            trySend(orders)
        }
        awaitClose { registration.remove() }
    }

    /** All orders (admin live board). */
    fun observeAll(): Flow<List<Order>> =
        collection()?.let { queryFlow(it) }
            ?: LocalOrderStore.orders.map { list -> list.sortedByDescending { it.createdAt } }

    /** Orders belonging to one customer. */
    fun observeForCustomer(uid: String): Flow<List<Order>> =
        collection()?.let { queryFlow(it.whereEqualTo("customerId", uid)) }
            ?: LocalOrderStore.orders.map { list ->
                list.filter { it.customerId == uid }.sortedByDescending { it.createdAt }
            }

    /** Placed, unassigned orders a courier can pick up. */
    fun observeAvailable(): Flow<List<Order>> =
        collection()?.let { col ->
            queryFlow(col.whereEqualTo("status", OrderStatus.PLACED.name)).map { it.filter { o -> o.courierId.isBlank() } }
        } ?: LocalOrderStore.orders.map { list ->
            list.filter { it.orderStatus == OrderStatus.PLACED && it.courierId.isBlank() }
                .sortedByDescending { it.createdAt }
        }

    /** Orders assigned to a given courier. */
    fun observeForCourier(uid: String): Flow<List<Order>> =
        collection()?.let { queryFlow(it.whereEqualTo("courierId", uid)) }
            ?: LocalOrderStore.orders.map { list ->
                list.filter { it.courierId == uid }.sortedByDescending { it.createdAt }
            }

    suspend fun placeOrder(order: Order): String {
        val now = System.currentTimeMillis()
        val stamped = order.copy(createdAt = now, updatedAt = now)
        val col = collection() ?: return LocalOrderStore.place(stamped)
        val ref = col.document()
        ref.set(stamped).await()
        return ref.id
    }

    suspend fun updateStatus(orderId: String, status: OrderStatus) {
        val now = System.currentTimeMillis()
        val col = collection()
        if (col == null) {
            LocalOrderStore.update(orderId) { it.copy(status = status.name, updatedAt = now) }
            return
        }
        col.document(orderId).update(mapOf("status" to status.name, "updatedAt" to now)).await()
    }

    suspend fun assignCourier(orderId: String, courierId: String, courierName: String) {
        val now = System.currentTimeMillis()
        val col = collection()
        if (col == null) {
            LocalOrderStore.update(orderId) {
                it.copy(
                    courierId = courierId,
                    courierName = courierName,
                    status = OrderStatus.ACCEPTED.name,
                    updatedAt = now,
                )
            }
            return
        }
        col.document(orderId).update(
            mapOf(
                "courierId" to courierId,
                "courierName" to courierName,
                "status" to OrderStatus.ACCEPTED.name,
                "updatedAt" to now,
            ),
        ).await()
    }

    private companion object {
        const val ORDERS = "orders"
    }
}
