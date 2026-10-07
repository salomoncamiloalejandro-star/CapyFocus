package com.muzu.capyfocus.ui.screens.agenda.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.ui.screens.agenda.AgendaEvent
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthCalendarView(
    selectedDate: LocalDate,
    items: List<AgendaItem>,
    onEvent: (AgendaEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstDayOfMonth = selectedDate.withDayOfMonth(1)
    val daysInMonth = selectedDate.lengthOfMonth()
    val firstDayOfWeekOffset = firstDayOfMonth.dayOfWeek.value - 1

    val itemsByEpochDay = items.groupBy { it.dateEpochDay }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            listOf("LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM").forEach { dayName ->
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val totalGridCells = firstDayOfWeekOffset + daysInMonth
        val rows = (totalGridCells + 6) / 7

        Column(modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - firstDayOfWeekOffset + 1

                        if (dayNumber in 1..daysInMonth) {
                            val date = firstDayOfMonth.plusDays((dayNumber - 1).toLong())
                            val isSelected = date == selectedDate
                            val isToday = date == LocalDate.now()
                            val dateItems = itemsByEpochDay[date.toEpochDay()] ?: emptyList()

                            MonthDayCell(
                                dayNumber = dayNumber,
                                isSelected = isSelected,
                                isToday = isToday,
                                items = dateItems,
                                onClick = { onEvent(AgendaEvent.SelectDate(date)) },
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val selectedDayItems = itemsByEpochDay[selectedDate.toEpochDay()] ?: emptyList()
        Text(
            text = "Actividades del ${selectedDate.dayOfMonth} de ${selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )

        if (selectedDayItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Sin actividades para este día.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(selectedDayItems, key = { it.id }) { item ->
                    MonthAgendaItemCard(item = item, onEvent = onEvent)
                }
            }
        }
    }
}

@Composable
private fun MonthDayCell(
    dayNumber: Int,
    isSelected: Boolean,
    isToday: Boolean,
    items: List<AgendaItem>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    isToday -> MaterialTheme.colorScheme.surfaceContainerHigh
                    else -> Color.Transparent
                },
            )
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = dayNumber.toString(),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp,
            ),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                isToday -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurface
            },
        )
        if (items.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(top = 2.dp),
            ) {
                items.take(3).forEach { item ->
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color(item.category.colorArgb)),
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthAgendaItemCard(
    item: AgendaItem,
    onEvent: (AgendaEvent) -> Unit,
) {
    val categoryColor = Color(item.category.colorArgb)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEvent(AgendaEvent.ShowDetailDialog(item)) },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = categoryColor.copy(alpha = 0.15f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(categoryColor),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )
                val startStr = String.format(Locale.getDefault(), "%02d:%02d", item.startMinuteOfDay / 60, item.startMinuteOfDay % 60)
                val endStr = String.format(Locale.getDefault(), "%02d:%02d", item.endMinuteOfDay / 60, item.endMinuteOfDay % 60)
                Text(
                    text = "$startStr - $endStr • ${item.category.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
