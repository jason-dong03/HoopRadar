package com.main.hoopradar.ui.screen.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.main.hoopradar.ui.common.AppScaffold
import com.main.hoopradar.ui.component.GlassCard
import com.main.hoopradar.ui.theme.*
import com.main.hoopradar.viewmodel.AuthViewModel
import com.main.hoopradar.viewmodel.PhotoUploadState
import com.main.hoopradar.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val profile by profileViewModel.profile.collectAsState()
    val uploadState by profileViewModel.photoUploadState.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var showSignOutConfirm by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) profileViewModel.uploadProfilePhoto(uri)
    }

    LaunchedEffect(uploadState) {
        if (uploadState is PhotoUploadState.Success) {
            kotlinx.coroutines.delay(1500)
            profileViewModel.resetPhotoUploadState()
        }
    }

    // Edit skill level dialog
    if (showEditDialog) {
        SkillLevelDialog(
            current = profile.skillLevel,
            onConfirm = { newLevel ->
                profileViewModel.updateProfile(profile.copy(skillLevel = newLevel))
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    // Sign out confirmation
    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            containerColor = DarkElevated,
            title = { Text("Sign Out", color = TextPrimary) },
            text = { Text("Are you sure you want to sign out?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.signOut()
                    onSignOut()
                }) { Text("Sign Out", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirm = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    AppScaffold(title = "Profile", showBackButton = true, onBack = onBack) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DeepNavy, DarkSurface)))
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .border(2.dp, HoopOrange, CircleShape)
                        .background(HoopOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!profile.photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = profile.photoUrl,
                            contentDescription = "Profile photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = HoopOrange, modifier = Modifier.size(44.dp))
                    }
                    if (uploadState is PhotoUploadState.Loading) {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = HoopOrange, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(profile.name.ifBlank { "Player" }, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                Text(
                    profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 28.dp)
                )

                // Info card with Edit button in the header
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Skill Level", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    profile.skillLevel.ifBlank { "Not set" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(HoopOrange.copy(alpha = 0.15f))
                                        .clickable { showEditDialog = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = HoopOrange, modifier = Modifier.size(12.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Edit", style = MaterialTheme.typography.labelSmall, color = HoopOrange)
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = GlassBorder)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Email", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text(profile.email.ifBlank { "Not set" }, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                when (val state = uploadState) {
                    is PhotoUploadState.Success -> Text("Photo updated!", color = HoopOrange, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                    is PhotoUploadState.Error -> Text(state.message, color = ErrorRed, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 8.dp))
                    else -> {}
                }

                Button(
                    onClick = { photoPicker.launch("image/*") },
                    enabled = uploadState !is PhotoUploadState.Loading,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HoopOrange,
                        contentColor = Color.White,
                        disabledContainerColor = HoopOrange.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                    )
                ) {
                    Text(
                        if (uploadState is PhotoUploadState.Loading) "Uploading…" else "Update Profile Photo",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Sign out button
                OutlinedButton(
                    onClick = { showSignOutConfirm = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(ErrorRed.copy(alpha = 0.5f))
                    )
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun SkillLevelDialog(
    current: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val levels = listOf("Beginner", "Intermediate", "Advanced")
    var selected by remember { mutableStateOf(if (current in levels) current else levels[0]) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkElevated,
        title = { Text("Edit Skill Level", color = TextPrimary) },
        text = {
            Box {
                OutlinedTextField(
                    value = selected,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HoopOrange,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = GlassWhite,
                        unfocusedContainerColor = GlassWhite
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth().background(DarkElevated)
                ) {
                    levels.forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level, color = if (level == selected) HoopOrange else TextPrimary) },
                            onClick = { selected = level; expanded = false }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) { Text("Save", color = HoopOrange) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}
