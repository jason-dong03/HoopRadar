package com.main.hoopradar.data.repository

import com.main.hoopradar.data.model.UserProfile

class UserRepository {
    fun getMockProfile(): UserProfile {
        return UserProfile(
            id = "user1",
            name = "Jason Dong",
            email = "jason@example.com",
            skillLevel = "Intermediate"
        )
    }
}