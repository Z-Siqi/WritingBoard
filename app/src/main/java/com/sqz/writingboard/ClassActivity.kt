package com.sqz.writingboard

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class WritingBoard : ViewModel() {
    var textState by mutableStateOf(TextFieldValue())
}

class ValueState : ViewModel() {
    /*value*/
    var initLayout by mutableStateOf(false)
    var isEditing by mutableStateOf(false)
    var cleanAllText by mutableStateOf(false)
    var editButton by mutableStateOf(false)
    var readOnlyText by mutableStateOf(false)
    var editingHorizontalScreen by mutableStateOf(false)

    /*action on text*/
    var saveAction by mutableStateOf(false)
    var matchText by mutableStateOf(false)

    /*to change screen*/
    var updateScreen by mutableStateOf(false)
    var ee by mutableStateOf(false)

    /*for trigger multiple action*/
    var editAction by mutableStateOf(false)
    var doneAction by mutableStateOf(false)
    var onClickSetting by mutableStateOf(false)
}

class WritingBoardSettingState {
    @Composable
    fun rememberSwitchState(key: String, context: Context): MutableState<Boolean> {
        return remember {
            mutableStateOf(
                settingState.readSwitchState(
                    key,
                    context
                )
            )
        }
    }

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

    @Composable
    fun rememberSegmentedButtonState(key: String, context: Context): MutableState<Int> {
        return remember {
            mutableIntStateOf(
                settingState.readSegmentedButtonState(
                    key,
                    context
                )
            )
        }
    }

    fun readSegmentedButtonState(name: String, context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
        return sharedPreferences.getInt(name, 1)
    }

    fun writeSegmentedButtonState(name: String, context: Context, state: Int){
        val sharedPreferences = context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt(name, state)
        editor.apply()
    }
}
