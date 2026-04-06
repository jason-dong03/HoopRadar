package com.main.hoopradar.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.main.hoopradar.data.model.UserProfile
import com.main.hoopradar.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repo = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    private val _photoUploadState = MutableStateFlow<PhotoUploadState>(PhotoUploadState.Idle)
    val photoUploadState: StateFlow<PhotoUploadState> = _photoUploadState

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val result = repo.getCurrentUserProfile()
                if (result != null) _profile.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            try {
                repo.updateUserProfile(profile)
                _profile.value = profile
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadProfilePhoto(imageUri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _photoUploadState.value = PhotoUploadState.Loading
            try {
                val url = repo.uploadProfilePhoto(uid, imageUri)
                _profile.value = _profile.value.copy(photoUrl = url)
                _photoUploadState.value = PhotoUploadState.Success
            } catch (e: Exception) {
                _photoUploadState.value = PhotoUploadState.Error(e.message ?: "Upload failed")
            }
        }
    }

    fun resetPhotoUploadState() {
        _photoUploadState.value = PhotoUploadState.Idle
    }
}

sealed class PhotoUploadState {
    object Idle : PhotoUploadState()
    object Loading : PhotoUploadState()
    object Success : PhotoUploadState()
    data class Error(val message: String) : PhotoUploadState()
}
