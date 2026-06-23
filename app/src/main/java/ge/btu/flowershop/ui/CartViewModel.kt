package ge.btu.flowershop.ui

import androidx.lifecycle.ViewModel
import ge.btu.flowershop.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CartItem(val product: Product, val quantity: Int) {
    val lineTotal: Double get() = product.price * quantity
}

/** In-memory shopping cart, shared across the customer screens. */
class CartViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun add(product: Product, quantity: Int = 1) {
        _items.value = _items.value.toMutableList().also { list ->
            val index = list.indexOfFirst { it.product.id == product.id }
            if (index >= 0) {
                list[index] = list[index].copy(quantity = list[index].quantity + quantity)
            } else {
                list.add(CartItem(product, quantity))
            }
        }
    }

    fun setQuantity(productId: String, quantity: Int) {
        _items.value = _items.value.mapNotNull { item ->
            if (item.product.id != productId) item
            else if (quantity <= 0) null
            else item.copy(quantity = quantity)
        }
    }

    fun remove(productId: String) = setQuantity(productId, 0)

    fun clear() {
        _items.value = emptyList()
    }
}
