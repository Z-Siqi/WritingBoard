package com.sqz.writingboard

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class WritingBoard : ViewModel() {
    var textState by mutableStateOf(TextFieldValue())
}

class ButtonState : ViewModel() {
    var doneButton by mutableStateOf(false)
    var cleanButton by mutableStateOf(false)
    var saveAction by mutableStateOf(false)
    var requestFocus = FocusRequester()
    var editButton by mutableStateOf(false)
}

class WritingBoardSettingState {
    fun readSwitchState(name: String, context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
        Log.i("WritingBoardTag", "readSwitchState")
        return sharedPreferences.getBoolean(name, false)
    }

    fun writeSwitchState(name: String,context: Context, state: Boolean) {
        val sharedPreferences = context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean(name, state)
        editor.apply()
        Log.i("WritingBoardTag", "writeSwitchState")
    }
}

