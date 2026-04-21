package com.main.hoopradar.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.main.hoopradar.data.model.UserProfile
import kotlinx.coroutines.tasks.await

class UserRepository( // repository for user profile data and profile photos
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(), // firebase authentication instance
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(), // firebase database instance
    private val storage: FirebaseStorage = FirebaseStorage.getInstance() // firebase storage instance
) {

    suspend fun getCurrentUserProfile(): UserProfile? { // gets the currently signed in user's profile
        val uid = auth.currentUser?.uid ?: return null // get logged in user id
        val doc = db.collection("users").document(uid).get().await() // fetch user document from firestore
        if (!doc.exists()) return null // return null if no profile exists
        return UserProfile( // convert firestore document into userprofile object
            uid = doc.getString("uid") ?: uid,
            name = doc.getString("name") ?: "",
            email = doc.getString("email") ?: "",
            photoUrl = doc.getString("photoUrl") ?: "",
            skillLevel = doc.getString("skillLevel") ?: "Beginner"
        )
    }

    suspend fun updateUserProfile(profile: UserProfile) { // updates or replaces the user's profile in firestore
        db.collection("users").document(profile.uid).set(profile).await()
    }

    /** Uploads [imageUri] to Storage, updates photoUrl in Firestore, returns the download URL. */
    suspend fun uploadProfilePhoto(uid: String, imageUri: Uri): String { // uploads profile image to firebase storage
        val ref = storage.reference.child("profile_photos/$uid.jpg") // storage path for profile image
        ref.putFile(imageUri).await() // upload selected image file
        val downloadUrl = ref.downloadUrl.await().toString() // get downloadable image url
        db.collection("users").document(uid) // save photo url in firestore user document
            .update("photoUrl", downloadUrl)
            .await()
        return downloadUrl
    }
}
