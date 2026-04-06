package com.main.hoopradar.data.model

data class Court(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isIndoor: Boolean = true
)