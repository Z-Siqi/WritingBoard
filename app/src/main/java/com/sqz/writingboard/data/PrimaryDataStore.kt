package com.sqz.writingboard.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

const val dataStoreName: String = "WritingBoard"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = dataStoreName
)

const val textDataKey: String = "saved_text"

const val maxLengthExpect: Int = 65536
