package com.main.hoopradar.ui.screen.run

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.main.hoopradar.data.model.Run
import com.main.hoopradar.ui.common.AppScaffold
import com.main.hoopradar.ui.component.GlassCard
import com.main.hoopradar.ui.component.PrimaryButton
import com.main.hoopradar.ui.theme.*
import com.main.hoopradar.viewmodel.JoinRunState
import com.main.hoopradar.viewmodel.RunsViewModel

@Composable
fun RunDetailsScreen(
    run: Run,
    onBack: () -> Unit,
    runsViewModel: RunsViewModel = viewModel()
) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val joinRunState by runsViewModel.joinRunState.collectAsState()

    val isAlreadyIn = run.playerIds.contains(currentUid)
    val isPending = run.pendingPlayerIds.contains(currentUid)
    val isOwner = run.creatorUID == currentUid

    // Refresh run list after a successful join/request so UI reflects new state
    LaunchedEffect(joinRunState) {
        if (joinRunState is JoinRunState.Joined || joinRunState is JoinRunState.Requested) {
            runsViewModel.resetJoinRunState()
        }
    }

    AppScaffold(title = "Run Details", showBackButton = true, onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DeepNavy, DarkSurface)))
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    RunDetailRow(Icons.Filled.LocationOn, "Court", run.courtName.ifBlank { "Unknown" })
                    HorizontalDivider(color = GlassBorder)
                    RunDetailRow(Icons.Filled.AccessTime, "Time", run.dateTime.ifBlank { "TBD" })
                    HorizontalDivider(color = GlassBorder)
                    RunDetailRow(Icons.Filled.Group, "Players", "${run.currentPlayers} / ${run.maxPlayers}")
                }
            }

            // Skill level + access type badges
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (run.skillLevel.isNotBlank()) {
                    Badge(label = run.skillLevel)
                }
                Badge(
                    label = if (run.inviteOnly) "Invite Only" else "Open to All",
                    icon = if (run.inviteOnly) Icons.Filled.Lock else Icons.Filled.LockOpen
                )
            }

            if (run.notes.isNotBlank()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Notes", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        Text(run.notes, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    }
                }
            }

            // Pending approvals section (only visible to owner)
            if (isOwner && run.pendingPlayerIds.isNotEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Join Requests (${run.pendingPlayerIds.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = HoopOrange
                        )
                        Text(
                            "${run.pendingPlayerIds.size} player(s) waiting for approval",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Join / status button
            when {
                isOwner -> {
                    // Owner doesn't join their own run
                }
                isAlreadyIn -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(HoopOrange.copy(alpha = 0.15f))
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("You're in this run", color = HoopOrangeLight, style = MaterialTheme.typography.titleMedium)
                    }
                }
                isPending -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(GlassWhite)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Request sent — awaiting approval", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
                    }
                }
                joinRunState is JoinRunState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = HoopOrange)
                    }
                }
                else -> {
                    PrimaryButton(
                        text = if (run.inviteOnly) "Request to Join" else "Join Run",
                        onClick = { runsViewModel.joinOrRequest(run, currentUid) }
                    )
                }
            }

            if (joinRunState is JoinRunState.Error) {
                Text(
                    (joinRunState as JoinRunState.Error).message,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun Badge(label: String, icon: ImageVector? = null) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(HoopOrange.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = HoopOrangeLight, modifier = Modifier.size(12.dp))
            }
            Text(label, style = MaterialTheme.typography.labelMedium, color = HoopOrangeLight)
        }
    }
}

@Composable
private fun RunDetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = HoopOrange, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
        }
    }
}
