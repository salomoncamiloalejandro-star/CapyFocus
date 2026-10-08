package com.muzu.capyfocus.ui.screens.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzu.capyfocus.core.notification.NotificationScheduler
import com.muzu.capyfocus.domain.models.AgendaItem
import com.muzu.capyfocus.domain.usecases.CreateAgendaItemUseCase
import com.muzu.capyfocus.domain.usecases.DeleteAgendaItemUseCase
import com.muzu.capyfocus.domain.usecases.ObserveAgendaForDateRangeUseCase
import com.muzu.capyfocus.domain.usecases.ToggleAgendaItemCompletionUseCase
import com.muzu.capyfocus.domain.usecases.UpdateAgendaItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val observeAgendaForDateRangeUseCase: ObserveAgendaForDateRangeUseCase,
    private val createAgendaItemUseCase: CreateAgendaItemUseCase,
    private val updateAgendaItemUseCase: UpdateAgendaItemUseCase,
    private val toggleAgendaItemCompletionUseCase: ToggleAgendaItemCompletionUseCase,
    private val deleteAgendaItemUseCase: DeleteAgendaItemUseCase,
    private val notificationScheduler: NotificationScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        loadItemsForCurrentState()
    }

    fun onEvent(event: AgendaEvent) {
        when (event) {
            is AgendaEvent.SelectDate -> {
                _uiState.update { it.copy(selectedDate = event.date) }
                loadItemsForCurrentState()
            }
            is AgendaEvent.ChangeViewMode -> {
                _uiState.update { it.copy(viewMode = event.viewMode) }
                loadItemsForCurrentState()
            }
            is AgendaEvent.NavigateDate -> {
                val currentState = _uiState.value
                val newDate = when (currentState.viewMode) {
                    AgendaViewMode.DAY -> currentState.selectedDate.plusDays(event.delta.toLong())
                    AgendaViewMode.WEEK -> currentState.selectedDate.plusWeeks(event.delta.toLong())
                    AgendaViewMode.MONTH -> currentState.selectedDate.plusMonths(event.delta.toLong())
                }
                _uiState.update { it.copy(selectedDate = newDate) }
                loadItemsForCurrentState()
            }
            is AgendaEvent.ShowAddDialog -> {
                _uiState.update {
                    it.copy(
                        isAddEditDialogVisible = true,
                        editingItem = null,
                        dialogInitialStartMinute = event.initialStartMinute ?: 540,
                        selectedDate = event.initialDate ?: it.selectedDate,
                        errorMessage = null,
                    )
                }
            }
            is AgendaEvent.ShowEditDialog -> {
                _uiState.update {
                    it.copy(
                        isAddEditDialogVisible = true,
                        editingItem = event.item,
                        isDetailVisible = false,
                        errorMessage = null,
                    )
                }
            }
            is AgendaEvent.ShowDetailDialog -> {
                _uiState.update {
                    it.copy(
                        isDetailVisible = true,
                        detailItem = event.item,
                    )
                }
            }
            AgendaEvent.DismissDialogs -> {
                _uiState.update {
                    it.copy(
                        isAddEditDialogVisible = false,
                        isDetailVisible = false,
                        editingItem = null,
                        detailItem = null,
                        errorMessage = null,
                    )
                }
            }
            is AgendaEvent.SaveAgendaItem -> {
                saveAgendaItem(event)
            }
            is AgendaEvent.DeleteAgendaItem -> {
                deleteItem(event.id)
            }
            is AgendaEvent.ToggleItemCompletion -> {
                toggleCompletion(event.id, event.isCompleted)
            }
            is AgendaEvent.DragMoveItem -> {
                dragMoveItem(event.item, event.newStartMinute)
            }
        }
    }

    private fun loadItemsForCurrentState() {
        observeJob?.cancel()
        val state = _uiState.value
        val (startEpochDay, endEpochDay) = when (state.viewMode) {
            AgendaViewMode.DAY -> {
                val epoch = state.selectedDate.toEpochDay()
                epoch to epoch
            }
            AgendaViewMode.WEEK -> {
                val startOfWeek = state.selectedDate.with(DayOfWeek.MONDAY)
                val endOfWeek = state.selectedDate.with(DayOfWeek.SUNDAY)
                startOfWeek.toEpochDay() to endOfWeek.toEpochDay()
            }
            AgendaViewMode.MONTH -> {
                val startOfMonth = state.selectedDate.withDayOfMonth(1)
                val endOfMonth = state.selectedDate.withDayOfMonth(state.selectedDate.lengthOfMonth())
                startOfMonth.toEpochDay() to endOfMonth.toEpochDay()
            }
        }

        observeJob = viewModelScope.launch {
            observeAgendaForDateRangeUseCase(startEpochDay, endEpochDay).collect { items ->
                _uiState.update {
                    it.copy(
                        items = items,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun saveAgendaItem(event: AgendaEvent.SaveAgendaItem) {
        viewModelScope.launch {
            val editingItem = _uiState.value.editingItem
            if (editingItem != null) {
                val realId = editingItem.id.substringBefore("_")
                val updated = editingItem.copy(
                    id = realId,
                    title = event.title,
                    description = event.description,
                    type = event.type,
                    category = event.category,
                    priority = event.priority,
                    dateEpochDay = event.date.toEpochDay(),
                    startMinuteOfDay = event.startMinute,
                    endMinuteOfDay = event.endMinute,
                    recurrenceType = event.recurrence,
                    notificationOffset = event.notificationOffset,
                )
                val result = updateAgendaItemUseCase(updated)
                result.onSuccess { item ->
                    notificationScheduler.scheduleNotification(item)
                    _uiState.update { it.copy(isAddEditDialogVisible = false, editingItem = null) }
                }.onFailure { err ->
                    _uiState.update { it.copy(errorMessage = err.message) }
                }
            } else {
                val result = createAgendaItemUseCase(
                    title = event.title,
                    description = event.description,
                    type = event.type,
                    category = event.category,
                    priority = event.priority,
                    dateEpochDay = event.date.toEpochDay(),
                    startMinuteOfDay = event.startMinute,
                    endMinuteOfDay = event.endMinute,
                    recurrenceType = event.recurrence,
                    notificationOffset = event.notificationOffset,
                )
                result.onSuccess { item ->
                    notificationScheduler.scheduleNotification(item)
                    _uiState.update { it.copy(isAddEditDialogVisible = false) }
                }.onFailure { err ->
                    _uiState.update { it.copy(errorMessage = err.message) }
                }
            }
        }
    }

    private fun deleteItem(id: String) {
        val realId = id.substringBefore("_")
        viewModelScope.launch {
            notificationScheduler.cancelNotification(realId)
            deleteAgendaItemUseCase(realId)
            _uiState.update {
                it.copy(
                    isDetailVisible = false,
                    isAddEditDialogVisible = false,
                    detailItem = null,
                    editingItem = null,
                )
            }
        }
    }

    private fun toggleCompletion(id: String, isCompleted: Boolean) {
        val realId = id.substringBefore("_")
        viewModelScope.launch {
            toggleAgendaItemCompletionUseCase(realId, isCompleted)
            if (isCompleted) {
                notificationScheduler.cancelNotification(realId)
            }
            _uiState.update { state ->
                val updatedDetail = if (state.detailItem?.id == id) state.detailItem.copy(isCompleted = isCompleted) else state.detailItem
                state.copy(detailItem = updatedDetail)
            }
        }
    }

    private fun dragMoveItem(item: AgendaItem, newStartMinute: Int) {
        val realId = item.id.substringBefore("_")
        val durationMinutes = (item.endMinuteOfDay - item.startMinuteOfDay).coerceAtLeast(15)
        val clampedStart = newStartMinute.coerceIn(0, 1440 - durationMinutes)
        val clampedEnd = clampedStart + durationMinutes

        val updated = item.copy(
            id = realId,
            startMinuteOfDay = clampedStart,
            endMinuteOfDay = clampedEnd,
        )

        viewModelScope.launch {
            updateAgendaItemUseCase(updated)
            notificationScheduler.scheduleNotification(updated)
        }
    }
}
