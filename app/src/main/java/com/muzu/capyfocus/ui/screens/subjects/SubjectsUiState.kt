package com.muzu.capyfocus.ui.screens.subjects

import com.muzu.capyfocus.domain.models.Subject

data class SubjectsUiState(
    val subjects: List<Subject> = emptyList(),
    val isLoading: Boolean = true,
    val isAddDialogVisible: Boolean = false,
    val nameInput: String = "",
    val selectedColorArgb: Int = 0xFF4CAF50.toInt(),
    val selectedIconKey: String = "book",
    val errorMessage: String? = null,
)
