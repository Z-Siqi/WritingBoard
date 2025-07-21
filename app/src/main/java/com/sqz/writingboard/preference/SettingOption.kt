package com.sqz.writingboard.preference

import android.content.Context

class SettingOption(context: Context) : PreferenceHelper(context) {
    @Override
    override fun preferencesFileName(): String {
        return "WritingBoardSetting"
    }

    @Deprecated("No need anymore")
    fun allowMultipleLines(write: Boolean? = null): Boolean {
        val name = "allow_multiple_lines"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    fun alwaysVisibleText(write: Boolean? = null): Boolean {
        val name = "always_visible_text"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    fun buttonStyle(write: Int? = null): Int {
        val name = "button_style"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 1)
    }

    @Deprecated("No need anymore")
    fun cleanAllText(write: Boolean? = null): Boolean {
        val name = "clean_all_text"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    @Deprecated("No need anymore")
    fun disableAutoSave(write: Boolean? = null): Boolean {
        val name = "disable_auto_save"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    @Deprecated("still in old model")
    fun easterEgg(write: Boolean? = null): Boolean {
        val name = "easter_eggs"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    fun editButton(write: Boolean? = null): Boolean {
        val name = "edit_button"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    fun fontSize(write: Int? = null): Int {
        val name = "font_size"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 1)
    }

    fun fontStyle(write: Int? = null): Int {
        val name = "font_style"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 1)
    }

    fun fontStyleExtra(write: Int? = null): Int {
        val name = "font_style_extra"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 0)
    }

    fun fontWeight(write: Int? = null): Int {
        val name = "font_weight"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 1)
    }

    fun italics(write: Boolean? = null): Boolean {
        val name = "italics"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    @Deprecated("still in old model")
    fun offButtonManual(write: Boolean? = null): Boolean {
        val name = "off_button_manual"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    @Deprecated("still in old model")
    fun offEditButtonManual(write: Boolean? = null): Boolean {
        val name = "off_editButton_manual"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    fun theme(write: Int? = null): Int {
        val name = "theme"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 1)
    }

    fun vibrate(write: Int? = null): Int {
        val name = "vibrate_settings"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 1)
    }
}
