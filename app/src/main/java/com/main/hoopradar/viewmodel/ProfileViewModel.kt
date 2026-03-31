package com.main.hoopradar.viewmodel

import androidx.lifecycle.ViewModel
import com.main.hoopradar.data.model.UserProfile
import com.main.hoopradar.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(
    private val repo: UserRepository = UserRepository()
) : ViewModel() {
    private val _profile = MutableStateFlow(repo.getMockProfile())
    val profile: StateFlow<UserProfile> = _profile
}