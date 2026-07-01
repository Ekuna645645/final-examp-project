package ge.btu.flowershop.data

import com.google.firebase.firestore.FirebaseFirestore
import ge.btu.flowershop.data.model.AppUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/** All users (admin management), backed by Firestore `users` with a [LocalUserStore] fallback. */
class UserRepository {

    private val db: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    val isConfigured: Boolean get() = db != null

    fun observeAll(): Flow<List<AppUser>> {
        val d = db ?: return LocalUserStore.users
        return callbackFlow {
            val registration = d.collection(USERS).addSnapshotListener { snapshot, _ ->
                val users = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AppUser::class.java)?.copy(uid = doc.id)
                }?.sortedBy { it.name } ?: emptyList()
                trySend(users)
            }
            awaitClose { registration.remove() }
        }
    }

    suspend fun setRole(uid: String, role: String) {
        val d = db ?: return LocalUserStore.setRole(uid, role)
        d.collection(USERS).document(uid).update("role", role).await()
    }

    private companion object {
        const val USERS = "users"
    }
}
