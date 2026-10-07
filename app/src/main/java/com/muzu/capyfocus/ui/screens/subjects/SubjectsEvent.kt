package com.muzu.capyfocus.ui.screens.subjects

sealed interface SubjectsEvent {
    data object ShowAddDialog : SubjectsEvent
    data object DismissAddDialog : SubjectsEvent
    data class OnNameChanged(val name: String) : SubjectsEvent
    data class OnColorSelected(val colorArgb: Int) : SubjectsEvent
    data class OnIconSelected(val iconKey: String) : SubjectsEvent
    data object SaveSubject : SubjectsEvent
    data class DeleteSubject(val subjectId: String) : SubjectsEvent
    data object ClearError : SubjectsEvent
}
