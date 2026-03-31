package com.main.hoopradar.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel : ViewModel() {
    private val _userLat = MutableStateFlow(38.0336)
    private val _userLng = MutableStateFlow(-78.5080)

    val userLat: StateFlow<Double> = _userLat
    val userLng: StateFlow<Double> = _userLng

    fun updateLocation(lat: Double, lng: Double) {
        _userLat.value = lat
        _userLng.value = lng
    }
}