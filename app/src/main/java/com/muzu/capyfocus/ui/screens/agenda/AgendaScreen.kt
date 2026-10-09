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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AgendaScreen(
    viewModel: AgendaViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AgendaHeader(uiState = uiState, onEvent = viewModel::onEvent)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(AgendaEvent.ShowAddDialog()) }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar elemento")
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                when (uiState.viewMode) {
                    AgendaViewMode.DAY -> {
                        DayCalendarView(
                            selectedDate = uiState.selectedDate,
                            items = uiState.items,
                            onEvent = viewModel::onEvent,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    AgendaViewMode.WEEK -> {
                        WeekCalendarView(
                            selectedDate = uiState.selectedDate,
                            items = uiState.items,
                            onEvent = viewModel::onEvent,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    AgendaViewMode.MONTH -> {
                        MonthCalendarView(
                            selectedDate = uiState.selectedDate,
                            items = uiState.items,
                            onEvent = viewModel::onEvent,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }

            if (uiState.isAddEditDialogVisible) {
                AddEditAgendaItemDialog(
                    selectedDate = uiState.selectedDate,
                    editingItem = uiState.editingItem,
                    initialStartMinute = uiState.dialogInitialStartMinute,
                    errorMessage = uiState.errorMessage,
                    onEvent = viewModel::onEvent,
                )
            }

            if (uiState.isDetailVisible && uiState.detailItem != null) {
                AgendaItemDetailDialog(
                    item = uiState.detailItem!!,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }
}

@Composable
private fun AgendaHeader(
    uiState: AgendaUiState,
    onEvent: (AgendaEvent) -> Unit,
) {
    val esLocale = Locale.forLanguageTag("es")
    val dateText = when (uiState.viewMode) {
        AgendaViewMode.DAY -> {
            val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM yyyy", esLocale)
            uiState.selectedDate.format(formatter).replaceFirstChar { it.uppercase() }
        }
        AgendaViewMode.WEEK -> {
            val startOfWeek = uiState.selectedDate.with(DayOfWeek.MONDAY)
            val endOfWeek = uiState.selectedDate.with(DayOfWeek.SUNDAY)
            if (startOfWeek.month == endOfWeek.month) {
                val monthStr = startOfWeek.format(DateTimeFormatter.ofPattern("MMMM yyyy", esLocale)).replaceFirstChar { it.uppercase() }
                "${startOfWeek.dayOfMonth} - ${endOfWeek.dayOfMonth} de $monthStr"
            } else {
                val startStr = startOfWeek.format(DateTimeFormatter.ofPattern("d 'de' MMM", esLocale))
                val endStr = endOfWeek.format(DateTimeFormatter.ofPattern("d 'de' MMM yyyy", esLocale))
                "$startStr - $endStr"
            }
        }
        AgendaViewMode.MONTH -> {
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", esLocale)
            uiState.selectedDate.format(formatter).replaceFirstChar { it.uppercase() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Selector de vista con SegmentedButton de Material 3
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            AgendaViewMode.entries.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = uiState.viewMode == mode,
                    onClick = { onEvent(AgendaEvent.ChangeViewMode(mode)) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = AgendaViewMode.entries.size,
                    ),
                ) {
                    Text(
                        text = mode.label,
                        maxLines = 1,
                    )
                }
            }
        }

        // Navegación de fechas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { onEvent(AgendaEvent.NavigateDate(-1)) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Anterior",
                )
            }
            Text(
                text = dateText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
            IconButton(onClick = { onEvent(AgendaEvent.NavigateDate(1)) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Siguiente",
                )
            }
        }
    }
}
