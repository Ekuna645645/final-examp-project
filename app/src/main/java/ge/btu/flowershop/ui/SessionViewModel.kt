package ge.btu.flowershop.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.flowershop.data.AuthRepository
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.UserRole
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Top-level auth state that drives which part of the app is shown. */
sealed interface SessionState {
    data object Loading : SessionState
    data object SignedOut : SessionState
    data class SignedIn(val user: AppUser) : SessionState
}

/**
 * Observes Firebase auth + the signed-in user's Firestore profile and exposes a single
 * [SessionState]. Also supports a dev-only role preview so the role screens can be viewed
 * before Firebase is configured.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    val isConfigured: Boolean get() = repository.isConfigured

    private val previewUser = MutableStateFlow<AppUser?>(null)

    private val baseState: Flow<SessionState> =
        repository.authState().flatMapLatest { firebaseUser ->
            if (firebaseUser == null) {
                flowOf(SessionState.SignedOut)
            } else {
                repository.observeUser(firebaseUser.uid).map { appUser ->
                    SessionState.SignedIn(
                        appUser ?: AppUser(uid = firebaseUser.uid, email = firebaseUser.email.orEmpty()),
                    )
                }
            }
        }

    val sessionState: StateFlow<SessionState> =
        combine(baseState, previewUser) { base, preview ->
            if (preview != null) SessionState.SignedIn(preview) else base
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionState.Loading)

    /** Dev-only: jump straight into a role's UI without real auth (used before Firebase setup). */
    fun previewAs(role: UserRole) {
        previewUser.value = AppUser(
            uid = "preview-${role.name.lowercase()}",
            name = "Preview ${role.name.lowercase().replaceFirstChar { it.uppercase() }}",
            email = "preview@bloom.dev",
            role = role.name,
        )
    }

    fun signOut() {
        previewUser.value = null
        repository.signOut()
    }
}
