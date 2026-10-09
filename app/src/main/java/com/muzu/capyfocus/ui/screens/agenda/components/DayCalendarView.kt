package com.muzu.capyfocus.ui.screens.agenda.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.ui.screens.agenda.AgendaEvent
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

private val HourRowHeight = 60.dp

@Composable
fun DayCalendarView(
    selectedDate: LocalDate,
    items: List<AgendaItem>,
    onEvent: (AgendaEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val isToday = selectedDate == LocalDate.now()
    val esLocale = Locale.forLanguageTag("es")

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val dayName = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, esLocale)
                        .replaceFirstChar { it.uppercase() }
                    val dayNum = selectedDate.dayOfMonth
                    val monthName = selectedDate.month.getDisplayName(TextStyle.FULL, esLocale)

                    Text(
                        text = "$dayName, $dayNum de $monthName",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )

                    if (isToday) {
                        Spacer(modifier = Modifier.width(8.dp))
                        SuggestionChip(
                            onClick = { },
                            label = { Text("HOY", style = MaterialTheme.typography.labelSmall) },
                        )
                    }
                }

                if (!isToday) {
                    TextButton(onClick = { onEvent(AgendaEvent.SelectDate(LocalDate.now())) }) {
                        Text("Ir a hoy")
                    }
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                for (hour in 0..23) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HourRowHeight),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Text(
                            text = String.format(Locale.getDefault(), "%02d:00", hour),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .width(50.dp)
                                .padding(start = 8.dp, top = 2.dp),
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 54.dp, end = 8.dp),
            ) {
                items.forEach { item ->
                    DayAgendaItemCard(
                        item = item,
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}

@Composable
private fun DayAgendaItemCard(
    item: AgendaItem,
    onEvent: (AgendaEvent) -> Unit,
) {
    val topDp = (item.startMinuteOfDay * HourRowHeight.value / 60f).dp
    val heightDp = (((item.endMinuteOfDay - item.startMinuteOfDay).coerceAtLeast(20)) * HourRowHeight.value / 60f).dp

    var offsetYPx by remember { mutableFloatStateOf(0f) }

    val categoryColor = Color(item.category.colorArgb)
    val cardBg = categoryColor.copy(alpha = if (item.isCompleted) 0.3f else 0.85f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset { IntOffset(0, offsetYPx.roundToInt()) }
            .padding(top = topDp)
            .height(heightDp)
            .pointerInput(item.id) {
                detectDragGestures(
                    onDragEnd = {
                        val minutesShift = ((offsetYPx / HourRowHeight.toPx()) * 60).roundToInt()
                        val snappedShift = (minutesShift / 15) * 15
                        if (snappedShift != 0) {
                            val newStart = (item.startMinuteOfDay + snappedShift).coerceIn(0, 1425)
                            onEvent(AgendaEvent.DragMoveItem(item, newStart))
                        }
                        offsetYPx = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetYPx += dragAmount.y
                    },
                )
            }
            .clickable { onEvent(AgendaEvent.ShowDetailDialog(item)) },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .height(12.dp)
                        .width(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(categoryColor),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
            val startStr = String.format(Locale.getDefault(), "%02d:%02d", item.startMinuteOfDay / 60, item.startMinuteOfDay % 60)
            val endStr = String.format(Locale.getDefault(), "%02d:%02d", item.endMinuteOfDay / 60, item.endMinuteOfDay % 60)
            Text(
                text = "$startStr - $endStr",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = Color.White.copy(alpha = 0.9f),
            )
        }
    }
}
