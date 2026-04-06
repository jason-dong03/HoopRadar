package com.main.hoopradar.data.model

data class Run(
    val id: String = "",
    val courtId: String = "",
    val courtName: String = "",
    val creatorUID: String = "",
    val dateTime: String = "",
    val maxPlayers: Int = 10,
    val currentPlayers: Int = 0,
    // "All Levels", "Beginner", "Intermediate", "Advanced"
    val skillLevel: String = "All Levels",
    val notes: String = "",
    val photoUrl: String? = null,
    val playerIds: List<String> = emptyList(),
    // true = owner must approve each join request
    val inviteOnly: Boolean = false,
    val pendingPlayerIds: List<String> = emptyList(),
    // Unix ms timestamp of when the run is scheduled — used for expiry
    val scheduledTimestamp: Long = 0L
) {
    val isFull: Boolean get() = maxPlayers > 0 && currentPlayers >= maxPlayers
}
