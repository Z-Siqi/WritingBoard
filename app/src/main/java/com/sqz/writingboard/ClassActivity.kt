package com.sqz.writingboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class TextBoard : ViewModel() {
    var textState by mutableStateOf(TextFieldValue())
}

class DoneButton : ViewModel() {
    var doneButton by mutableStateOf(false)
}