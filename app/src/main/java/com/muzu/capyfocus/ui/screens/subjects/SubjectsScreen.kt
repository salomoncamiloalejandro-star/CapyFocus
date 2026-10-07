package com.muzu.capyfocus.ui.screens.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muzu.capyfocus.R
import com.muzu.capyfocus.domain.models.Subject

@Composable
fun SubjectsScreen(
    viewModel: SubjectsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SubjectsContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun SubjectsContent(
    uiState: SubjectsUiState,
    onEvent: (SubjectsEvent) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(SubjectsEvent.ShowAddDialog) },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.subjects_add),
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                uiState.subjects.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.subjects_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        items(
                            items = uiState.subjects,
                            key = { it.id },
                        ) { subject ->
                            SubjectItem(
                                subject = subject,
                                onDelete = { onEvent(SubjectsEvent.DeleteSubject(subject.id)) },
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isAddDialogVisible) {
        AddSubjectDialog(
            uiState = uiState,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun SubjectItem(
    subject: Subject,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(subject.colorArgb)),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = subject.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.subjects_delete, subject.name),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun AddSubjectDialog(
    uiState: SubjectsUiState,
    onEvent: (SubjectsEvent) -> Unit,
) {
    val presetColors = listOf(
        0xFF4CAF50.toInt(),
        0xFF2196F3.toInt(),
        0xFF9C27B0.toInt(),
        0xFFFF9800.toInt(),
        0xFFE91E63.toInt(),
        0xFF00BCD4.toInt(),
    )

    AlertDialog(
        onDismissRequest = { onEvent(SubjectsEvent.DismissAddDialog) },
        title = {
            Text(text = stringResource(R.string.subjects_add))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = uiState.nameInput,
                    onValueChange = { onEvent(SubjectsEvent.OnNameChanged(it)) },
                    label = { Text(stringResource(R.string.subjects_name_label)) },
                    isError = uiState.errorMessage != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (uiState.errorMessage != null) {
                    Text(
                        text = stringResource(R.string.subjects_error_blank),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    presetColors.forEach { colorArgb ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(colorArgb))
                                .clickable { onEvent(SubjectsEvent.OnColorSelected(colorArgb)) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onEvent(SubjectsEvent.SaveSubject) },
            ) {
                Text(stringResource(R.string.subjects_save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onEvent(SubjectsEvent.DismissAddDialog) },
            ) {
                Text(stringResource(R.string.subjects_cancel))
            }
        },
    )
}
