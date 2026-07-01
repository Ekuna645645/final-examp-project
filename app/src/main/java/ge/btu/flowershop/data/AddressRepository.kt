package ge.btu.flowershop.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import ge.btu.flowershop.data.model.AppUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** Saved delivery addresses on `users/{uid}.addresses`, with a [LocalAddressStore] fallback. */
class AddressRepository {

    private val db: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()

    fun observe(uid: String): Flow<List<String>> {
        val d = db ?: return LocalAddressStore.observe(uid)
        return callbackFlow {
            val registration = d.collection(USERS).document(uid).addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(AppUser::class.java)?.addresses ?: emptyList())
            }
            awaitClose { registration.remove() }
        }
    }

    suspend fun add(uid: String, address: String) {
        val d = db ?: return LocalAddressStore.add(uid, address)
        d.collection(USERS).document(uid).update("addresses", FieldValue.arrayUnion(address)).await()
    }

    suspend fun remove(uid: String, address: String) {
        val d = db ?: return LocalAddressStore.remove(uid, address)
        d.collection(USERS).document(uid).update("addresses", FieldValue.arrayRemove(address)).await()
    }

    private companion object {
        const val USERS = "users"
    }
}
