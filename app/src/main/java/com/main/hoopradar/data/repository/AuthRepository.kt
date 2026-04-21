package com.main.hoopradar.data.repository

import com.main.hoopradar.data.remote.FirebaseModule
import com.google.firebase.auth.FirebaseUser

class AuthRepository { // repository that handles authentication related actions
    fun currentUser(): FirebaseUser? = FirebaseModule.auth.currentUser // returns the currently signed-in user

    fun signOut() { // signs the current user out of firebase
        FirebaseModule.auth.signOut()
    }
}