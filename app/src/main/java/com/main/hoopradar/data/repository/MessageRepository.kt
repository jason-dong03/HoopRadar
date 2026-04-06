package com.main.hoopradar.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.main.hoopradar.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun messagesFlow(runId: String): Flow<List<Message>> = callbackFlow {
        val listener = db.collection("runs")
            .document(runId)
            .collection("messages")
            .orderBy("timestampMs", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener
                val messages = snapshot.documents.mapNotNull { doc ->
                    Message(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        senderName = doc.getString("senderName") ?: "",
                        text = doc.getString("text") ?: "",
                        timestampMs = doc.getLong("timestampMs") ?: 0L
                    )
                }
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(runId: String, message: Message) {
        val data = mapOf(
            "senderId" to message.senderId,
            "senderName" to message.senderName,
            "text" to message.text,
            "timestampMs" to System.currentTimeMillis()
        )
        db.collection("runs")
            .document(runId)
            .collection("messages")
            .add(data)
            .await()
    }
}
