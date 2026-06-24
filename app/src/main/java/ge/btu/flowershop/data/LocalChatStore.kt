package ge.btu.flowershop.data

import ge.btu.flowershop.data.model.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** In-memory chat store used before Firebase is configured (process singleton). */
object LocalChatStore {

    private var counter = 1
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    @Synchronized
    fun send(message: ChatMessage): String {
        val id = "msg-${counter++}"
        _messages.value = _messages.value + message.copy(id = id)
        return id
    }
}
