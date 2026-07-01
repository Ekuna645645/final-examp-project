package ge.btu.flowershop.data.model

import com.google.firebase.firestore.Exclude

/**
 * A Bloom account, stored in Firestore at `users/{uid}`. Defaults on every field let
 * Firestore deserialize via the required no-arg constructor.
 */
data class AppUser(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = UserRole.CUSTOMER.name,
    val phone: String = "",
    val addresses: List<String> = emptyList(),
    val createdAt: Long = 0L,
) {
    @get:Exclude
    val userRole: UserRole get() = UserRole.from(role)
}
