package com.sqz.writingboard.ui

import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.postDelayed
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.R
import com.sqz.writingboard.classes.ValueState
import com.sqz.writingboard.dataStore
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.settingState
import com.sqz.writingboard.ui.component.drawVerticalScrollbar
import com.sqz.writingboard.ui.theme.CursiveCN
import com.sqz.writingboard.ui.theme.themeColor
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

class WritingBoard : ViewModel() {
    var textState by mutableStateOf(TextFieldValue())
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WritingBoardText(scrollState: ScrollState, modifier: Modifier = Modifier) {

    val valueState: ValueState = viewModel()
    val viewModel: WritingBoard = viewModel()
    val dataStore = LocalContext.current.dataStore
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var autoSave by remember { mutableStateOf(false) }

    val fontSize = when (settingState.readSegmentedButtonState("font_size", context)) {
        0 -> 18.sp
        1 -> 23.sp
        2 -> 33.sp
        else -> 18.sp
    }
    val fontLanguage = if (
        (stringResource(R.string.used_language) == "CN") ||
        (stringResource(R.string.used_language) == "TW")
    ) CursiveCN else FontFamily.Cursive
    val fontFamily = when (settingState.readSegmentedButtonState("font_style", context)) {
        0 -> FontFamily.Monospace
        1 -> FontFamily.Default
        2 -> FontFamily.Serif
        3 -> fontLanguage
        else -> FontFamily.Default
    }
    val fontWeight = when (settingState.readSegmentedButtonState("font_weight", context)) {
        0 -> FontWeight.Normal
        1 -> FontWeight.SemiBold
        2 -> FontWeight.W900
        else -> FontWeight.Normal
    }
    val fontStyle = if (settingState.readSwitchState("italics", context)) {
        FontStyle.Italic
    } else {
        FontStyle.Normal
    }

    LaunchedEffect(true) { //to load saved texts
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
        viewModel.textState = TextFieldValue(savedText)

        if (!valueState.initLayout) { //to block save if text not load
            valueState.initLayout = true
            Log.i("WritingBoardTag", "Initializing WritingBoard Text")
        }

        Log.i("WritingBoardTag", "LaunchedEffect: val savedText")
    }

    //clean all texts action
    if (valueState.cleanAllText) {
        viewModel.textState.text.let { newText ->
            coroutineScope.launch {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("saved_text")] = newText.drop(Int.MAX_VALUE)
                }
                Log.i("WritingBoardTag", "Save writing board texts")
            }
        }
    }
    //match action by done button
    if (valueState.matchText) {
        viewModel.textState.text.let { newText ->
            if (
                (newText.endsWith("\uD83C\uDFF3️\u200D⚧️")) &&
                (!settingState.readSwitchState("easter_eggs", context)) ||
                (newText.endsWith("_-OPEN_IT"))
            ) {
                settingState.writeSwitchState("easter_eggs", context, true)
                valueState.ee = true
            }
        }
        valueState.matchText = false
    }
    //save action
    if (valueState.saveAction && valueState.initLayout) {
        viewModel.textState.text.let { newText ->
            val textToSave = if (!settingState.readSwitchState(
                    "allow_multiple_lines",
                    context
                ) && newText.isNotEmpty() && newText.last() == '\n'
            ) {
                Log.i("WritingBoardTag", "Removing line breaks and adding a new line.")
                newText.trimEnd { it == '\n' }.plus('\n')
            } else if (newText.isEmpty()) {
                Log.w("WritingBoardTag", "Saved Nothing!")
                newText
            } else {
                newText
            }
            coroutineScope.launch {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("saved_text")] = textToSave
                }
                Log.i("WritingBoardTag", "Save writing board texts")
            }
        }
        LaunchedEffect(true) {
            WritingBoardWidget().updateAll(context)
        }
        valueState.saveAction = false
    }
    if (
        (autoSave) &&
        (!settingState.readSwitchState("disable_auto_save", context))
    ) {
        Handler(Looper.getMainLooper()).postDelayed(2500) {
            valueState.saveAction = true
            autoSave = false
        }
    }
    if (
        (settingState.readSwitchState("edit_button", context)) &&
        (!valueState.editButton) ||
        (!valueState.initLayout) ||
        (valueState.readOnlyText)
    ) {
        BasicText(
            text = viewModel.textState.text,
            modifier = modifier
                .fillMaxSize()
                .drawVerticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(8.dp),
            style = TextStyle.Default.copy(
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                fontStyle = fontStyle,
                color = themeColor("textColor")
            )
        )
        Log.i("WritingBoardTag", "Read-only text")
    } else {
        BasicTextField2(
            value = viewModel.textState.text,
            onValueChange = { newText ->
                viewModel.textState = TextFieldValue(newText)
                autoSave = true
            },
            scrollState = scrollState,
            modifier = modifier
                .fillMaxSize()
                .drawVerticalScrollbar(scrollState)
                .padding(8.dp)
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        valueState.isEditing = true
                    }
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, _ -> }
                },
            textStyle = TextStyle.Default.copy(
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                fontStyle = fontStyle,
                color = themeColor("textColor")
            )
        )
    }
}