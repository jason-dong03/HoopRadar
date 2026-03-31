package com.main.hoopradar.ui.screen.run

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.main.hoopradar.ui.common.AppScaffold

@Composable
fun CreateRunScreen(
    onBack: () -> Unit,
    onRunCreated: () -> Unit
) {
    var time by remember { mutableStateOf("") }
    var players by remember { mutableStateOf("") }
    var skill by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AppScaffold(
        title = "Create Run",
        showBackButton = true,
        onBack = onBack
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Create Run")

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = players,
                onValueChange = { players = it },
                label = { Text("Players") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = skill,
                onValueChange = { skill = it },
                label = { Text("Skill Level") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { }) {
                Text("Take Run Photo")
            }

            Button(onClick = onRunCreated) {
                Text("Post Run")
            }
        }
    }
}