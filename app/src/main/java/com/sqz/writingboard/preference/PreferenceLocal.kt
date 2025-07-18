package com.sqz.writingboard.preference

import android.content.Context

class PreferenceLocal(context: Context) : PreferenceHelper(context) {
    @Override
    override fun preferencesFileName(): String {
        return "local_preference"
    }

    fun easterEgg(write: Boolean? = null): Boolean {
        val name = "easter_eggs"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, false)
    }

    fun settingsButtonManual(write: Boolean? = null): Boolean {
        val name = "settings_button_manual"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, true)
    }

    fun editButtonManual(write: Boolean? = null): Boolean {
        val name = "edit_button_manual"
        if (write != null) super.writePreferencesState(name, write)
        return super.readPreferencesState(name, true)
    }
}
