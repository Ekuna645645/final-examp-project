package ge.btu.flowershop.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.flowershop.data.AuthRepository
import ge.btu.flowershop.data.model.AppUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
 * [SessionState]. The whole app reacts to this to route between auth and role screens.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    val isConfigured: Boolean get() = repository.isConfigured

    val sessionState: StateFlow<SessionState> =
        repository.authState()
            .flatMapLatest { firebaseUser ->
                if (firebaseUser == null) {
                    flowOf(SessionState.SignedOut)
                } else {
                    repository.observeUser(firebaseUser.uid).map { appUser ->
                        SessionState.SignedIn(
                            appUser ?: AppUser(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email.orEmpty(),
                            ),
                        )
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionState.Loading)

    fun signOut() = repository.signOut()
}
