package ge.btu.flowershop.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.flowershop.data.OrderRepository
import ge.btu.flowershop.data.model.Order
import ge.btu.flowershop.data.model.OrderStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Shared orders ViewModel used by the customer, admin and courier order screens. */
class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrderRepository()
    val isConfigured: Boolean get() = repository.isConfigured

    /** Admin live board. */
    val allOrders: StateFlow<List<Order>> =
        repository.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Courier "available to pick up". */
    val availableOrders: StateFlow<List<Order>> =
        repository.observeAvailable().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun ordersForCustomer(uid: String): Flow<List<Order>> = repository.observeForCustomer(uid)
    fun ordersForCourier(uid: String): Flow<List<Order>> = repository.observeForCourier(uid)

    fun placeOrder(order: Order, onPlaced: (String) -> Unit) {
        viewModelScope.launch {
            runCatching { repository.placeOrder(order) }
                .onSuccess { onPlaced(it) }
                .onFailure { Log.e(TAG, "placeOrder failed", it) }
        }
    }

    fun setStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            runCatching { repository.updateStatus(orderId, status) }
                .onFailure { Log.e(TAG, "updateStatus failed", it) }
        }
    }

    fun cancel(orderId: String) = setStatus(orderId, OrderStatus.CANCELLED)

    fun acceptOrder(orderId: String, courierId: String, courierName: String) {
        viewModelScope.launch {
            runCatching { repository.assignCourier(orderId, courierId, courierName) }
                .onFailure { Log.e(TAG, "acceptOrder failed", it) }
        }
    }

    private companion object {
        const val TAG = "OrderViewModel"
    }
}
