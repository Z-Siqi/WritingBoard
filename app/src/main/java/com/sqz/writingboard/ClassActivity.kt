package com.sqz.writingboard

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class WritingBoard : ViewModel() {
    var textState by mutableStateOf(TextFieldValue())
}

class ButtonState : ViewModel() {
    var doneButton by mutableStateOf(false)
}

class WritingBoardSettingState {
    fun readSwitchState(name: String, context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(name, false)
    }

    fun writeSwitchState(name: String,context: Context, state: Boolean) {
        val sharedPreferences = context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(name, state)
        editor.apply()
    }
}
