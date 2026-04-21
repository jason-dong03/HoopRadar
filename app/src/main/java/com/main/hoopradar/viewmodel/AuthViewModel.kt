package com.main.hoopradar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.main.hoopradar.auth.GoogleAuthClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import com.main.hoopradar.data.remote.FirebaseModule

data class AuthUiState( // holds ui state for authentication screen
    val isLoading: Boolean = false, // true while sign in request is running
    val isSignedIn: Boolean = false, // true when usr is signed in
    val errorMessage: String? = null // stores login error message if one occurs
)

class AuthViewModel(application: Application) : AndroidViewModel(application) { // viewmodel that manages login/logout logic


    private val auth = FirebaseModule.auth // firebase authentication instance
    private val db = FirebaseModule.firestore // firestore database instance
    private val googleAuthClient = GoogleAuthClient(application, auth) // google sign in helper class

    private val _uiState = MutableStateFlow( // mutable authentication state
        AuthUiState(isSignedIn = auth.currentUser != null)
    )
    val uiState: StateFlow<AuthUiState> = _uiState // read only state exposed to ui

    fun signInWithGoogle(webClientId: String) { // starts google sign in process
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy( // show loading spinner and clear errors
                isLoading = true,
                errorMessage = null
            )

            val result = googleAuthClient.signIn(webClientId) // attempt google sign in

            _uiState.value = if (result.isSuccess) {
                saveUserToFirestore() // save user info in firestore
                AuthUiState( // update ui as signed in
                    isLoading = false,
                    isSignedIn = true,
                    errorMessage = null
                )
            } else {
                AuthUiState( // show error if sign in failed
                    isLoading = false,
                    isSignedIn = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private suspend fun saveUserToFirestore() { // saves signed in user data to firestore
        val user = auth.currentUser ?: return
        val docRef = db.collection("users").document(user.uid)
        val snapshot = docRef.get().await()

        if (!snapshot.exists()) { // if first login, create new profile document
            docRef.set(mapOf(
                "uid" to user.uid,
                "name" to (user.displayName ?: ""),
                "email" to (user.email ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: ""),
                "skillLevel" to "Beginner"
            )).await()
        } else { // if returning user, update profile info
            docRef.update(mapOf(
                "name" to (user.displayName ?: ""),
                "email" to (user.email ?: ""),
                "photoUrl" to (user.photoUrl?.toString() ?: "")
            )).await()
        }
    }

    fun signOut() { // signs current user out
        googleAuthClient.signOut()
        _uiState.value = AuthUiState(isSignedIn = false) // reset ui state
    }
}