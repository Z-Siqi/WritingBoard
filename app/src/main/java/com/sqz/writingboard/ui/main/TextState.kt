package com.sqz.writingboard.ui.main

data class TextState(
    val requestSave: Boolean = false,
    val isEditing: Boolean = false,
    val editButtonState: Boolean = false,
    val readOnlyText: Boolean = false,
    val requestMatch: Boolean = false,
)
