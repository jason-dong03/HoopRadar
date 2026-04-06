package com.main.hoopradar.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.main.hoopradar.data.model.UserProfile
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    suspend fun getCurrentUserProfile(): UserProfile? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = db.collection("users").document(uid).get().await()
        if (!doc.exists()) return null
        return UserProfile(
            uid = doc.getString("uid") ?: uid,
            name = doc.getString("name") ?: "",
            email = doc.getString("email") ?: "",
            photoUrl = doc.getString("photoUrl") ?: "",
            skillLevel = doc.getString("skillLevel") ?: "Beginner"
        )
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        db.collection("users").document(profile.uid).set(profile).await()
    }

    /** Uploads [imageUri] to Storage, updates photoUrl in Firestore, returns the download URL. */
    suspend fun uploadProfilePhoto(uid: String, imageUri: Uri): String {
        val ref = storage.reference.child("profile_photos/$uid.jpg")
        ref.putFile(imageUri).await()
        val downloadUrl = ref.downloadUrl.await().toString()
        db.collection("users").document(uid)
            .update("photoUrl", downloadUrl)
            .await()
        return downloadUrl
    }
}
