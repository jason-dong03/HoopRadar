package com.main.hoopradar.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.main.hoopradar.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageRepository( // repository for chat message storage and retrieval
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance() // firestore database instance
) {
    fun messagesFlow(runId: String): Flow<List<Message>> = callbackFlow { // real time stream of messages for a specific run
        val listener = db.collection("runs") // listen for live updates from firestore
            .document(runId)
            .collection("messages")
            .orderBy("timestampMs", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener // stop if error occurs or snapshot is null
                val messages = snapshot.documents.mapNotNull { doc -> // convert firestore documents into message objects
                    Message(
                        id = doc.id,
                        senderId = doc.getString("senderId") ?: "",
                        senderName = doc.getString("senderName") ?: "",
                        text = doc.getString("text") ?: "",
                        timestampMs = doc.getLong("timestampMs") ?: 0L
                    )
                }
                trySend(messages) // send updated message list to Flow collectors
            }
        awaitClose { listener.remove() } // remove listener when is closed
    }

    suspend fun sendMessage(runId: String, message: Message) { // sends a new message to firestore
        val data = mapOf( // data map to store in firestore
            "senderId" to message.senderId,
            "senderName" to message.senderName,
            "text" to message.text,
            "timestampMs" to System.currentTimeMillis()
        ) // add message document to messages subcollection
        db.collection("runs")
            .document(runId)
            .collection("messages")
            .add(data)
            .await()
    }
}
