package ge.btu.flowershop.data

import com.google.firebase.firestore.FirebaseFirestore
import ge.btu.flowershop.data.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/**
 * Products backed by the Firestore `products` collection. Before Firebase is configured
 * it serves the [DemoData] catalog so the shop is browsable immediately.
 */
class ProductRepository {

    private val db: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    val isConfigured: Boolean get() = db != null

    fun observeProducts(): Flow<List<Product>> {
        val d = db ?: return flowOf(DemoData.products)
        return callbackFlow {
            val registration = d.collection(PRODUCTS).addSnapshotListener { snapshot, _ ->
                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }?.sortedBy { it.createdAt } ?: emptyList()
                trySend(products)
            }
            awaitClose { registration.remove() }
        }
    }

    suspend fun upsertProduct(product: Product) {
        val d = db ?: return
        if (product.id.isBlank()) {
            val ref = d.collection(PRODUCTS).document()
            ref.set(product.copy(createdAt = System.currentTimeMillis())).await()
        } else {
            d.collection(PRODUCTS).document(product.id).set(product).await()
        }
    }

    suspend fun deleteProduct(id: String) {
        db?.collection(PRODUCTS)?.document(id)?.delete()?.await()
    }

    /** One-tap seeding of the demo catalog into Firestore (admin convenience). */
    suspend fun seedDemoProducts() {
        val d = db ?: return
        DemoData.products.forEachIndexed { index, product ->
            d.collection(PRODUCTS).document("demo-${index + 1}")
                .set(product.copy(createdAt = System.currentTimeMillis() + index))
                .await()
        }
    }

    private companion object {
        const val PRODUCTS = "products"
    }
}
