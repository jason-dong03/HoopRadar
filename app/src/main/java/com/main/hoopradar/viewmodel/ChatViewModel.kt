package com.main.hoopradar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.main.hoopradar.data.model.Message
import com.main.hoopradar.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(private val runId: String) : ViewModel() { // viewmodel that manages chat messages for one specific run

    private val repo = MessageRepository() // repository used to read and send messages

    private val _messages = MutableStateFlow<List<Message>>(emptyList()) // holds current list of chat messages
    val messages: StateFlow<List<Message>> = _messages // read only messages state for ui

    private val _sendError = MutableStateFlow<String?>(null) // holds error message if sending fails
    val sendError: StateFlow<String?> = _sendError

    init {
        viewModelScope.launch { // start listening for real time message updates when viewmodel is created
            repo.messagesFlow(runId).collect { _messages.value = it }
        }
    }

    fun send(senderId: String, senderName: String, text: String) { // sends a new chat message
        if (text.isBlank()) return // do nothing if message is blank
        viewModelScope.launch {
            try { // save trimmed message text to firestore
                repo.sendMessage(runId, Message(senderId = senderId, senderName = senderName, text = text.trim()))
            } catch (e: Exception) { // update error stat if sending fails
                _sendError.value = "Failed to send message"
            }
        }
    }

    class Factory(private val runId: String) : ViewModelProvider.Factory { // factory used to create chatviewmodel with runid parameter
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ChatViewModel(runId) as T
    }
}
