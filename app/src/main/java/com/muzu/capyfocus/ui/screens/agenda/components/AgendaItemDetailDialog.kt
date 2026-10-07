package com.muzu.capyfocus.ui.screens.agenda.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.ui.screens.agenda.AgendaEvent

@Composable
fun AgendaItemDetailDialog(
    item: AgendaItem,
    onEvent: (AgendaEvent) -> Unit,
) {
    val categoryColor = Color(item.category.colorArgb)

    AlertDialog(
        onDismissRequest = { onEvent(AgendaEvent.DismissDialogs) },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(categoryColor),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { onEvent(AgendaEvent.ShowEditDialog(item)) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { onEvent(AgendaEvent.DeleteAgendaItem(item.id)) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val startStr = String.format("%02d:%02d", item.startMinuteOfDay / 60, item.startMinuteOfDay % 60)
                val endStr = String.format("%02d:%02d", item.endMinuteOfDay / 60, item.endMinuteOfDay % 60)

                Text(
                    text = "Horario: $startStr - $endStr",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Categoría: ${item.category.label} • Prioridad: ${item.priority.name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (item.recurrenceType.label != "Sin repetición") {
                    Text(
                        text = "Repetición: ${item.recurrenceType.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (item.notificationOffset != null) {
                    Text(
                        text = "Recordatorio: ${item.notificationOffset.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEvent(AgendaEvent.ToggleItemCompletion(item.id, !item.isCompleted))
                },
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (item.isCompleted) "Marcar pendiente" else "Marcar completada")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(AgendaEvent.DismissDialogs) }) {
                Text("Cerrar")
            }
        },
    )
}
