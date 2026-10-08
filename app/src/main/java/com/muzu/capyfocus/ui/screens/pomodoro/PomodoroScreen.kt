package com.muzu.capyfocus.ui.screens.pomodoro

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muzu.capyfocus.R
import com.muzu.capyfocus.domain.models.PomodoroCategory
import com.muzu.capyfocus.domain.models.PomodoroConfig
import com.muzu.capyfocus.domain.models.PomodoroSessionType
import com.muzu.capyfocus.domain.models.Subject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isActive = uiState.isRunning || uiState.isPaused
    val targetColor = if (!isActive) {
        MaterialTheme.colorScheme.surface
    } else {
        when (uiState.sessionType) {
            PomodoroSessionType.WORK -> Color(0xFFB71C1C) // Red
            PomodoroSessionType.SHORT_BREAK -> Color(0xFF0D47A1) // Blue
            PomodoroSessionType.LONG_BREAK -> Color(0xFF1B5E20) // Green
        }
    }
    val animatedBackgroundColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 800),
        label = "PomodoroBgColor"
    )

    val textColor = if (isActive) Color.White else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackgroundColor)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.pomodoro_title), color = textColor) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    actions = {
                        IconButton(onClick = { viewModel.onEvent(PomodoroEvent.ToggleSettingsDialog) }) {
                            Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.pomodoro_settings), tint = textColor)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Session Type Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PomodoroSessionType.entries.forEach { type ->
                        val label = when (type) {
                            PomodoroSessionType.WORK -> stringResource(R.string.pomodoro_state_work)
                            PomodoroSessionType.SHORT_BREAK -> stringResource(R.string.pomodoro_state_short_break)
                            PomodoroSessionType.LONG_BREAK -> stringResource(R.string.pomodoro_state_long_break)
                        }
                        FilterChip(
                            modifier = Modifier.weight(1f),
                            selected = uiState.sessionType == type,
                            onClick = { viewModel.onEvent(PomodoroEvent.ChangeSessionType(type)) },
                            label = {
                                Text(
                                    text = label,
                                    maxLines = 2,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        )
                    }
                }

                // Timer display (Fixed position in layout, no conditional cards shifting it)
                val minutes = uiState.timeLeftSeconds / 60
                val seconds = uiState.timeLeftSeconds % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, fontWeight = FontWeight.Bold),
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.pomodoro_block_count, uiState.currentBlockNumber, uiState.config.blocksBeforeLongBreak),
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor.copy(alpha = 0.9f)
                    )
                }

                // Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.onEvent(PomodoroEvent.ResetTimer) },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.pomodoro_reset), modifier = Modifier.size(28.dp), tint = textColor)
                    }

                    Button(
                        onClick = {
                            if (uiState.isRunning) {
                                viewModel.onEvent(PomodoroEvent.PauseTimer)
                            } else if (uiState.isPaused) {
                                viewModel.onEvent(PomodoroEvent.ResumeTimer)
                            } else {
                                viewModel.onEvent(PomodoroEvent.StartTimer)
                            }
                        },
                        modifier = Modifier.size(width = 140.dp, height = 56.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        val icon = if (uiState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow
                        val text = if (uiState.isRunning) stringResource(R.string.pomodoro_pause) else if (uiState.isPaused) stringResource(R.string.pomodoro_resume) else stringResource(R.string.pomodoro_start)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(icon, contentDescription = null)
                            Text(text, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = { viewModel.onEvent(PomodoroEvent.SkipTimer) },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = stringResource(R.string.pomodoro_skip), modifier = Modifier.size(28.dp), tint = textColor)
                    }
                }
            }

            // Dialogs
            if (uiState.isActivitySelectionVisible) {
                ActivitySelectionDialog(
                    selectedCategory = uiState.selectedCategory,
                    selectedSubject = uiState.selectedSubject,
                    subjects = uiState.subjects,
                    onCategorySelected = { viewModel.onEvent(PomodoroEvent.SelectCategory(it)) },
                    onSubjectSelected = { viewModel.onEvent(PomodoroEvent.SelectSubject(it)) },
                    onAddSubjectClicked = { viewModel.onEvent(PomodoroEvent.ToggleAddSubjectDialog) },
                    onConfirm = { viewModel.onEvent(PomodoroEvent.ConfirmActivity) },
                    onDismiss = { viewModel.onEvent(PomodoroEvent.ToggleActivitySelectionDialog) }
                )
            }

            if (uiState.isAddSubjectDialogVisible) {
                AddSubjectDialog(
                    onDismiss = { viewModel.onEvent(PomodoroEvent.ToggleAddSubjectDialog) },
                    onSave = { viewModel.onEvent(PomodoroEvent.CreateSubject(it)) }
                )
            }

            if (uiState.isSettingsVisible) {
                PomodoroSettingsDialog(
                    config = uiState.config,
                    onDismiss = { viewModel.onEvent(PomodoroEvent.ToggleSettingsDialog) },
                    onSave = {
                        viewModel.onEvent(PomodoroEvent.UpdateConfig(it))
                        viewModel.onEvent(PomodoroEvent.ToggleSettingsDialog)
                    }
                )
            }
        }
    }
}

