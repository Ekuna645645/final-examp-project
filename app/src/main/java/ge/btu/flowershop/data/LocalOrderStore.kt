package ge.btu.flowershop.data

import ge.btu.flowershop.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * In-memory order store used as a fallback before Firebase is configured. It's a process
 * singleton, so an order placed in the customer preview is visible in the admin and courier
 * previews during the same app run — enough to demo the full flow offline.
 */
object LocalOrderStore {

    private var counter = 1
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    @Synchronized
    fun place(order: Order): String {
        val id = "local-${counter++}"
        _orders.value = _orders.value + order.copy(id = id)
        return id
    }

    @Synchronized
    fun update(id: String, transform: (Order) -> Order) {
        _orders.value = _orders.value.map { if (it.id == id) transform(it) else it }
    }
}
