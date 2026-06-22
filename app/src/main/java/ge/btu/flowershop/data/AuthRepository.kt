package ge.btu.flowershop.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.UserRole
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Wraps Firebase Authentication and the `users` Firestore collection.
 * Guards against Firebase not being configured yet (no google-services.json) so the
 * project still builds and runs.
 */
class AuthRepository {

    private val auth: FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()
    private val db: FirebaseFirestore? = runCatching { FirebaseFirestore.getInstance() }.getOrNull()

    val isConfigured: Boolean get() = auth != null && db != null
    val currentUid: String? get() = auth?.currentUser?.uid

    /** Streams the Firebase auth user (null when signed out / unconfigured). */
    fun authState(): Flow<FirebaseUser?> = callbackFlow {
        val a = auth
        if (a == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        a.addAuthStateListener(listener)
        awaitClose { a.removeAuthStateListener(listener) }
    }

    /** Streams the Firestore profile doc for a user. */
    fun observeUser(uid: String): Flow<AppUser?> = callbackFlow {
        val d = db
        if (d == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
        val registration = d.collection(USERS).document(uid)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.toObject(AppUser::class.java)?.copy(uid = uid))
            }
        awaitClose { registration.remove() }
    }

    suspend fun signInEmail(email: String, password: String) {
        val a = auth ?: error("Firebase is not configured")
        a.signInWithEmailAndPassword(email.trim(), password).await()
    }

    suspend fun signUpEmail(name: String, email: String, password: String, role: UserRole) {
        val a = auth ?: error("Firebase is not configured")
        val result = a.createUserWithEmailAndPassword(email.trim(), password).await()
        val uid = result.user?.uid ?: error("Sign-up failed")
        writeUserDoc(uid, name.trim(), email.trim(), role)
    }

    /** Signs in with a Google ID token, creating a customer profile on first sign-in. */
    suspend fun signInWithGoogle(idToken: String) {
        val a = auth ?: error("Firebase is not configured")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = a.signInWithCredential(credential).await()
        val user = result.user ?: error("Google sign-in failed")
        ensureUserDoc(user.uid, user.displayName.orEmpty(), user.email.orEmpty())
    }

    fun signOut() = auth?.signOut()

    private suspend fun writeUserDoc(uid: String, name: String, email: String, role: UserRole) {
        val d = db ?: return
        val appUser = AppUser(
            uid = uid,
            name = name,
            email = email,
            role = role.name,
            createdAt = System.currentTimeMillis(),
        )
        d.collection(USERS).document(uid).set(appUser).await()
    }

    private suspend fun ensureUserDoc(uid: String, name: String, email: String) {
        val d = db ?: return
        val ref = d.collection(USERS).document(uid)
        if (!ref.get().await().exists()) {
            writeUserDoc(uid, name, email, UserRole.CUSTOMER)
        }
    }

    private companion object {
        const val USERS = "users"
    }
}
