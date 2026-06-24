package ge.btu.flowershop.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.flowershop.data.TicketRepository
import ge.btu.flowershop.data.model.Ticket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Shared ViewModel for complaint tickets (customer creates, admin resolves). */
class TicketViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TicketRepository()

    val allTickets: StateFlow<List<Ticket>> =
        repository.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun ticketsForCustomer(uid: String): Flow<List<Ticket>> = repository.observeForCustomer(uid)

    fun create(customerId: String, customerName: String, subject: String, message: String) {
        viewModelScope.launch {
            runCatching {
                repository.create(
                    Ticket(customerId = customerId, customerName = customerName, subject = subject, message = message),
                )
            }.onFailure { Log.e(TAG, "create ticket failed", it) }
        }
    }

    fun resolve(ticketId: String, reply: String) {
        viewModelScope.launch {
            runCatching { repository.resolve(ticketId, reply) }
                .onFailure { Log.e(TAG, "resolve ticket failed", it) }
        }
    }

    private companion object {
        const val TAG = "TicketViewModel"
    }
}
