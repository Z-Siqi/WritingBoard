package com.sqz.writingboard.preference

import android.content.Context
import androidx.core.content.edit

open class PreferenceHelper(private val context: Context) {
    private val preferencesFileName = "preferences"
    open fun preferencesFileName(): String = this.preferencesFileName

    /** Boolean getter. default: if no data was set **/
    fun readPreferencesState(name: String, default: Boolean = false): Boolean {
        val sharedPreferences =
            context.getSharedPreferences(this.preferencesFileName(), Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(name, default)
    }

    /** Boolean setter **/
    fun writePreferencesState(name: String, state: Boolean) {
        val sharedPreferences =
            context.getSharedPreferences(this.preferencesFileName(), Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean(name, state)
        }
    }

    /** Int getter. default: if no data was set **/
    fun readPreferencesState(name: String, default: Int = 1): Int {
        val sharedPreferences =
            context.getSharedPreferences(this.preferencesFileName(), Context.MODE_PRIVATE)
        return sharedPreferences.getInt(name, default)
    }

    /** Int setter **/
    fun writePreferencesState(name: String, state: Int) {
        val sharedPreferences =
            context.getSharedPreferences(this.preferencesFileName(), Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putInt(name, state)
        }
    }

    /** String getter. default: if no data was set **/
    fun readPreferencesState(name: String, default: String? = null): String? {
        val sharedPreferences =
            context.getSharedPreferences(this.preferencesFileName(), Context.MODE_PRIVATE)
        return sharedPreferences.getString(name, default)
    }

    /** String setter **/
    fun writePreferencesState(name: String, state: String?) {
        val sharedPreferences =
            context.getSharedPreferences(this.preferencesFileName(), Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putString(name, state)
        }
    }

    /** Long getter. default: if no data was set **/
    fun readPreferencesState(name: String, default: Long = 1): Long {
        val sharedPreferences =
            context.getSharedPreferences(this.preferencesFileName(), Context.MODE_PRIVATE)
        return sharedPreferences.getLong(name, default)
    }

    /** Long setter **/
    fun writePreferencesState(name: String, state: Long) {
        val sharedPreferences =
            context.getSharedPreferences(this.preferencesFileName(), Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putLong(name, state)
        }
    }
}
