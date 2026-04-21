package com.main.hoopradar.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.main.hoopradar.data.model.Court
import kotlinx.coroutines.tasks.await

class CourtRepository( // repository for retrieving court data from firebase
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance() // firestore database instance
) {
    suspend fun getCourts(): List<Court> { // gets all courts from the 'courts' collection
        val snapshot = db.collection("courts").get().await() // downloads documents from firestore

        Log.d("CourtRepository", "Fetched ${snapshot.documents.size} court docs from Firestore") // log number of documents fetched

        return snapshot.documents.mapNotNull { doc -> // convert firestore documents into Court objects
            val latitude = doc.getDouble("latitude")
            val longitude = doc.getDouble("longitude")

            Log.d( // log document contents for debugging
                "CourtRepository",
                "doc=${doc.id}, name=${doc.getString("name")}, lat=$latitude, lng=$longitude, address=${doc.getString("address")}"
            )

            if (latitude == null || longitude == null) { // skip document if coordinates are missing
                Log.d("CourtRepository", "Skipping ${doc.id} because latitude/longitude is null")
                return@mapNotNull null
            }

            Court( // return mapped Court object
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