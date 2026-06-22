package ge.btu.flowershop.data.model

/** The three account types. Stored in Firestore as the uppercase enum name. */
enum class UserRole {
    CUSTOMER, COURIER, ADMIN;

    companion object {
        fun from(value: String?): UserRole =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: CUSTOMER
    }
}
