package com.main.hoopradar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.main.hoopradar.data.model.Message
import com.main.hoopradar.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val runId: String) : ViewModel() {

    private val repo = MessageRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _sendError = MutableStateFlow<String?>(null)
    val sendError: StateFlow<String?> = _sendError

    init {
        viewModelScope.launch {
            repo.messagesFlow(runId).collect { _messages.value = it }
        }
    }

    fun send(senderId: String, senderName: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            try {
                repo.sendMessage(runId, Message(senderId = senderId, senderName = senderName, text = text.trim()))
            } catch (e: Exception) {
                _sendError.value = "Failed to send message"
            }
        }
    }

    class Factory(private val runId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ChatViewModel(runId) as T
    }
}
