package com.main.hoopradar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.hoopradar.data.model.Court
import com.main.hoopradar.data.repository.CourtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourtsViewModel : ViewModel() {

    private val repo = CourtRepository()

    private val _courts = MutableStateFlow<List<Court>>(emptyList())
    val courts: StateFlow<List<Court>> = _courts

    init {
        loadCourts()
    }

    private fun loadCourts() {
        viewModelScope.launch {
            try {
                _courts.value = repo.getCourts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}