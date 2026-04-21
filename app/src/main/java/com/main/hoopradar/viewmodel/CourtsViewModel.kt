package com.main.hoopradar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.main.hoopradar.data.model.Court
import com.main.hoopradar.data.repository.CourtRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourtsViewModel : ViewModel() { // viewmodel that manages basketball court data for the ui

    private val repo = CourtRepository() // repository used to fetch court data from firestore

    private val _courts = MutableStateFlow<List<Court>>(emptyList()) // holds list of courts
    val courts: StateFlow<List<Court>> = _courts // read only state exposed to ui

    init { // load courts when viewmodel is created
        loadCourts()
    }

    private fun loadCourts() { // loads court data from repository
        viewModelScope.launch {
            try { // update state with courts from firestore
                _courts.value = repo.getCourts()
            } catch (e: Exception) { // print error if loading fails
                e.printStackTrace()
            }
        }
    }
}