package ge.btu.flowershop.data.model

import com.google.firebase.firestore.Exclude

/**
 * A flower product, stored in Firestore at `products/{id}`. Defaults on every field let
 * Firestore deserialize via the required no-arg constructor. [id] comes from the doc id.
 */
data class Product(
    @get:Exclude val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val category: String = "Bouquets",
    val stock: Int = 0,
    val active: Boolean = true,
    val createdAt: Long = 0L,
) {
    @get:Exclude
    val priceLabel: String get() = "$%.2f".format(price)
}

/** Categories offered in the shop. */
val productCategories = listOf("Bouquets", "Tulips", "Roses", "Romance", "Premium", "Plants")
