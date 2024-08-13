package com.sqz.writingboard.ui.main.text

import android.util.Log
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.R
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.ui.theme.CursiveCN
import com.sqz.writingboard.ui.theme.themeColor
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.SolidColor
import com.sqz.writingboard.NavScreen
import com.sqz.writingboard.ui.WritingBoardViewModel
import com.sqz.writingboard.ui.main.WritingBoardObject
import com.sqz.writingboard.ui.setting.data.SettingOption
import com.sqz.writingboard.ui.theme.ThemeColor

/**
 * Process text-related such as typing and font.
 **/
@Composable
fun WritingBoardText(
    state: LazyListState,
    savableState: @Composable (
        isSaved: Boolean,
        (reset: Boolean) -> Unit,
        @Composable (toSave: Boolean) -> Unit
    ) -> Unit,
    matchText: (textFieldState: TextFieldState, (text: CharSequence) -> Unit) -> Unit,
    editState: () -> Unit, // tell isEditing
    isEditing: Boolean,
    readOnly: Boolean,
    editButton: Boolean,
    requestSave: () -> Unit,
    bottomHigh: Int,
    yInScreenFromClickAsLazyList: Int,
    modifier: Modifier = Modifier,
    viewModel: WritingBoardViewModel = viewModel(),
) {
    val context = LocalContext.current
    val set = SettingOption(context)
    val textFieldState = viewModel.textFieldState
    val coroutineScope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }
    WritingBoardObject.focusRequest(
        focusRequester = focusRequester,
        textFieldState = textFieldState
    )

    /** Load saved text **/
    if (!viewModel.savedText.isInitialized) {
        LaunchedEffect(true) {
            viewModel.loadSavedText(context = context) {
                textFieldState.clearText()
                textFieldState.edit {
                    viewModel.savedText.value?.let { insert(0, it) }
                }
            }
        }
    }

    var autoSave by remember { mutableStateOf(false) }

    val fontSize = when (set.fontSize()) {
        0 -> 18.sp
        1 -> 23.sp
        2 -> 33.sp
        else -> 18.sp
    }
    val fontLanguage = if (
        (stringResource(R.string.used_language) == "CN") ||
        (stringResource(R.string.used_language) == "TW")
    ) CursiveCN else FontFamily.Cursive
    val fontFamily = when (set.fontStyle()) {
        0 -> FontFamily.Monospace
        1 -> FontFamily.Default
        2 -> FontFamily.Serif
        3 -> fontLanguage
        else -> FontFamily.Default
    }
    val fontWeight = when (set.fontWeight()) {
        0 -> FontWeight.Normal
        1 -> FontWeight.SemiBold
        2 -> FontWeight.W900
        else -> FontWeight.Normal
    }
    val fontStyle = if (set.italics()) {
        FontStyle.Italic
    } else {
        FontStyle.Normal
    }

    //match action by done button
    matchText(textFieldState) {
        it.let { newText ->
            if (
                (newText.endsWith("\uD83C\uDFF3️\u200D⚧️")) &&
                (!set.easterEgg()) ||
                (newText.endsWith("_-OPEN_IT"))
            ) {
                set.easterEgg(true)
                NavScreen.updateScreen(ee = true)
            }
        }
    }

    /** Save Action **/
    var saved by remember { mutableStateOf(false) }
    savableState(saved, { reset ->
        if (reset) saved = false
    }) { toSave ->
        LaunchedEffect(toSave) {
            if (toSave) coroutineScope.launch {
                textFieldState.text.toString().let { newText ->
                    val textToSave = if (!set.allowMultipleLines() &&
                        newText.isNotEmpty() && newText.last() == '\n'
                    ) {
                        Log.d("WritingBoardTag", "Removing line breaks and adding a new line.")
                        newText.trimEnd { it == '\n' }.plus('\n')
                    } else if (newText.isEmpty()) {
                        Log.w("WritingBoardTag", "Saved Nothing!")
                        newText
                    } else {
                        newText
                    }
                    viewModel.saveText(textToSave, context).let {
                        if (it) saved = true
                    }
                }
                WritingBoardWidget().updateAll(context)
            }
        }
    }

    if (autoSave && !set.disableAutoSave()) LaunchedEffect(true) {
        requestSave()
        autoSave = false
    }
    // auto save when not WindowFocused
    val isWindowFocused = LocalWindowInfo.current.isWindowFocused
    val textNotEmpty = textFieldState.text.isNotEmpty()
    if (!isWindowFocused && textNotEmpty && isEditing) LaunchedEffect(true) {
        autoSave = true
    }

    if (set.editButton() && !editButton || readOnly) {
        BasicText(
            text = textFieldState.text.toString(),
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            style = TextStyle.Default.copy(
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                fontStyle = fontStyle,
                color = themeColor(ThemeColor.TextColor)
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
        //catch text change
        var oldText by remember { mutableIntStateOf(0) }
        if (textFieldState.text.length != oldText) {
            //codes
            autoSaveByChar += 1
            oldText = textFieldState.text.length
        }

        //text function
        BasicTextField2(
            state = textFieldState,
            lazyListState = state,
            verticalScrollWhenCursorUnderKeyboard = true,
            extraScrollValue = bottomHigh,
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp)
                .focusRequester(focusRequester)
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) {
                        editState()
                    }
                }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, _ -> }
                },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
            textStyle = TextStyle.Default.copy(
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                fontStyle = fontStyle,
                color = themeColor(ThemeColor.TextColor)
            ),
            yInScreenFromClickAsLazyList = yInScreenFromClickAsLazyList
        )
    }
}
