package ge.btu.flowershop.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.flowershop.data.AuthRepository
import ge.btu.flowershop.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
)

/** Handles the sign-in / sign-up actions for the auth screens. */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    val isConfigured: Boolean get() = repository.isConfigured

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun signIn(email: String, password: String) = launch { repository.signInEmail(email, password) }

    fun signUp(name: String, email: String, password: String, role: UserRole) =
        launch { repository.signUpEmail(name, email, password, role) }

    fun signInWithGoogle(idToken: String) = launch { repository.signInWithGoogle(idToken) }

    /** Surface an error raised outside the ViewModel (e.g. a cancelled Google dialog). */
    fun showError(message: String) {
        _state.value = AuthUiState(isLoading = false, error = message)
    }

    private fun launch(block: suspend () -> Unit) {
        _state.value = AuthUiState(isLoading = true)
        viewModelScope.launch {
            runCatching { block() }
                .onSuccess { _state.value = AuthUiState(isLoading = false) }
                .onFailure {
                    _state.value = AuthUiState(isLoading = false, error = it.message ?: "Something went wrong")
                }
        }
    }
}
