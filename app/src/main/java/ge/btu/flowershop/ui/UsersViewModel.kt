package ge.btu.flowershop.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.flowershop.data.UserRepository
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.UserRole
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Admin user management: list all accounts and change their roles. */
class UsersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository()
    val isConfigured: Boolean get() = repository.isConfigured

    val users: StateFlow<List<AppUser>> =
        repository.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setRole(uid: String, role: UserRole) {
        viewModelScope.launch {
            runCatching { repository.setRole(uid, role.name) }
                .onFailure { Log.e(TAG, "setRole failed", it) }
        }
    }

    private companion object {
        const val TAG = "UsersViewModel"
    }
}
