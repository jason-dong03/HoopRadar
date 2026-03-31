package com.main.hoopradar.ui.screen.run

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RunDetailsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Run Details")
        Text("Court: Emmet Street Courts")
        Text("Time: Tonight 6:00 PM")
        Text("Players: 6/10")
        Text("Skill: Intermediate")
        Text("Roster: Jason, Mike, Chris...")
        Button(onClick = { }) { Text("Join Run") }
        Button(onClick = onBack) { Text("Back") }
    }
}