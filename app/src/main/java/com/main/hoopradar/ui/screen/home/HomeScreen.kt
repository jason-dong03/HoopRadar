package com.main.hoopradar.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.main.hoopradar.data.model.Run
import com.main.hoopradar.ui.component.GlassCard
import com.main.hoopradar.ui.theme.*
import com.main.hoopradar.viewmodel.RunsViewModel

@Composable
fun HomeScreen(
    onNearbyCourtsClick: () -> Unit,
    onCreateRunClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRunChatClick: (runId: String, courtName: String) -> Unit,
    runsViewModel: RunsViewModel = viewModel()
) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val allRuns by runsViewModel.runs.collectAsState()
    val myRuns = remember(allRuns, currentUid) {
        allRuns.filter { it.playerIds.contains(currentUid) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepNavy, DarkSurface, DeepNavy)))
            .statusBarsPadding()
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Spacer(Modifier.height(32.dp))
            Text(
                text = "HoopRadar",
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = HoopOrange
            )
            Text(
                text = "Find courts. Join runs. Ball out.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )
        }

        item { HomeNavCard(Icons.Filled.Map, "Nearby Courts", "Find courts & active runs near you", onNearbyCourtsClick) }
        item { Spacer(Modifier.height(12.dp)) }
        item { HomeNavCard(Icons.Filled.Add, "Create Run", "Organize a pickup game", onCreateRunClick) }
        item { Spacer(Modifier.height(12.dp)) }
        item { HomeNavCard(Icons.Filled.Person, "Profile", "Your stats and settings", onProfileClick) }

        // My Runs section
        item {
            Spacer(Modifier.height(32.dp))
            Text(
                "My Runs",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary
            )
            Text(
                "Runs you've joined",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )
        }

        if (myRuns.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "You haven't joined any runs yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            items(myRuns, key = { it.id }) { run ->
                MyRunCard(
                    run = run,
                    onChatClick = { onRunChatClick(run.id, run.courtName) }
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun MyRunCard(run: Run, onChatClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        run.courtName.ifBlank { "Run" },
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        run.dateTime.ifBlank { "TBD" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        "${run.currentPlayers}/${run.maxPlayers} players · ${run.skillLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                // Access badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HoopOrange.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        if (run.inviteOnly) "Invite Only" else "Open",
                        style = MaterialTheme.typography.labelSmall,
                        color = HoopOrangeLight
                    )
                }
            }

            // Chat button
            OutlinedButton(
                onClick = onChatClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = HoopOrange),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(HoopOrange.copy(alpha = 0.5f))
                ),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Run Chat", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun HomeNavCard(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HoopOrange.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = HoopOrange, modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        }
    }
}
