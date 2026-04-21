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

class ProfileViewModel : ViewModel() { // viewmodel that manages user profile data

    private val repo = UserRepository() // repository used for profile database/storage actions
    private val auth = FirebaseAuth.getInstance() // firebase auth used to get current user id

    private val _profile = MutableStateFlow(UserProfile()) // holds current user profile data
    val profile: StateFlow<UserProfile> = _profile // read only profile state for ui

    private val _photoUploadState = MutableStateFlow<PhotoUploadState>(PhotoUploadState.Idle) // holds profile photo upload status
    val photoUploadState: StateFlow<PhotoUploadState> = _photoUploadState // read only upload state for ui

    init { // load profile when viewmodel starts
        loadProfile()
    }

    fun loadProfile() { // loads current user profile from firestore
        viewModelScope.launch {
            try {
                val result = repo.getCurrentUserProfile()
                if (result != null) _profile.value = result // update state if profile exists
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateProfile(profile: UserProfile) { // saves updated profile data
        viewModelScope.launch {
            try {
                repo.updateUserProfile(profile)
                _profile.value = profile
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadProfilePhoto(imageUri: Uri) { // uploads new profile photo to firebase storage
        val uid = auth.currentUser?.uid ?: return // stop if no logged in user
        viewModelScope.launch {
            _photoUploadState.value = PhotoUploadState.Loading // show loading state
            try {
                val url = repo.uploadProfilePhoto(uid, imageUri) // upload image and get download url
                _profile.value = _profile.value.copy(photoUrl = url) // update profile photo in ui
                _photoUploadState.value = PhotoUploadState.Success // upload successful
            } catch (e: Exception) { // upload failed
                _photoUploadState.value = PhotoUploadState.Error(e.message ?: "Upload failed")
            }
        }
    }

    fun resetPhotoUploadState() { // represents profile photo upload states
        _photoUploadState.value = PhotoUploadState.Idle
    }
}

sealed class PhotoUploadState { // represents profile photo upload states
    object Idle : PhotoUploadState() // no upload currently
    object Loading : PhotoUploadState() // upload in progress
    object Success : PhotoUploadState() // upload completed successfully
    data class Error(val message: String) : PhotoUploadState() // upload failed with message
}
