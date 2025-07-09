package com.sqz.writingboard.ui.layout

data class LocalState(
    val isEditable: Boolean = true,
    val isInReadOnlyMode: Boolean = false,
    val isImeOn: Boolean = false,
    val isFocus: Boolean = false,
)
