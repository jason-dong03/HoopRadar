package com.main.hoopradar.data.repository

import com.main.hoopradar.data.model.Court

class CourtRepository {
    fun getMockCourts(): List<Court> {
        return listOf(
            Court(
                id = "1",
                name = "AFC Gym Courts",
                address = "UVA Charlottesville, VA",
                latitude = 38.0356,
                longitude = -78.5034,
                distanceMiles = 0.8
            ),
            Court(
                id = "2",
                name = "Memorial Gym Courts",
                address = "UVA Rec Center",
                latitude = 38.0365,
                longitude = -78.5070,
                distanceMiles = 1.2
            )
        )
    }
}