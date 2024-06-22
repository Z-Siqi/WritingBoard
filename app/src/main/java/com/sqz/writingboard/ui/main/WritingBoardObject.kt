package com.sqz.writingboard.ui.main

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.sqz.writingboard.component.Vibrate
import com.sqz.writingboard.ui.setting.SettingOption

object WritingBoardObject {

    var isEditing by mutableStateOf(false)
    var saveAction by mutableStateOf(false)
    var matchText by mutableStateOf(false)

    var softKeyboard: Boolean = false
    var editButton: Boolean = false
    var readOnlyText: Boolean = false
    var readOnlyTextScroll: Boolean = true

    private var _doneAction by mutableStateOf(false)
    private var _doneWithMatch by mutableStateOf(false)

    fun doneAction(matchText: Boolean = false) {
        _doneAction = true
        if (matchText) _doneWithMatch = true
    }

    fun doneRequest(
        softwareKeyboardController: SoftwareKeyboardController?,
        focusManager: FocusManager,
        settingOption: SettingOption,
        context: Context,
    ) {
        if (_doneAction) {
            if (_doneWithMatch) this.matchText = true
            softwareKeyboardController?.hide()
            focusManager.clearFocus()
            if (settingOption.vibrate() == 2) Vibrate(context).createOneTick()
            this.saveAction = true
            this.isEditing = false
            this.editButton = false
            Log.d("WritingBoardTag", "Done action is triggered")
            _doneAction = false
            _doneWithMatch = false
        }
    }

    fun onClickSetting(navAction: () -> Unit) {
        this.doneAction()
        navAction()
    }

    fun editAction(set: SettingOption, vibrate: Vibrate) {
        this.editButton = true
        if (set.vibrate() != 0) for (i in 0..2) vibrate.createOneTick()
        Log.d("WritingBoardTag", "Edit button is clicked")
    }

    fun cleanAllText(it: TextFieldState) {
        it.clearText()
        this.saveAction
    }

    fun editingHorizontalScreen(
        config: Configuration,
        withoutSoftKeyboard: Boolean = false
    ): Boolean {
        val isHorizontal = config.screenWidthDp > (config.screenHeightDp * 1.1)
        return if (isHorizontal && this.softKeyboard && this.isEditing) {
            Log.d("WritingBoardTag", "editingHorizontalScreen is true")
            true
        } else isHorizontal && this.isEditing && withoutSoftKeyboard
    }

}