package com.sqz.writingboard.ui.layout.handler

import android.content.Context
import android.util.Log
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.WindowInfo
import com.sqz.writingboard.common.feedback.Feedback
import com.sqz.writingboard.preference.PreferenceLocal
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.NavRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.text.endsWith

class RequestHandler {
    private val _viewModel: MainViewModel

    constructor(viewModel: MainViewModel) {
        this._viewModel = viewModel
    }

    fun onSettingsClick(feedback: Feedback) {
        feedback.onClickEffect()
        if (_viewModel.state.value.isInReadOnlyMode) {
            _viewModel.state.update { it.copy(isEditable = false) }
        }
        _viewModel.navControllerHandler.navigate(NavRoute.Setting)
    }

    fun onEditClick(feedback: Feedback) {
        feedback.onDoubleTickEffect()
        _viewModel.state.update { it.copy(isEditable = true) }
    }

    fun finishClick(context: Context, feedback: Feedback) {
        feedback.onHeavyClickEffect()
        _request.update { it.copy(freeFocus = true) }
        _viewModel.textFieldState().let { text ->
            val prefs = PreferenceLocal(context)
            if (text.text.toString().endsWith("\uD83C\uDFF3️\u200D⚧️") && !prefs.easterEgg()) {
                prefs.easterEgg(true)
                _viewModel.navControllerHandler.navigate(NavRoute.EE)
            } else if (text.text.toString().endsWith("_-OPEN_IT")) {
                text.edit { delete(text.text.length - 9, text.text.length) }
                _viewModel.navControllerHandler.navigate(NavRoute.EE)
            } else text
        }
        _viewModel.saveTextToStorage(context)
    }

    fun freeEditing() {
        _viewModel.state.value.let {
            if (it.isImeOn) _request.update { update ->
                update.copy(freeKeyboard = true)
            } else _request.update { update ->
                update.copy(freeFocus = true)
            }
        }
    }

    fun requestWriting() {
        if (_viewModel.state.value.isEditable) _request.update { it.copy(getFocus = true) }
    }

    fun saveTextWhenWindowNotFocused(windowInfo: WindowInfo, context: Context) {
        val textIsEdited =
            _viewModel.textFieldState().text.hashCode() != _viewModel.savedTextHashCode
        if (!windowInfo.isWindowFocused && _viewModel.savedTextHashCode != null && textIsEdited) {
            Log.d("WritingBoard", "RequestHandler: isWindowFocused")
            _viewModel.saveTextToStorage(context, false)
        }
    }

    fun saveTextImmediately(context: Context) {
        if (_viewModel.savedTextHashCode != null) {
            if (_viewModel.textFieldState().text.hashCode() != _viewModel.savedTextHashCode) {
                _viewModel.saveTextToStorage(context, false)
            }
        }
    }

    private fun readOnlyModeController(context: Context) {
        _viewModel.state.update { it ->
            it.copy(isInReadOnlyMode = SettingOption(context).editButton())
        }
    }

    data class Request(
        val freeFocus: Boolean = false,
        val getFocus: Boolean = false,
        val freeKeyboard: Boolean = false,
    )

    private val _request = MutableStateFlow<Request>(Request())
    val getValue: StateFlow<Request> = _request

    fun focusManager(
        getValue: Request,
        context: Context,
        softwareKeyboardController: SoftwareKeyboardController?,
        focusManager: FocusManager,
        focusRequester: FocusRequester
    ): FocusRequester {
        this.readOnlyModeController(context)
        if (getValue.freeFocus) _request.update {
            softwareKeyboardController?.hide().also {
                focusManager.clearFocus()
            }
            if (_viewModel.state.value.isInReadOnlyMode) _viewModel.state.update {
                it.copy(isEditable = false)
            }
            it.copy(freeFocus = false)
        }
        if (getValue.getFocus) _request.update {
            focusRequester.requestFocus().also {
                _viewModel.textFieldState().edit { placeCursorAtEnd() }
            }
            it.copy(getFocus = false)
        }
        if (getValue.freeKeyboard) _request.update {
            softwareKeyboardController?.hide()
            it.copy(freeKeyboard = false)
        }
        return focusRequester
    }
}
