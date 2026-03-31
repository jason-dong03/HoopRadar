package com.main.hoopradar.ui.screen.court

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.main.hoopradar.ui.common.AppScaffold

@Composable
fun CourtDetailsScreen(
    onBack: () -> Unit
) {
    AppScaffold(
        title = "Court Detail",
        showBackButton = true,
        onBack = onBack
    ) {
        Text("Court Details")
        Text("Name: Emmet Street Courts")
        Text("Address: UVA Charlottesville, VA")
        Text("Distance: 0.8 miles away")
        Text("Upcoming Runs: 2")
        Button(onClick = { }) { Text("Create Run at this Court") }
        Button(onClick = onBack) { Text("Back") }
    }
}