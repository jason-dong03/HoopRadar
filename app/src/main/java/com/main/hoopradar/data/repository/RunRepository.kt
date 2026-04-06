package com.main.hoopradar.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.main.hoopradar.data.model.Run
import kotlinx.coroutines.tasks.await

class RunRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getRuns(): List<Run> {
        val snapshot = db.collection("runs").get().await()
        val now = System.currentTimeMillis()

        // Delete expired runs (and their messages subcollection) from Firestore.
        // Runs with no scheduledTimestamp (legacy/0) are kept indefinitely.
        snapshot.documents
            .filter { doc ->
                val ts = doc.getLong("scheduledTimestamp") ?: 0L
                ts > 0L && ts < now
            }
            .forEach { doc -> deleteRunWithMessages(doc.reference.id) }

        return snapshot.documents.mapNotNull { doc ->
            val ts = doc.getLong("scheduledTimestamp") ?: 0L
            // Skip expired runs — they may still exist if the delete above hasn't propagated
            if (ts > 0L && ts < now) return@mapNotNull null
            Run(
                id = doc.id,
                courtId = doc.getString("court_id") ?: "",
                courtName = doc.getString("courtName") ?: "",
                dateTime = doc.getString("dateTime") ?: "",
                currentPlayers = (doc.getLong("currentPlayers") ?: 0).toInt(),
                maxPlayers = (doc.getLong("maxPlayers") ?: 0).toInt(),
                skillLevel = doc.getString("skillLevel") ?: "All Levels",
                notes = doc.getString("notes") ?: "",
                creatorUID = doc.getString("creatorUID") ?: "",
                photoUrl = doc.getString("photoUrl") ?: "",
                playerIds = (doc.get("playerIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                inviteOnly = doc.getBoolean("inviteOnly") ?: false,
                pendingPlayerIds = (doc.get("pendingPlayerIds") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                scheduledTimestamp = ts
            )
        }
    }

    suspend fun createRun(run: Run) {
        val data = mapOf(
            "court_id" to run.courtId,
            "courtName" to run.courtName,
            "creatorUID" to run.creatorUID,
            "dateTime" to run.dateTime,
            "maxPlayers" to run.maxPlayers,
            "currentPlayers" to run.currentPlayers,
            "skillLevel" to run.skillLevel,
            "notes" to run.notes,
            "playerIds" to run.playerIds,
            "inviteOnly" to run.inviteOnly,
            "pendingPlayerIds" to emptyList<String>(),
            "scheduledTimestamp" to run.scheduledTimestamp
        )
        db.collection("runs").add(data).await()
    }

    suspend fun joinRun(runId: String, userId: String) {
        db.collection("runs").document(runId).update(
            mapOf(
                "playerIds" to FieldValue.arrayUnion(userId),
                "currentPlayers" to FieldValue.increment(1)
            )
        ).await()
    }

    suspend fun requestJoin(runId: String, userId: String) {
        db.collection("runs").document(runId).update(
            "pendingPlayerIds", FieldValue.arrayUnion(userId)
        ).await()
    }

    /** Deletes a run document and all messages in its subcollection. */
    private suspend fun deleteRunWithMessages(runId: String) {
        val runRef = db.collection("runs").document(runId)
        // Delete all messages first, then the run document
        val messages = runRef.collection("messages").get().await()
        messages.documents.forEach { it.reference.delete() }
        runRef.delete().await()
    }
}
