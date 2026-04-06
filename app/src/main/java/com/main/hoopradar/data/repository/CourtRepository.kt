package com.main.hoopradar.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.main.hoopradar.data.model.Court
import kotlinx.coroutines.tasks.await

class CourtRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getCourts(): List<Court> {
        val snapshot = db.collection("courts").get().await()

        Log.d("CourtRepository", "Fetched ${snapshot.documents.size} court docs from Firestore")

        return snapshot.documents.mapNotNull { doc ->
            val latitude = doc.getDouble("latitude")
            val longitude = doc.getDouble("longitude")

            Log.d(
                "CourtRepository",
                "doc=${doc.id}, name=${doc.getString("name")}, lat=$latitude, lng=$longitude, address=${doc.getString("address")}"
            )

            if (latitude == null || longitude == null) {
                Log.d("CourtRepository", "Skipping ${doc.id} because latitude/longitude is null")
                return@mapNotNull null
            }

            Court(
                id = doc.id,
                name = doc.getString("name") ?: "",
                address = doc.getString("address") ?: "",
                latitude = latitude,
                longitude = longitude,
                isIndoor = doc.getBoolean("isIndoor") ?: false
            )
        }
    }
}