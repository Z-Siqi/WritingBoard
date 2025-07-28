package com.sqz.writingboard.preference

import android.content.Context

class SettingOption(context: Context) : PreferenceHelper(context) {
    @Override
    override fun preferencesFileName(): String {
        return "WritingBoardSetting"
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
        val name = FONT_STYLE
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 1)
    }

    fun fontStyleExtra(write: Int? = null): Int {
        val name = FONT_STYLE_EXTRA
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 0)
    }

    fun fontWeight(write: Int? = null): Int {
        val name = FONT_WEIGHT
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, 1)
    }

    fun italics(write: Boolean? = null): Boolean {
        val name = "italics"
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

    fun mergeLineBreak(write: Boolean? = null): Boolean {
        val name = "merge_line_break"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, true)
    }

    fun instantSaveText(write: Boolean? = null): Boolean {
        val name = "instant_save_text"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    companion object {
        const val FONT_STYLE = "font_style"
        const val FONT_STYLE_EXTRA = "font_style_extra"
        const val FONT_WEIGHT = "font_weight"
    }
}
