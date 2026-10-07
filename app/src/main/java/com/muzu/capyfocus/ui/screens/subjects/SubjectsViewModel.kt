package com.muzu.capyfocus.ui.screens.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muzu.capyfocus.domain.usecases.CreateSubjectUseCase
import com.muzu.capyfocus.domain.usecases.DeleteSubjectUseCase
import com.muzu.capyfocus.domain.usecases.ObserveSubjectsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectsViewModel @Inject constructor(
    private val observeSubjectsUseCase: ObserveSubjectsUseCase,
    private val createSubjectUseCase: CreateSubjectUseCase,
    private val deleteSubjectUseCase: DeleteSubjectUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectsUiState())
    val uiState: StateFlow<SubjectsUiState> = _uiState.asStateFlow()

    init {
        observeSubjects()
    }

    private fun observeSubjects() {
        viewModelScope.launch {
            observeSubjectsUseCase().collect { subjects ->
                _uiState.update {
                    it.copy(
                        subjects = subjects,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun onEvent(event: SubjectsEvent) {
        when (event) {
            SubjectsEvent.ShowAddDialog -> {
                _uiState.update {
                    it.copy(
                        isAddDialogVisible = true,
                        nameInput = "",
                        errorMessage = null,
                    )
                }
            }
            SubjectsEvent.DismissAddDialog -> {
                _uiState.update {
                    it.copy(
                        isAddDialogVisible = false,
                        nameInput = "",
                        errorMessage = null,
                    )
                }
            }
            is SubjectsEvent.OnNameChanged -> {
                _uiState.update { it.copy(nameInput = event.name, errorMessage = null) }
            }
            is SubjectsEvent.OnColorSelected -> {
                _uiState.update { it.copy(selectedColorArgb = event.colorArgb) }
            }
            is SubjectsEvent.OnIconSelected -> {
                _uiState.update { it.copy(selectedIconKey = event.iconKey) }
            }
            SubjectsEvent.SaveSubject -> {
                saveSubject()
            }
            is SubjectsEvent.DeleteSubject -> {
                deleteSubject(event.subjectId)
            }
            SubjectsEvent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun saveSubject() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val result = createSubjectUseCase(
                name = currentState.nameInput,
                colorArgb = currentState.selectedColorArgb,
                iconKey = currentState.selectedIconKey,
            )
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isAddDialogVisible = false,
                        nameInput = "",
                        errorMessage = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = error.message)
                }
            }
        }
    }

    private fun deleteSubject(subjectId: String) {
        viewModelScope.launch {
            deleteSubjectUseCase(subjectId)
        }
    }
}