@Composable
fun ActivitySelectionDialog(
    selectedCategory: PomodoroCategory,
    selectedSubject: Subject?,
    subjects: List<Subject>,
    onCategorySelected: (PomodoroCategory) -> Unit,
    onSubjectSelected: (Subject?) -> Unit,
    onAddSubjectClicked: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val canConfirm = selectedCategory != PomodoroCategory.STUDY || selectedSubject != null || subjects.isEmpty()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pomodoro_select_activity)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Categoría", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    PomodoroCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { onCategorySelected(cat) },
                            label = { Text(cat.label, maxLines = 1) }
                        )
                    }
                }

                if (selectedCategory == PomodoroCategory.STUDY) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.pomodoro_subject_label), style = MaterialTheme.typography.labelLarge)
                    if (subjects.isEmpty()) {
                        Text("No hay materias creadas. Creá una para continuar.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    } else {
                        LazyColumn(modifier = Modifier.height(150.dp)) {
                            items(subjects) { subject ->
                                val isSelected = selectedSubject?.id == subject.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { onSubjectSelected(subject) }
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(subject.colorArgb))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(subject.name, style = MaterialTheme.typography.bodyLarge, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    }
                    TextButton(onClick = onAddSubjectClicked) {
                        Text(stringResource(R.string.pomodoro_add_subject))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = canConfirm
            ) {
                Text("Comenzar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.subjects_cancel))
            }
        }
    )
}

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.subjects_add)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.subjects_name_label)) },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onSave(name) }) {
                Text(stringResource(R.string.subjects_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.subjects_cancel))
            }
        }
    )
}

@Composable
fun PomodoroSettingsDialog(
    config: PomodoroConfig,
    onDismiss: () -> Unit,
    onSave: (PomodoroConfig) -> Unit
) {
    var workMin by remember { mutableStateOf(config.workDurationMinutes.toString()) }
    var shortBreakMin by remember { mutableStateOf(config.shortBreakDurationMinutes.toString()) }
    var longBreakMin by remember { mutableStateOf(config.longBreakDurationMinutes.toString()) }
    var blocksCount by remember { mutableStateOf(config.blocksBeforeLongBreak.toString()) }
    var autoStart by remember { mutableStateOf(config.autoStart) }
    var soundEnabled by remember { mutableStateOf(config.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(config.vibrationEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.pomodoro_settings)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = workMin,
                    onValueChange = { workMin = it },
                    label = { Text(stringResource(R.string.pomodoro_work_duration)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = shortBreakMin,
                    onValueChange = { shortBreakMin = it },
                    label = { Text(stringResource(R.string.pomodoro_short_break_duration)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = longBreakMin,
                    onValueChange = { longBreakMin = it },
                    label = { Text(stringResource(R.string.pomodoro_long_break_duration)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = blocksCount,
                    onValueChange = { blocksCount = it },
                    label = { Text(stringResource(R.string.pomodoro_blocks_before_long_break)) },
                    singleLine = true
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.pomodoro_auto_start))
                    Switch(checked = autoStart, onCheckedChange = { autoStart = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.pomodoro_sound))
                    Switch(checked = soundEnabled, onCheckedChange = { soundEnabled = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.pomodoro_vibration))
                    Switch(checked = vibrationEnabled, onCheckedChange = { vibrationEnabled = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val newConfig = config.copy(
                    workDurationMinutes = workMin.toIntOrNull() ?: config.workDurationMinutes,
                    shortBreakDurationMinutes = shortBreakMin.toIntOrNull() ?: config.shortBreakDurationMinutes,
                    longBreakDurationMinutes = longBreakMin.toIntOrNull() ?: config.longBreakDurationMinutes,
                    blocksBeforeLongBreak = blocksCount.toIntOrNull() ?: config.blocksBeforeLongBreak,
                    autoStart = autoStart,
                    soundEnabled = soundEnabled,
                    vibrationEnabled = vibrationEnabled
                )
                onSave(newConfig)
            }) {
                Text(stringResource(R.string.subjects_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.subjects_cancel))
            }
        }
    )
}
