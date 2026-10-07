package com.muzu.capyfocus.ui.screens.agenda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muzu.capyfocus.ui.screens.agenda.components.AddEditAgendaItemDialog
import com.muzu.capyfocus.ui.screens.agenda.components.AgendaItemDetailDialog
import com.muzu.capyfocus.ui.screens.agenda.components.DayCalendarView
import com.muzu.capyfocus.ui.screens.agenda.components.MonthCalendarView
import com.muzu.capyfocus.ui.screens.agenda.components.WeekCalendarView
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AgendaScreen(
    viewModel: AgendaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AgendaContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun AgendaContent(
    uiState: AgendaUiState,
    onEvent: (AgendaEvent) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(AgendaEvent.ShowAddDialog()) },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear actividad")
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            AgendaHeader(
                uiState = uiState,
                onEvent = onEvent,
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    when (uiState.viewMode) {
                        AgendaViewMode.DAY -> {
                            DayCalendarView(
                                items = uiState.items,
                                onEvent = onEvent,
                            )
                        }
                        AgendaViewMode.WEEK -> {
                            WeekCalendarView(
                                selectedDate = uiState.selectedDate,
                                items = uiState.items,
                                onEvent = onEvent,
                            )
                        }
                        AgendaViewMode.MONTH -> {
                            MonthCalendarView(
                                selectedDate = uiState.selectedDate,
                                items = uiState.items,
                                onEvent = onEvent,
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isAddEditDialogVisible) {
        AddEditAgendaItemDialog(
            selectedDate = uiState.selectedDate,
            editingItem = uiState.editingItem,
            initialStartMinute = uiState.dialogInitialStartMinute,
            errorMessage = uiState.errorMessage,
            onEvent = onEvent,
        )
    }

    if (uiState.isDetailVisible && uiState.detailItem != null) {
        AgendaItemDetailDialog(
            item = uiState.detailItem,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun AgendaHeader(
    uiState: AgendaUiState,
    onEvent: (AgendaEvent) -> Unit,
) {
    val monthName = uiState.selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val year = uiState.selectedDate.year

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onEvent(AgendaEvent.NavigateDate(-1)) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Anterior")
                }
                Text(
                    text = "${monthName.replaceFirstChar { it.uppercase() }} $year",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                IconButton(onClick = { onEvent(AgendaEvent.NavigateDate(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Siguiente")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AgendaViewMode.entries.forEach { mode ->
                    FilterChip(
                        selected = uiState.viewMode == mode,
                        onClick = { onEvent(AgendaEvent.ChangeViewMode(mode)) },
                        label = { Text(mode.label) },
                    )
                }
            }
        }
    }
}
