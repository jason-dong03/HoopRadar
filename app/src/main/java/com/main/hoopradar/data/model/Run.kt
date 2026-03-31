package com.main.hoopradar.data.model

data class Run(
    val id: String = "",
    val courtId: String = "",
    val courtName: String = "",
    val creatorId: String = "",
    val dateTime: String = "",
    val maxPlayers: Int = 10,
    val currentPlayers: Int = 0,
    val skillLevel: String = "All Levels",
    val notes: String = "",
    val photoUrl: String? = null,
    val playerIds: List<String> = emptyList()
)