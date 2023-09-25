package com.sqz.writingboard

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.sqz.writingboard.ui.setting

class WritingBoard : ViewModel() {
    var textState by mutableStateOf(TextFieldValue())
}

class ValueState : ViewModel() {
    var doneButton by mutableStateOf(false)
    var cleanButton by mutableStateOf(false)
    var saveAction by mutableStateOf(false)
    var requestFocus = FocusRequester()
    var editButton by mutableStateOf(false)
    var updateScreen by mutableStateOf(false)
    var openLayout by mutableStateOf(false)
}

class WritingBoardSettingState {
    @Composable
    fun rememberSwitchState(key: String, context: Context): MutableState<Boolean> {
        return remember {
            mutableStateOf(
                setting.readSwitchState(
                    key,
                    context
                )
            )
        }
    }

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

    @Composable
    fun rememberSegmentedButtonState(key: String, context: Context): MutableState<Int> {
        return remember {
            mutableStateOf(
                setting.readSegmentedButtonState(
                    key,
                    context
                )
            )
        }
    }

    fun readSegmentedButtonState(name: String, context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
        Log.i("WritingBoardTag", "readSegmentedButtonState")
        return sharedPreferences.getInt(name, 1)
    }

    fun writeSegmentedButtonState(name: String, context: Context, state: Int){
        val sharedPreferences = context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(name, state)
        editor.apply()
        Log.i("WritingBoardTag", "writeSwitchState")
    }
}
