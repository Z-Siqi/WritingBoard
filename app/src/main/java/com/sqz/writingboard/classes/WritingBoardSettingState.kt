package com.sqz.writingboard.classes

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.sqz.writingboard.settingState

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
