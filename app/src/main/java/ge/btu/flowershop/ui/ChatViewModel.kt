package ge.btu.flowershop.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ge.btu.flowershop.data.ChatRepository
import ge.btu.flowershop.data.model.AppUser
import ge.btu.flowershop.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/** Per-order chat ViewModel shared by the customer and courier chat screens. */
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ChatRepository()

    fun messages(orderId: String): Flow<List<ChatMessage>> = repository.observe(orderId)

    fun send(orderId: String, sender: AppUser, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            runCatching {
                repository.send(
                    ChatMessage(
                        orderId = orderId,
                        senderId = sender.uid,
                        senderName = sender.name.ifBlank { sender.email },
                        senderRole = sender.role,
                        text = text.trim(),
                    ),
                )
            }.onFailure { Log.e(TAG, "send message failed", it) }
        }
    }

    private companion object {
        const val TAG = "ChatViewModel"
    }
}
