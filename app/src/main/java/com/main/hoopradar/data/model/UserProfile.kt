package com.main.hoopradar.data.model


data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val skillLevel: String = "Beginner" //keep for now, so we can sort runs based on skill? or maybe use ints
)