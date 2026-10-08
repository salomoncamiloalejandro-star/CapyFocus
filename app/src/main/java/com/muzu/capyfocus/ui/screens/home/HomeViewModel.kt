package com.muzu.capyfocus.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzu.capyfocus.domain.usecases.GetHomeConfigUseCase
import com.muzu.capyfocus.domain.usecases.ObserveAgendaForDateRangeUseCase
import com.muzu.capyfocus.domain.usecases.SaveHomeConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeConfigUseCase: GetHomeConfigUseCase,
    private val saveHomeConfigUseCase: SaveHomeConfigUseCase,
    private val observeAgendaForDateRangeUseCase: ObserveAgendaForDateRangeUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getHomeConfigUseCase().collect { path ->
                _uiState.update { it.copy(backgroundImagePath = path) }
            }
        }

        viewModelScope.launch {
            val today = LocalDate.now()
            val startEpoch = today.toEpochDay()
            val endEpoch = today.plusDays(7).toEpochDay()
            observeAgendaForDateRangeUseCase(startEpoch, endEpoch).collect { items ->
                val upcoming = items.filter { !it.isCompleted }.sortedBy { it.startMinuteOfDay }
                _uiState.update { it.copy(upcomingItems = upcoming) }
            }
        }
    }

    fun setBackgroundImage(uriString: String) {
        viewModelScope.launch {
            try {
                val uri = android.net.Uri.parse(uriString)
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.filesDir, "home_bg_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                saveHomeConfigUseCase(file.absolutePath)
            } catch (_: Exception) {
            }
        }
    }

    fun removeBackgroundImage() {
        viewModelScope.launch {
            saveHomeConfigUseCase(null)
        }
    }
}
