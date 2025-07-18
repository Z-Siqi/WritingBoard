package com.sqz.writingboard.ui

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.View
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sqz.writingboard.component.Feedback
import com.sqz.writingboard.dataStore
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.ui.main.TextState
import com.sqz.writingboard.preference.SettingOption
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WritingBoardViewModel : ViewModel() {
    private val _savedText = MutableLiveData<String>()
    private var _isLoad by mutableStateOf(false)

    val textFieldState = TextFieldState()

    val savedText: LiveData<String> get() = _savedText

    suspend fun loadSavedText(context: Context, then: () -> Unit = {}) {
        viewModelScope.launch {
            val savedText: String = context.dataStore.data.catch {
                if (it is IOException) {
                    Log.e(ContentValues.TAG, "Error reading preferences.", it)
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }.map { preferences ->
                preferences[stringPreferencesKey("saved_text")] ?: ""
            }.first()
            _savedText.value = savedText
            delay(5)
            then()
            _isLoad = true
        }
    }

    suspend fun saveText(toSave: String, context: Context): Boolean {
        return if (_isLoad) {
            viewModelScope.launch {
                context.dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("saved_text")] = toSave
                }
                Log.i("WritingBoardTag", "Text is saved")
            }
            true
        } else {
            Log.e("WritingBoardTag", "Failed to save! Due to data not load yet!")
            false
        }
    }

    private val _textState = MutableStateFlow(TextState())
    val textState: MutableStateFlow<TextState> = _textState

    private var _oneTimeSaveLimit by mutableStateOf(false)
    fun formatThenSave(text: String, context: Context, forceSave: Boolean = false) {
        val set = SettingOption(context)
        if (!this._oneTimeSaveLimit) viewModelScope.launch { // to save if requestSave = true
            if (_textState.value.requestSave || forceSave) text.let { newText ->
                val textToSave = if (!set.allowMultipleLines() &&
                    newText.isNotEmpty() && newText.last() == '\n'
                ) {
                    Log.d("WritingBoardTag", "Removing line breaks and adding a new line.")
                    newText.trimEnd { it == '\n' }.plus('\n')
                } else if (newText.isEmpty()) {
                    Log.w("WritingBoardTag", "Saved Nothing!")
                    newText
                } else newText
                saveText(textToSave, context).let {
                    if (it) _textState.update { update ->
                        _oneTimeSaveLimit = true
                        WritingBoardWidget().updateAll(context)
                        update.copy(requestSave = false)
                    } else {
                        delay(1000)
                        Log.e("WritingBoardTag", "Timeout: Failed to save!")
                        _textState.update { update -> update.copy(requestSave = false) }
                    }
                }
                delay(500)
                _oneTimeSaveLimit = false
            }
        }
    }

    /** Clean all text that in WritingBoard **/
    fun cleanAllText() = _textState.update {
        it.copy(requestSave = true).also { this.textFieldState.clearText() }
    }

    /** For the match text (EE) **/
    fun matchText(text: (text: CharSequence) -> Unit, value: Boolean) = _textState.update {
        if (value) it.copy(requestMatch = false).also { this.textFieldState.text.let(text) } else it
    }

    /** Set the state of TextState **/
    fun textStateSetter(setter: Boolean, type: TextState) = _textState.update {
        it.copy(
            requestSave = if (type.requestSave) setter else it.requestSave,
            isEditing = if (type.isEditing) setter else it.isEditing,
            editButtonState = if (type.editButtonState) setter else it.editButtonState,
            readOnlyText = if (type.readOnlyText) setter else it.readOnlyText,
        )
    }

    /** Request cursor when click the (bottom) area **/
    private var _focusRequestState by mutableStateOf(false)
    fun focusRequestState(setter: Boolean? = null, focusRequester: FocusRequester? = null) {
        if (setter != null) this._focusRequestState = setter
        if (this._focusRequestState) focusRequester?.requestFocus()?.also {
            this.textFieldState.edit { placeCursorAtEnd() }
            this._focusRequestState = false
        }
    }

    /** When Done action is triggered **/
    fun doneAction(
        softwareKeyboardController: SoftwareKeyboardController?, focusManager: FocusManager,
        settingOption: SettingOption, view: View, requestMatch: Boolean = false
    ) {
        softwareKeyboardController?.hide().also { focusManager.clearFocus() }
        if (settingOption.vibrate() == 2)
            Feedback(view).createOneTick() else Feedback(view = view).createClickSound()
        _textState.update {
            it.copy( // update the state of TextState
                requestSave = true, isEditing = false, editButtonState = false,
                requestMatch = requestMatch
            ).also { Log.d("WritingBoardTag", "Done action is triggered") }
        }
    }

    /** Set and check the soft keyboard is appeared or not **/
    private var _softKeyboardState by mutableStateOf(false)
    fun softKeyboardState(setter: Boolean? = null): Boolean {
        if (setter != null) this._softKeyboardState = setter
        return _softKeyboardState
    }

    /** When edit button is clicked **/
    fun editAction(set: SettingOption, vibrate: Feedback) = _textState.update {
        if (set.vibrate() != 0) vibrate.createDoubleTick() else vibrate.createClickSound()
        it.copy(editButtonState = true).also { Log.d("WritingBoardTag", "Edit button is clicked") }
    }

    /** Quick setting tile state **/
    var resultOfQST by mutableIntStateOf(-5)
}
