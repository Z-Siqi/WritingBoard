package com.sqz.writingboard.ui

import android.content.ContentValues
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.clearText
import androidx.compose.foundation.text2.input.delete
import androidx.compose.foundation.text2.input.insert
import androidx.compose.foundation.text2.input.placeCursorAtEnd
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.foundation.text2.input.selectAll
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
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
import kotlinx.coroutines.delay
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

    val fixChooseAllWay = true
    val text2 = rememberTextFieldState()

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
        viewModel.textState = TextFieldValue(savedText, TextRange(0, savedText.length))

        if (fixChooseAllWay && !valueState.initLayout) { //text2
            text2.edit { insert(0, viewModel.textState.text) }
        }

        if (!valueState.initLayout) { //to block save if text not load
            Handler(Looper.getMainLooper()).postDelayed(88) {
                valueState.initLayout = true
            }
            Log.i("WritingBoardTag", "Initializing WritingBoard Text")
        }

        Log.d("WritingBoardTag", "LaunchedEffect: val savedText")
    }

    //clean all texts action
    if (valueState.cleanAllText) {
        if (fixChooseAllWay) {
            text2.clearText()
            valueState.saveAction = true
        } else {
            viewModel.textState.text.let { newText ->
                coroutineScope.launch {
                    dataStore.edit { preferences ->
                        preferences[stringPreferencesKey("saved_text")] =
                            newText.drop(Int.MAX_VALUE)
                    }
                    Log.i("WritingBoardTag", "Save writing board texts")
                }
            }
        }
    }
    //match action by done button
    if (valueState.matchText) {
        if (fixChooseAllWay) {
            valueState.saveAction = true
            text2.text.let { newText ->
                if (
                    (newText.endsWith("\uD83C\uDFF3️\u200D⚧️")) &&
                    (!settingState.readSwitchState("easter_eggs", context)) ||
                    (newText.endsWith("_-OPEN_IT"))
                ) {
                    settingState.writeSwitchState("easter_eggs", context, true)
                    valueState.ee = true
                }
            }
        } else {
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
        }
        valueState.matchText = false
    }
    //save action
    if (valueState.saveAction && valueState.initLayout) {

        if (fixChooseAllWay) { //text2
            viewModel.textState = TextFieldValue(text2.text.toString())
        }

        viewModel.textState.text.let { newText ->
            val textToSave = if (!settingState.readSwitchState(
                    "allow_multiple_lines",
                    context
                ) && newText.isNotEmpty() && newText.last() == '\n'
            ) {
                Log.d("WritingBoardTag", "Removing line breaks and adding a new line.")
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
        (valueState.initLayout) &&
        (!settingState.readSwitchState("disable_auto_save", context))
    ) {
        var save by remember { mutableIntStateOf(0) }
        save++
        if (save == 1) {
            valueState.saveAction = true
        }
        LaunchedEffect(true) {
            delay(1500)
            if (save > 1) {
                save = 0
            }
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
        Log.d("WritingBoardTag", "Read-only text")
    } else {
        //auto save by char
        var autoSaveByChar by remember { mutableIntStateOf(0) }
        if (autoSaveByChar == 20) {
            autoSave = true
            autoSaveByChar = 0
        }

        if (fixChooseAllWay) {
            val isWindowFocused = LocalWindowInfo.current.isWindowFocused
            //fix choose all
            var fixChooseAll by remember { mutableStateOf(false) }
            if (
                (!fixChooseAll) &&
                (text2.text.selectionInChars.length == text2.text.length) &&
                (valueState.initLayout) &&
                (text2.text.isNotEmpty())
            ) {
                text2.edit { placeCursorAtEnd() }
                text2.edit { selectAll() }
                fixChooseAll = true
            } else if (text2.text.selectionInChars.length < text2.text.length) {
                fixChooseAll = false
            }

            //opt editing when reopen app
            var judgeCondition by remember { mutableStateOf(false) }
            if (!settingState.readSwitchState("opt_edit_text", context)) {
                var rememberScroll by remember { mutableIntStateOf(0) }
                var scrollIt by remember { mutableStateOf(false) }
                if (scrollIt && judgeCondition && valueState.isEditing && rememberScroll != 0) {
                    LaunchedEffect(true) {
                        scrollState.scrollTo(rememberScroll)
                    }
                    scrollIt = false
                } else if (!valueState.isEditing) rememberScroll = 0
                var oldChar by remember { mutableIntStateOf(0) }
                if (text2.text.selectionInChars.collapsed && valueState.softKeyboard) {
                    if (text2.text.selectionInChars.start != oldChar) {
                        rememberScroll = scrollState.value
                        LaunchedEffect(true) {
                            delay(500)
                            oldChar = text2.text.selectionInChars.start
                        }
                    }
                }
                if (valueState.softKeyboard && rememberScroll != 0 && isWindowFocused) {
                    Handler(Looper.getMainLooper()).postDelayed(200) {
                        scrollIt = true
                    }
                    Handler(Looper.getMainLooper()).postDelayed(300) {
                        judgeCondition = false
                    }
                }
            } else {
                if (valueState.isEditing && valueState.softKeyboard && isWindowFocused) {
                    LaunchedEffect(true) {
                        delay(200)
                        text2.edit { insert(text2.text.selectionInChars.start, " ") }
                        Handler(Looper.getMainLooper()).postDelayed(1) {
                            text2.edit { placeCursorBeforeCharAt(text2.text.selectionInChars.start -1) }
                            text2.edit { delete(text2.text.selectionInChars.start, text2.text.selectionInChars.start +1) }
                        }
                    }
                }
            }

            //catch typing
            if (text2.undoState.canUndo) {
                autoSaveByChar++
                text2.undoState.clearHistory()
            }
            //text function
            BasicTextField2(
                state = text2,
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
                    .onConsumedWindowInsetsChanged {
                        if (!isWindowFocused) {
                            autoSave = true
                            judgeCondition = true
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
        } else {
            BasicTextField2(
                value = viewModel.textState.text,
                onValueChange = { newText ->
                    viewModel.textState = TextFieldValue(newText)
                    autoSaveByChar++
                },
                scrollState = scrollState,
                modifier = modifier
                    .fillMaxSize()
                    .drawVerticalScrollbar(scrollState)
                    .padding(8.dp)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            valueState.isEditing = true
                            autoSaveByChar++
                        }
                    }
                    .onSizeChanged {
                        autoSave = true
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
}