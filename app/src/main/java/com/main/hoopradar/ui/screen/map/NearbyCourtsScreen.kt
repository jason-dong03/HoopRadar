package com.main.hoopradar.ui.screen.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.main.hoopradar.ui.common.AppScaffold
import com.main.hoopradar.viewmodel.CourtsViewModel
import com.main.hoopradar.viewmodel.RunsViewModel

@Composable
fun NearbyCourtsScreen(
    onBack: () -> Unit,
    onCourtClick: () -> Unit,
    onRunClick: () -> Unit,
    courtsViewModel: CourtsViewModel = viewModel(),
    runsViewModel: RunsViewModel = viewModel()
) {
    val courts by courtsViewModel.courts.collectAsState()
    val runs by runsViewModel.runs.collectAsState()

    AppScaffold(
        title = "Nearby Courts & Runs",
        showBackButton = true,
        onBack = onBack
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Map goes here using Google Maps Compose")

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(courts) { court ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onCourtClick() }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(court.name)
                            Text(court.address)
                            Text("${court.distanceMiles ?: "?"} miles away")
                        }
                    }
                }

                items(runs) { run ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onRunClick() }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(run.courtName)
                            Text(run.dateTime)
                            Text("${run.currentPlayers}/${run.maxPlayers} players")
                            Text(run.skillLevel)
                        }
                    }
                }
            }
        }
    }
}