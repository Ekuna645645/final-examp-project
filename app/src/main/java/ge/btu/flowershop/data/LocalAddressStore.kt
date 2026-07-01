package ge.btu.flowershop.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** In-memory saved delivery addresses per user, used before Firebase is configured. */
object LocalAddressStore {

    private val byUser = MutableStateFlow<Map<String, List<String>>>(emptyMap())

    fun observe(uid: String): Flow<List<String>> = byUser.map { it[uid].orEmpty() }

    @Synchronized
    fun add(uid: String, address: String) {
        val current = byUser.value[uid].orEmpty()
        if (address.isNotBlank() && address !in current) {
            byUser.value = byUser.value + (uid to (current + address))
        }
    }

    @Synchronized
    fun remove(uid: String, address: String) {
        val current = byUser.value[uid].orEmpty()
        byUser.value = byUser.value + (uid to (current - address))
    }
}
