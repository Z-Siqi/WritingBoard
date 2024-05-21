package com.sqz.writingboard.ui

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqz.writingboard.dataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WritingBoardViewModel : ViewModel() {
    private val _savedText = MutableLiveData<String>()
    private var _isLoad by mutableStateOf(false)
    val savedText: LiveData<String> get() = _savedText

    suspend fun loadSavedText(context: Context, then: () -> Unit = {}) {
        viewModelScope.launch {
            val savedText: String = context.dataStore.data
                .catch {
                    if (it is IOException) {
                        Log.e(ContentValues.TAG, "Error reading preferences.", it)
                        emit(emptyPreferences())
                    } else {
                        throw it
                    }
                }
                .map { preferences ->
                    preferences[stringPreferencesKey("saved_text")] ?: ""
                }.first()
            _savedText.value = savedText
            delay(5)
            then()
            _isLoad = true
        }
    }

    suspend fun saveText(toSave: String, context: Context){
        if (_isLoad) {
            viewModelScope.launch {
                context.dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("saved_text")] = toSave
                }
                Log.i("WritingBoardTag", "Text is saved")
            }
        } else {
            Log.e("WritingBoardTag", "Failed to save! Due to data not load yet!")
        }
    }
}
