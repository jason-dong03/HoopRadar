package com.main.hoopradar.ui.screen.map

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.main.hoopradar.ui.component.GlassCard
import com.main.hoopradar.ui.theme.*
import com.main.hoopradar.viewmodel.CourtsViewModel
import com.main.hoopradar.viewmodel.RunsViewModel

@Composable
fun NearbyCourtsScreen(
    onBack: () -> Unit,
    onCourtClick: () -> Unit,
    onRunClick: (runId: String) -> Unit,
    courtsViewModel: CourtsViewModel = viewModel(),
    runsViewModel: RunsViewModel = viewModel()
) {
    val courts by courtsViewModel.courts.collectAsState()
    val runs by runsViewModel.runs.collectAsState()

    LaunchedEffect(courts, runs) {
        Log.d("NearbyCourtsScreen", "courts count = ${courts.size}")
        Log.d("NearbyCourtsScreen", "runs count = ${runs.size}")
        courts.forEach { court ->
            Log.d("NearbyCourtsScreen", "court: id=${court.id}, name=${court.name}, lat=${court.latitude}, lng=${court.longitude}, address=${court.address}")
        }
        runs.forEach { run ->
            Log.d("NearbyCourtsScreen", "run: courtId=${run.courtId}, dateTime=${run.dateTime}, players=${run.currentPlayers}/${run.maxPlayers}")
        }
    }

    var selectedCourtId by remember { mutableStateOf<String?>(null) }
    val runsByCourtId = runs.groupBy { it.courtId }

    val uvaLatLng = LatLng(38.0356, -78.5034)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uvaLatLng, 13f)
    }
    var mapReady by remember { mutableStateOf(false) }

    fun isValidCoord(lat: Double, lng: Double) =
        lat in -90.0..90.0 && lng in -180.0..180.0 && (lat != 0.0 || lng != 0.0)

    // Only move the camera after the map is fully initialized — calling CameraUpdateFactory
    // before onMapLoaded fires throws NullPointerException
    LaunchedEffect(courts, mapReady) {
        if (!mapReady) return@LaunchedEffect
        val firstValidCourt = courts.firstOrNull { isValidCoord(it.latitude, it.longitude) }
        if (firstValidCourt != null) {
            Log.d("NearbyCourtsScreen", "Moving camera to first valid court: ${firstValidCourt.name}")
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(LatLng(firstValidCourt.latitude, firstValidCourt.longitude), 15f)
            )
        } else {
            Log.d("NearbyCourtsScreen", "No valid courts found, staying centered on UVA")
        }
    }

    val selectedCourt = courts.find { it.id == selectedCourtId }
    val selectedCourtRuns = runsByCourtId[selectedCourtId].orEmpty()

    Box(modifier = Modifier.fillMaxSize().background(DeepNavy)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepNavy)
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    text = "Nearby Courts",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }

            // Map
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                cameraPositionState = cameraPositionState,
                onMapLoaded = { mapReady = true }
            ) {
                courts.forEach { court ->
                    if (isValidCoord(court.latitude, court.longitude)) {
                        Log.d("NearbyCourtsScreen", "Rendering marker for ${court.name} at ${court.latitude}, ${court.longitude}")
                        key(court.id) {
                            Marker(
                                state = rememberMarkerState(position = LatLng(court.latitude, court.longitude)),
                                title = court.name,
                                snippet = court.address,
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE),
                                onClick = { selectedCourtId = court.id; false }
                            )
                        }
                    } else {
                        Log.d("NearbyCourtsScreen", "Skipping marker for ${court.name} because coordinates are invalid")
                    }
                }
            }

            // Bottom panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(DarkSurface)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(GlassBorder)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(12.dp))

                if (selectedCourt != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = HoopOrange, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(selectedCourt.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    }

                    if (selectedCourtRuns.isEmpty()) {
                        Text("No active runs at this court.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedCourtRuns) { run ->
                                val cardAlpha = if (run.isFull) 0.45f else 1f
                                GlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .alpha(cardAlpha)
                                        .then(
                                            if (!run.isFull) Modifier.clickable { onRunClick(run.id) }
                                            else Modifier
                                        )
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(run.dateTime, style = MaterialTheme.typography.titleMedium, color = if (run.isFull) TextMuted else TextPrimary)
                                            if (run.isFull) {
                                                Text("Full", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                                            }
                                        }
                                        Text(
                                            "${run.currentPlayers}/${run.maxPlayers} players · ${run.skillLevel}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextMuted
                                        )
                                        if (run.notes.isNotBlank()) {
                                            Text(run.notes, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        "Courts near you",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(courts) { court ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCourtId = court.id }
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = HoopOrange, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(court.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                        Text(court.address, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                    }
                                    val runCount = runsByCourtId[court.id]?.size ?: 0
                                    if (runCount > 0) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(HoopOrange)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                "$runCount run${if (runCount > 1) "s" else ""}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
