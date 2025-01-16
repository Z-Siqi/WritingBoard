package com.sqz.writingboard.ui.setting.data

import android.content.Context

class SettingOption(private val context: Context) {
    private val settingState = SettingState()

    fun allowMultipleLines(write: Boolean? = null): Boolean {
        val name = "allow_multiple_lines"
        if (write != null) settingState.writeSwitchState(name, context, write)
        return settingState.readSwitchState(name, context)
    }

    fun alwaysVisibleText(write: Boolean? = null): Boolean {
        val name = "always_visible_text"
        if (write != null) settingState.writeSwitchState(name, context, write)
        return settingState.readSwitchState(name, context)
    }

    fun buttonStyle(write: Int? = null): Int {
        val name = "button_style"
        if (write != null) settingState.writeSegmentedButtonState(name, context, write)
        return settingState.readSegmentedButtonState(name, context)
    }

    fun cleanAllText(write: Boolean? = null): Boolean {
        val name = "clean_all_text"
        if (write != null) settingState.writeSwitchState(name, context, write)
        return settingState.readSwitchState(name, context)
    }

    fun disableAutoSave(write: Boolean? = null): Boolean {
        val name = "disable_auto_save"
        if (write != null) settingState.writeSwitchState(name, context, write)
        return settingState.readSwitchState(name, context)
    }

    fun easterEgg(write: Boolean? = null): Boolean {
        val name = "easter_eggs"
        if (write != null) settingState.writeSwitchState(name, context, write)
        return settingState.readSwitchState(name, context)
    }

    fun editButton(write: Boolean? = null): Boolean {
        val name = "edit_button"
        if (write != null) settingState.writeSwitchState(name, context, write)
        return settingState.readSwitchState(name, context)
    }

    fun fontSize(write: Int? = null): Int {
        val name = "font_size"
        if (write != null) settingState.writeSegmentedButtonState(name, context, write)
        return settingState.readSegmentedButtonState(name, context)
    }

    fun fontStyle(write: Int? = null): Int {
        val name = "font_style"
        if (write != null) settingState.writeSegmentedButtonState(name, context, write)
        return settingState.readSegmentedButtonState(name, context)
    }

    fun fontStyleExtra(write: Int? = null): Int {
        val name = "font_style_extra"
        if (write != null) settingState.writeSegmentedButtonState(name, context, write)
        return settingState.readSegmentedButtonState(name, context)
    }

    fun fontWeight(write: Int? = null): Int {
        val name = "font_weight"
        if (write != null) settingState.writeSegmentedButtonState(name, context, write)
        return settingState.readSegmentedButtonState(name, context)
    }

    fun italics(write: Boolean? = null): Boolean {
        val name = "italics"
        if (write != null) settingState.writeSwitchState(name, context, write)
        return settingState.readSwitchState(name, context)
    }

    fun offButtonManual(write: Boolean? = null): Boolean {
        val name = "off_button_manual"
        if (write != null) settingState.writeSwitchState(name, context, write)
        return settingState.readSwitchState(name, context)
    }

    fun offEditButtonManual(write: Boolean? = null): Boolean {
        val name = "off_editButton_manual"
        if (write != null) settingState.writeSwitchState(name, context, write)
        return settingState.readSwitchState(name, context)
    }

    fun theme(write: Int? = null): Int {
        val name = "theme"
        if (write != null) settingState.writeSegmentedButtonState(name, context, write)
        return settingState.readSegmentedButtonState(name, context)
    }

    fun vibrate(write: Int? = null): Int {
        val name = "vibrate_settings"
        if (write != null) settingState.writeSegmentedButtonState(name, context, write)
        return settingState.readSegmentedButtonState(name, context)
    }

}
