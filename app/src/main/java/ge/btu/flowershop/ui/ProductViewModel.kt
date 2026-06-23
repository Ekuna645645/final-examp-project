package ge.btu.flowershop.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.flowershop.data.ProductRepository
import ge.btu.flowershop.data.model.Product
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Shared products ViewModel used by both the customer shop and the admin product manager. */
class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository()
    val isConfigured: Boolean get() = repository.isConfigured

    val products: StateFlow<List<Product>> =
        repository.observeProducts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun productById(id: String): Product? = products.value.firstOrNull { it.id == id }

    fun save(product: Product) = viewModelScope.launch {
        runCatching { repository.upsertProduct(product) }
            .onFailure { Log.e(TAG, "save product failed", it) }
    }

    fun delete(id: String) = viewModelScope.launch {
        runCatching { repository.deleteProduct(id) }
            .onFailure { Log.e(TAG, "delete product failed", it) }
    }

    fun seedDemo() = viewModelScope.launch {
        runCatching { repository.seedDemoProducts() }
            .onFailure { Log.e(TAG, "seed failed", it) }
    }

    private companion object {
        const val TAG = "ProductViewModel"
    }
}
