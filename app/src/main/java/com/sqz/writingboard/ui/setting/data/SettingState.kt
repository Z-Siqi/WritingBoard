package com.sqz.writingboard.ui.setting.data

import android.content.Context

class SettingState {

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
