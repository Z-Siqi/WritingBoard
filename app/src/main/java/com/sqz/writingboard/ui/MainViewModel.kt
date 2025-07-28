package com.sqz.writingboard.ui

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqz.writingboard.data.dataStore
import com.sqz.writingboard.data.textDataKey
import com.sqz.writingboard.glance.GlanceWidgetManager
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.layout.LocalState
import com.sqz.writingboard.ui.layout.handler.RequestHandler
import com.sqz.writingboard.ui.layout.handler.NavControllerHandler
import com.sqz.writingboard.ui.layout.handler.BoardSizeHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var _load: Boolean
    private val _textFieldState = TextFieldState()

    var savedTextHashCode: Int? = null

    var navControllerHandler: NavControllerHandler

    var boardSizeHandler: BoardSizeHandler

    var requestHandler: RequestHandler

    val state = MutableStateFlow<LocalState>(LocalState())

    init {
        _load = false
        this.let { viewModel ->
            navControllerHandler = NavControllerHandler(viewModel)
            requestHandler = RequestHandler(viewModel)
            boardSizeHandler = BoardSizeHandler(viewModel, state)
        }
    }

    fun textFieldState(context: Context? = null): TextFieldState {
        if (!_load && context != null && _textFieldState.text.isEmpty()) _load = true.also {
            viewModelScope.launch {
                state.update { // init read only state
                    it.copy(isEditable = !SettingOption(context).editButton())
                }
                val savedText: String = context.dataStore.data.catch { // load data from storage
                    if (it is IOException) {
                        Log.e(ContentValues.TAG, "Error reading preferences.", it)
                        emit(emptyPreferences())
                    } else throw it
                }.map { prefs -> prefs[stringPreferencesKey(textDataKey)] ?: "" }.first()
                if (_textFieldState.text.isNotEmpty()) throw TypeCastException("Text is already exist! Pls report this!")
                if (SettingOption(context).mergeLineBreak() && savedText.isNotEmpty() && savedText.last() == '\n') {
                    Log.d("WritingBoardTag", "Removing line breaks and adding a new line.")
                    _textFieldState.edit { insert(0, savedText.trimEnd { it == '\n' }.plus('\n')) }
                } else {
                    _textFieldState.edit { insert(0, savedText) }
                }
                savedTextHashCode = savedText.hashCode()
            }
        }
        return _textFieldState
    }

    private var _saver: Boolean = false

    fun saveTextToStorage(context: Context, force: Boolean = true) {
        val dispatchers = if (force) Dispatchers.Main else Dispatchers.IO
        if (!_saver && savedTextHashCode != null) _saver = true.also {
            viewModelScope.launch(dispatchers) {
                context.dataStore.edit { prefs ->
                    prefs[stringPreferencesKey(textDataKey)] = _textFieldState.text.toString()
                }
                Log.i("WritingBoardTag", "Text is saved")
                savedTextHashCode = _textFieldState.text.hashCode()
                GlanceWidgetManager.updateWidget(context)
                _saver = false
            }
        }
        // this assert is to make sure saving cannot before loading.
        assert(savedTextHashCode != null)
    }
}
