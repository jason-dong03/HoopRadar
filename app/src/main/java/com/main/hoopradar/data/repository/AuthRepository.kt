package com.main.hoopradar.data.repository

import com.main.hoopradar.data.remote.FirebaseModule
import com.google.firebase.auth.FirebaseUser

class AuthRepository {
    fun currentUser(): FirebaseUser? = FirebaseModule.auth.currentUser

    fun signOut() {
        FirebaseModule.auth.signOut()
    }
}