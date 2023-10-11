package com.sqz.writingboard.ui

import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.os.postDelayed
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.ValueState
import com.sqz.writingboard.WritingBoard
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

@Composable
fun WritingBoardText(
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {

    val buttonState: ValueState = viewModel()
    val viewModel: WritingBoard = viewModel()
    val dataStore = LocalContext.current.dataStore
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusRequester = buttonState.requestFocus
    var scrollToPosition by remember { mutableFloatStateOf(0.0f) }
    val scrollState = rememberScrollState(100)

    val fontFamily = when (settingState.readSegmentedButtonState("font_style", context)) {
        0 -> FontFamily.Monospace
        1 -> FontFamily.Default
        2 -> FontFamily.Serif
        3 -> FontFamily.Cursive
        else -> FontFamily.Default
    }
    val fontStyle = if (settingState.readSwitchState("italics", context)) {
        FontStyle.Italic
    } else {
        FontStyle.Normal
    }
    val color = when (settingState.readSegmentedButtonState("theme", context)) {
        1 -> MaterialTheme.colorScheme.secondary
        2 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }

    LaunchedEffect(true) {
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
    }

    //clean all texts action
    if (buttonState.cleanButton) {
        viewModel.textState.text.let { newText ->
            coroutineScope.launch {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("saved_text")] = newText.drop(Int.MAX_VALUE)
                }
                Log.i("WritingBoardTag", "Save writing board texts")
            }
        }
    }
    //save action by button
    if (buttonState.saveAction) {
        viewModel.textState.text.let { newText ->
            val textToSave = if (!settingState.readSwitchState(
                    "allow_multiple_lines",
                    context
                ) && newText.isNotEmpty() && newText.last() == '\n'
            ) {
                Log.i("WritingBoardTag", "Removing line breaks and adding a new line.")
                newText.trimEnd { it == '\n' }.plus('\n')
            } else {
                newText
            }
            coroutineScope.launch {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("saved_text")] = textToSave
                }
                Log.i("WritingBoardTag", "Save writing board texts by done button")
            }
        }
        buttonState.saveAction = false
    }

    if (settingState.readSwitchState(
            "edit_button",
            context
        ) && !buttonState.editButton || !buttonState.openLayout
    ) {
        BasicText(
            text = viewModel.textState.text,
            modifier = modifier
                .padding(16.dp)
                .focusRequester(focusRequester)
                .verticalScroll(scrollState),
            style = TextStyle.Default.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily,
                fontStyle = fontStyle,
                color = color
            )
        )
        Log.i("WritingBoardTag", "Read-only text")
    } else {
        BasicTextField(
            value = viewModel.textState.text,
            onValueChange = { newText ->
                viewModel.textState = TextFieldValue(newText)
                Handler(Looper.getMainLooper()).postDelayed(2500) {
                    buttonState.saveAction = true
                }
                buttonState.doneButton = true
            },
            singleLine = false,
            modifier = if (buttonState.editScroll) {
                modifier
                    .padding(16.dp)
                    .focusRequester(focusRequester)
                    .verticalScroll(scrollState)
            } else {
                modifier
                    .padding(16.dp)
                    .focusRequester(focusRequester)
                    .onGloballyPositioned { coordinates ->
                        scrollToPosition = scrollState.value + coordinates.positionInRoot().y
                    }
                   },
            textStyle = TextStyle.Default.copy(
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily,
                fontStyle = fontStyle,
                color = color
            )
        )
    }
}