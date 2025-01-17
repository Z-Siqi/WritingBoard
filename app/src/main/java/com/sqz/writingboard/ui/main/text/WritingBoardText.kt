package com.sqz.writingboard.ui.main.text

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.insert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.NavScreen
import com.sqz.writingboard.ui.WritingBoardViewModel
import com.sqz.writingboard.ui.main.TextState
import com.sqz.writingboard.preferences.SettingOption
import com.sqz.writingboard.ui.theme.ThemeColor
import com.sqz.writingboard.ui.theme.themeColor
import java.io.File

/**
 * Text-related such as typing and font.
 */
@Composable
fun WritingBoardText(
    state: LazyListState,
    matchText: ((text: CharSequence) -> Unit) -> Unit,
    editStateAsTrue: (Boolean, type: TextState) -> Unit, // tell isEditing
    textState: TextState,
    requestSave: (text: String, context: Context, forceSave: Boolean) -> Unit,
    bottomHigh: Int,
    yInScreenFromClickAsLazyList: Int,
    modifier: Modifier = Modifier,
    viewModel: WritingBoardViewModel = viewModel(),
) {
    val context = LocalContext.current
    val set = SettingOption(context)
    val textFieldState = viewModel.textFieldState

    val focusRequester = remember { FocusRequester() }
    viewModel.focusRequestState(focusRequester = focusRequester)

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

    //match action by done button
    matchText {
        it.let { newText ->
            if (newText.endsWith("\uD83C\uDFF3️\u200D⚧️") && !set.easterEgg() ||
                newText.endsWith("_-OPEN_IT")
            ) {
                set.easterEgg(true)
                NavScreen.updateScreen(ee = true)
            }
        }
    }

    /** Save Text Action **/
    var autoSave by remember { mutableStateOf(false) }
    requestSave(textFieldState.text.toString(), context, false)
    if (autoSave && !set.disableAutoSave()) LaunchedEffect(Unit) {
        requestSave(textFieldState.text.toString(), context, true)
        autoSave = false
    }

    // auto save when not WindowFocused
    val isWindowFocused = LocalWindowInfo.current.isWindowFocused
    val textNotEmpty = textFieldState.text.isNotEmpty()
    if (!isWindowFocused && textNotEmpty && textState.isEditing) LaunchedEffect(true) {
        autoSave = true
    }

    val fontSize = when (set.fontSize()) {
        0 -> 18.sp
        1 -> 23.sp
        2 -> 33.sp
        else -> 18.sp
    }
    var customFont by remember { mutableStateOf<FontFamily?>(FontFamily.Default) }
    LaunchedEffect(Unit) {
        val fontFile = File(context.filesDir, "font.ttf")
        if (fontFile.exists()) {
            val font = Font(fontFile)
            customFont = FontFamily(font)
        }
    }
    val fontFamily = when (set.fontStyle()) {
        0 -> FontFamily.Monospace
        1 -> FontFamily.Default
        2 -> FontFamily.Serif
        3 -> when (set.fontStyleExtra()) {
            0 -> FontFamily.Cursive
            1 -> customFont
            else -> FontFamily.Default
        }
        else -> FontFamily.Default
    }
    val fontWeight = when (set.fontWeight()) {
        0 -> FontWeight.Normal
        1 -> FontWeight.SemiBold
        2 -> FontWeight.W900
        else -> FontWeight.Normal
    }
    val fontStyle = if (set.italics()) FontStyle.Italic else FontStyle.Normal

    if (set.editButton() && !textState.editButtonState || textState.readOnlyText) {
        var logShowLimit by rememberSaveable { mutableStateOf(false) }
        BasicText(
            text = textFieldState.text.toString(),
            modifier = modifier
                .fillMaxSize()
                .background(themeColor(ThemeColor.BoardColor))
                .padding(8.dp),
            style = TextStyle.Default.copy(
                fontSize = fontSize,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                fontStyle = fontStyle,
                color = themeColor(ThemeColor.TextColor)
            )
        )
        if (!logShowLimit) Log.d("WritingBoardTag", "Read-only text").also {
            logShowLimit = true
        }
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
                .background(themeColor(ThemeColor.BoardColor))
                .padding(8.dp)
                .focusRequester(focusRequester)
                .onFocusEvent { focusState ->
                    if (focusState.isFocused) editStateAsTrue(true, TextState(isEditing = true))
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
