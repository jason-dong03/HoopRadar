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

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {


    private val auth = FirebaseModule.auth
    private val db = FirebaseModule.firestore
    private val googleAuthClient = GoogleAuthClient(application, auth)

    private val _uiState = MutableStateFlow(
        AuthUiState(isSignedIn = auth.currentUser != null)
    )
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signInWithGoogle(webClientId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = googleAuthClient.signIn(webClientId)

            _uiState.value = if (result.isSuccess) {
                saveUserToFirestore()
                AuthUiState(
                    isLoading = false,
                    isSignedIn = true,
                    errorMessage = null
                )
            } else {
                AuthUiState(
                    isLoading = false,
                    isSignedIn = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private suspend fun saveUserToFirestore() {
        val user = auth.currentUser ?: return

        val userMap = mapOf(
            "uid" to user.uid,
            "name" to (user.displayName ?: ""),
            "email" to (user.email ?: ""),
            "photoUrl" to (user.photoUrl?.toString() ?: "")
        )

        db.collection("users")
            .document(user.uid)
            .set(userMap)
            .await()
    }

    fun signOut() {
        googleAuthClient.signOut()
        _uiState.value = AuthUiState(isSignedIn = false)
    }
}