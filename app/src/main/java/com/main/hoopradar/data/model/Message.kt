package com.main.hoopradar.data.model

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timestampMs: Long = 0L
)
