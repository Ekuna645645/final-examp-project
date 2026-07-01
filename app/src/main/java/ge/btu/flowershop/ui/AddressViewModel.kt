package ge.btu.flowershop.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.flowershop.data.AddressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/** Saved delivery addresses for the current customer. */
class AddressViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AddressRepository()

    fun addresses(uid: String): Flow<List<String>> = repository.observe(uid)

    fun add(uid: String, address: String) {
        if (address.isBlank()) return
        viewModelScope.launch {
            runCatching { repository.add(uid, address.trim()) }
                .onFailure { Log.e(TAG, "add address failed", it) }
        }
    }

    fun remove(uid: String, address: String) {
        viewModelScope.launch {
            runCatching { repository.remove(uid, address) }
                .onFailure { Log.e(TAG, "remove address failed", it) }
        }
    }

    private companion object {
        const val TAG = "AddressViewModel"
    }
}
