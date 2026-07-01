package ge.btu.flowershop.data

import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** In-memory users list (seeded with demo accounts) used before Firebase is configured. */
object LocalUserStore {

    private val _users = MutableStateFlow(
        listOf(
            AppUser(uid = "demo-nino", name = "Nino Beridze", email = "nino@bloom.dev", role = UserRole.CUSTOMER.name),
            AppUser(uid = "demo-gio", name = "Gio Kapanadze", email = "gio@bloom.dev", role = UserRole.COURIER.name),
            AppUser(uid = "demo-admin", name = "Admin", email = "admin@bloom.dev", role = UserRole.ADMIN.name),
        ),
    )
    val users: StateFlow<List<AppUser>> = _users.asStateFlow()

    @Synchronized
    fun setRole(uid: String, role: String) {
        _users.value = _users.value.map { if (it.uid == uid) it.copy(role = role) else it }
    }
}
