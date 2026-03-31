package com.main.hoopradar.data.repository

import com.main.hoopradar.data.model.Run

class RunRepository {
    fun getMockRuns(): List<Run> {
        return listOf(
            Run(
                id = "run1",
                courtId = "1",
                courtName = "Emmet Street Courts",
                creatorId = "1",
                dateTime = "Tonight 6:00 PM",
                maxPlayers = 10,
                currentPlayers = 6,
                skillLevel = "Intermediate",
                notes = "Need 4 more"
            )
        )
    }
}