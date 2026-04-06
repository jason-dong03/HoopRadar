package com.main.hoopradar.ui.screen.run

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.main.hoopradar.data.model.Run
import com.main.hoopradar.ui.common.AppScaffold
import com.main.hoopradar.ui.component.PrimaryButton
import com.main.hoopradar.ui.theme.*
import com.main.hoopradar.viewmodel.CourtsViewModel
import com.main.hoopradar.viewmodel.CreateRunState
import com.main.hoopradar.viewmodel.RunsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRunScreen(
    onBack: () -> Unit,
    onRunCreated: () -> Unit,
    courtsViewModel: CourtsViewModel = viewModel(),
    runsViewModel: RunsViewModel = viewModel()
) {
    val courts by courtsViewModel.courts.collectAsState()
    val createRunState by runsViewModel.createRunState.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var courtDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCourtIndex by remember { mutableStateOf(-1) }
    var selectedDateMs by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var maxPlayers by remember { mutableStateOf("10") }
    var selectedSkill by remember { mutableStateOf("All Levels") }
    var notes by remember { mutableStateOf("") }
    var inviteOnly by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val skillLevels = listOf("All Levels", "Beginner", "Intermediate", "Advanced")

    // Build display string and scheduledTimestamp from selected date + time
    val scheduledTimestamp: Long? = remember(selectedDateMs, selectedHour, selectedMinute) {
        if (selectedDateMs != null && selectedHour != null && selectedMinute != null) {
            val cal = Calendar.getInstance().apply {
                timeInMillis = selectedDateMs!!
                set(Calendar.HOUR_OF_DAY, selectedHour!!)
                set(Calendar.MINUTE, selectedMinute!!)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            cal.timeInMillis
        } else null
    }
    val dateTimeDisplay: String = remember(scheduledTimestamp) {
        if (scheduledTimestamp != null) {
            SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault()).format(Date(scheduledTimestamp))
        } else ""
    }

    fun submit() {
        if (selectedCourtIndex < 0) { errorMessage = "Please select a court"; return }
        if (scheduledTimestamp == null) { errorMessage = "Please select a date & time"; return }
        if (currentUser == null) { errorMessage = "Not signed in"; return }

        errorMessage = null
        val court = courts[selectedCourtIndex]
        runsViewModel.createRun(
            Run(
                courtId = court.id,
                courtName = court.name,
                creatorUID = currentUser.uid,
                dateTime = dateTimeDisplay,
                scheduledTimestamp = scheduledTimestamp,
                maxPlayers = maxPlayers.toIntOrNull() ?: 10,
                currentPlayers = 1,
                skillLevel = selectedSkill,
                notes = notes,
                playerIds = listOf(currentUser.uid),
                inviteOnly = inviteOnly
            )
        )
    }

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMs ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMs = datePickerState.selectedDateMillis
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("Next", color = HoopOrange) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = TextSecondary) }
            },
            colors = DatePickerDefaults.colors(
                containerColor = DarkElevated,
                titleContentColor = TextPrimary,
                headlineContentColor = TextPrimary,
                weekdayContentColor = TextSecondary,
                subheadContentColor = TextSecondary,
                dayContentColor = TextPrimary,
                selectedDayContainerColor = HoopOrange,
                todayDateBorderColor = HoopOrange,
                todayContentColor = HoopOrange
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time picker dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour ?: 18,
            initialMinute = selectedMinute ?: 0,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = DarkElevated,
            title = { Text("Select Time", color = TextPrimary) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = DarkSurface,
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = TextSecondary,
                            selectorColor = HoopOrange,
                            containerColor = DarkElevated,
                            periodSelectorSelectedContainerColor = HoopOrange,
                            periodSelectorUnselectedContainerColor = GlassWhite,
                            periodSelectorSelectedContentColor = Color.White,
                            periodSelectorUnselectedContentColor = TextSecondary,
                            timeSelectorSelectedContainerColor = HoopOrange,
                            timeSelectorUnselectedContainerColor = GlassWhite,
                            timeSelectorSelectedContentColor = Color.White,
                            timeSelectorUnselectedContentColor = TextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) { Text("OK", color = HoopOrange) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }

    // Show success confirmation overlay
    if (createRunState is CreateRunState.Success) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DeepNavy, DarkSurface))),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = HoopOrange,
                    modifier = Modifier.size(72.dp)
                )
                Text(
                    "Run Posted!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    "Your run at ${courts.getOrNull(selectedCourtIndex)?.name ?: ""} has been posted.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                PrimaryButton(text = "Done") {
                    runsViewModel.resetCreateRunState()
                    onRunCreated()
                }
            }
        }
        return
    }

    // Show error from ViewModel
    if (createRunState is CreateRunState.Error) {
        errorMessage = (createRunState as CreateRunState.Error).message
        runsViewModel.resetCreateRunState()
    }

    AppScaffold(title = "Create Run", showBackButton = true, onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DeepNavy, DarkSurface)))
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Court selector
            Box {
                OutlinedTextField(
                    value = if (selectedCourtIndex >= 0) courts[selectedCourtIndex].name else "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Court") },
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = hoopTextFieldColors()
                )
                // Transparent overlay to capture clicks — OutlinedTextField consumes touch events
                // before a clickable modifier on it can fire
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { courtDropdownExpanded = true }
                )
                DropdownMenu(
                    expanded = courtDropdownExpanded,
                    onDismissRequest = { courtDropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkElevated)
                ) {
                    courts.forEachIndexed { index, court ->
                        DropdownMenuItem(
                            text = { Text(court.name, color = TextPrimary) },
                            onClick = {
                                selectedCourtIndex = index
                                courtDropdownExpanded = false
                                errorMessage = null
                            }
                        )
                    }
                }
            }

            // Date & time picker
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (selectedDateMs != null) HoopOrange else TextSecondary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (selectedDateMs != null) HoopOrange else GlassBorder)
                    )
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (selectedDateMs != null) SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(selectedDateMs!!)) else "Date")
                }
                OutlinedButton(
                    onClick = { if (selectedDateMs != null) showTimePicker = true else showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (selectedHour != null) HoopOrange else TextSecondary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(if (selectedHour != null) HoopOrange else GlassBorder)
                    )
                ) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (selectedHour != null) SimpleDateFormat("h:mm a", Locale.getDefault()).format(
                        Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, selectedHour!!); set(Calendar.MINUTE, selectedMinute!!) }.time
                    ) else "Time")
                }
            }
            if (dateTimeDisplay.isNotBlank()) {
                Text(dateTimeDisplay, style = MaterialTheme.typography.bodyMedium, color = HoopOrangeLight)
            }

            HoopTextField(
                value = maxPlayers,
                onValueChange = { maxPlayers = it },
                label = "Max Players"
            )

            // Skill level chips
            Text("Skill Level", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                skillLevels.forEach { level ->
                    FilterChip(
                        selected = selectedSkill == level,
                        onClick = { selectedSkill = level },
                        label = { Text(level, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HoopOrange,
                            selectedLabelColor = Color.White,
                            containerColor = GlassWhite,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedSkill == level,
                            borderColor = GlassBorder,
                            selectedBorderColor = HoopOrange
                        )
                    )
                }
            }

            // Invite only toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GlassWhite)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        if (inviteOnly) "Invite Only" else "Open to All",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Text(
                        if (inviteOnly) "Players request to join — you approve" else "Anyone can join instantly",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Switch(
                    checked = inviteOnly,
                    onCheckedChange = { inviteOnly = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = HoopOrange,
                        checkedTrackColor = HoopOrange.copy(alpha = 0.4f),
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = GlassBorder
                    )
                )
            }

            HoopTextField(
                value = notes,
                onValueChange = { notes = it },
                label = "Notes (optional)",
                singleLine = false
            )

            errorMessage?.let {
                Text(it, color = ErrorRed, style = MaterialTheme.typography.bodyMedium)
            }

            val isLoading = createRunState is CreateRunState.Loading
            PrimaryButton(
                text = if (isLoading) "Posting…" else "Post Run",
                onClick = { if (!isLoading) submit() }
            )
            Spacer(Modifier.height(16.dp))
        }

        // Full-screen loading overlay while Firestore write is in flight
        if (createRunState is CreateRunState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = HoopOrange)
            }
        }
    }
}

@Composable
private fun HoopTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = hoopTextFieldColors()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun hoopTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = HoopOrange,
    unfocusedBorderColor = GlassBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = HoopOrange,
    focusedLabelColor = HoopOrange,
    unfocusedLabelColor = TextSecondary,
    focusedContainerColor = GlassWhite,
    unfocusedContainerColor = GlassWhite
)
