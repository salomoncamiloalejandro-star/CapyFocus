package com.muzu.capyfocus.ui.screens.agenda.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.muzu.capyfocus.domain.models.AgendaCategory
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.models.AgendaItemType
import com.muzu.capyfocus.domain.models.AgendaPriority
import com.muzu.capyfocus.domain.models.NotificationOffset
import com.muzu.capyfocus.domain.models.RecurrenceType
import com.muzu.capyfocus.ui.screens.agenda.AgendaEvent
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditAgendaItemDialog(
    selectedDate: LocalDate,
    editingItem: AgendaItem?,
    initialStartMinute: Int,
    errorMessage: String?,
    onEvent: (AgendaEvent) -> Unit,
) {
    var title by remember { mutableStateOf(editingItem?.title ?: "") }
    var description by remember { mutableStateOf(editingItem?.description ?: "") }
    var type by remember { mutableStateOf(editingItem?.type ?: AgendaItemType.TASK) }
    var category by remember { mutableStateOf(editingItem?.category ?: AgendaCategory.STUDY) }
    var priority by remember { mutableStateOf(editingItem?.priority ?: AgendaPriority.MEDIUM) }
    var startMinute by remember { mutableIntStateOf(editingItem?.startMinuteOfDay ?: initialStartMinute) }
    var endMinute by remember { mutableIntStateOf(editingItem?.endMinuteOfDay ?: (startMinute + 60)) }
    var recurrence by remember { mutableStateOf(editingItem?.recurrenceType ?: RecurrenceType.NONE) }
    var notificationOffset by remember { mutableStateOf(editingItem?.notificationOffset ?: NotificationOffset.FIFTEEN_MINUTES_BEFORE) }

    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { onEvent(AgendaEvent.DismissDialogs) },
        title = {
            Text(
                text = if (editingItem != null) "Editar Actividad" else "Nueva Actividad",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    isError = errorMessage != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción (opcional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Tipo", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    AgendaItemType.entries.forEach { t ->
                        val label = when (t) {
                            AgendaItemType.TASK -> "Tarea"
                            AgendaItemType.EVENT -> "Evento"
                            AgendaItemType.WORK -> "Trabajo"
                        }
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(label) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Categoría", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    AgendaCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(cat.colorArgb)),
                                    )
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Text(cat.label)
                                }
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Prioridad", style = MaterialTheme.typography.labelMedium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    AgendaPriority.entries.forEach { p ->
                        val label = when (p) {
                            AgendaPriority.LOW -> "Baja"
                            AgendaPriority.MEDIUM -> "Media"
                            AgendaPriority.HIGH -> "Alta"
                        }
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(label) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Horario", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val startStr = String.format("%02d:%02d", startMinute / 60, startMinute % 60)
                    val endStr = String.format("%02d:%02d", endMinute / 60, endMinute % 60)

                    Column {
                        Text("Inicio: $startStr", style = MaterialTheme.typography.bodyMedium)
                        Row {
                            TextButton(onClick = { startMinute = (startMinute - 15).coerceAtLeast(0) }) { Text("-15m") }
                            TextButton(onClick = { startMinute = (startMinute + 15).coerceAtMost(1425) }) { Text("+15m") }
                        }
                    }

                    Column {
                        Text("Fin: $endStr", style = MaterialTheme.typography.bodyMedium)
                        Row {
                            TextButton(onClick = { endMinute = (endMinute - 15).coerceAtLeast(startMinute + 15) }) { Text("-15m") }
                            TextButton(onClick = { endMinute = (endMinute + 15).coerceAtMost(1440) }) { Text("+15m") }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Recurrencia", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    RecurrenceType.entries.forEach { rec ->
                        FilterChip(
                            selected = recurrence == rec,
                            onClick = { recurrence = rec },
                            label = { Text(rec.label) },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Notificación", style = MaterialTheme.typography.labelMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    NotificationOffset.entries.forEach { offset ->
                        FilterChip(
                            selected = notificationOffset == offset,
                            onClick = { notificationOffset = offset },
                            label = { Text(offset.label) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(
                        AgendaEvent.SaveAgendaItem(
                            title = title,
                            description = description,
                            type = type,
                            category = category,
                            priority = priority,
                            date = selectedDate,
                            startMinute = startMinute,
                            endMinute = endMinute,
                            recurrence = recurrence,
                            notificationOffset = notificationOffset,
                        ),
                    )
                },
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(AgendaEvent.DismissDialogs) }) {
                Text("Cancelar")
            }
        },
    )
}
