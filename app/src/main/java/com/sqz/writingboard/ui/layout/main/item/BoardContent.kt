package com.sqz.writingboard.ui.layout.main.item

import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.common.feedback.Feedback
import com.sqz.writingboard.common.io.deleteFont
import com.sqz.writingboard.common.io.importedFontName
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.component.BasicTextField2
import com.sqz.writingboard.ui.component.drawVerticalScrollbar
import com.sqz.writingboard.ui.layout.handler.RequestHandler
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import com.sqz.writingboard.ui.theme.getBottomDp
import com.sqz.writingboard.ui.theme.getSystemTopBarMaxHeightDp
import com.sqz.writingboard.ui.theme.navBarHeightDpIsEditing
import com.sqz.writingboard.ui.theme.pxToDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import java.io.File

@Composable
fun BoardContent(
    viewModel: MainViewModel,
    feedback: Feedback,
    writingBoardPadding: WritingBoardPadding,
    cursorPosition: (x: Int, y: Int) -> Unit = { _, _ -> },
    settings: SettingOption,
    scrollState: LazyListState
) {
    val context = LocalContext.current
    val focusManager = viewModel.requestHandler.focusManager(
        getValue = viewModel.requestHandler.getValue.collectAsState().value,
        context = context,
        softwareKeyboardController = LocalSoftwareKeyboardController.current,
        focusManager = LocalFocusManager.current,
        focusRequester = remember { FocusRequester() }
    )
    var customFont by remember { mutableStateOf<FontFamily?>(FontFamily.Default) }
    LaunchedEffect(Unit) {
        val fontFile = File(context.filesDir, importedFontName)
        if (fontFile.exists()) {
            val font = Font(fontFile)
            customFont = FontFamily(font)
        }
    }
    val getState = viewModel.state.collectAsState().value
    var yInScreenFromClick = remember { mutableIntStateOf(0) }
    val extraScrollValue = (writingBoardPadding.bottom.value).toInt()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .drawVerticalScrollbar(scrollState)
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    if (settings.vibrate() != 0) feedback.onClickSound()
                    viewModel.requestHandler.requestWriting()
                }
            }
            .yInScreenFromClickGetter(
                value = yInScreenFromClick, writingBoardPadding = writingBoardPadding
            ),
        state = scrollState
    ) {
        val enableSpacer = !settings.alwaysVisibleText() && settings.buttonStyle() == 1
                || settings.alwaysVisibleText() && viewModel.boardSizeHandler.editWithLowScreenHeight()
        if (getState.isEditable) item {
            Spacer(Modifier.height(4.dp))
            BasicTextField2(
                state = viewModel.textFieldState(context),
                lazyListState = scrollState,
                verticalScrollWhenCursorUnderKeyboard = true,
                extraScrollValue = if (settings.buttonStyle() != 2) extraScrollValue else {
                    extraScrollValue + navBarHeightDpIsEditing
                },
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusManager)
                    .onFocusEvent { focusState ->
                        viewModel.state.update { it.copy(isFocus = focusState.isFocused) }
                    },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                textStyle = textStyle(settings, customFont, WritingBoardTheme.color.boardText),
                onTextLayout = { textLayoutResult ->
                    if (viewModel.state.value.isFocus) { // get cursor position
                        val getCursorRect = textLayoutResult.invoke()?.getCursorRect(
                            viewModel.textFieldState().selection.start
                        )
                        val cursorX = getCursorRect?.left
                        val cursorY = getCursorRect?.top?.minus(
                            scrollState.firstVisibleItemScrollOffset.toFloat()
                        )
                        if (cursorY != null && cursorX != null) {
                            cursorPosition(cursorX.toInt(), cursorY.toInt())
                        }
                    }
                    if (settings.instantSaveText()) { // saveTextImmediately
                        viewModel.requestHandler.saveTextImmediately(context)
                    }
                },
                yInScreenFromClickAsLazyList = yInScreenFromClick.intValue,
                onFailure = { deleteFont(context) },
                keyboardOptions = keyboardOptions(settings),
            )
            Spacer(enableSpacer, viewModel.requestHandler, writingBoardPadding)
        } else item {
            Spacer(Modifier.height(4.dp))
            BasicText(
                text = viewModel.textFieldState(context).text.toString(),
                modifier = Modifier.fillMaxSize(),
                style = textStyle(settings, customFont, WritingBoardTheme.color.boardText)
            )
            Spacer(enableSpacer, viewModel.requestHandler, writingBoardPadding)
        }
    }
    // Change board size when the button will obscures the text
    if (settings.alwaysVisibleText() && settings.buttonStyle() != 2) {
        val getMoveBoardState = getMoveBoardState(scrollState)
        val defaultButtonMode = getMoveBoardState && settings.buttonStyle() == 1
        val hidedButtonMode = if (settings.editButton() && getState.isEditable) {
            getMoveBoardState && settings.buttonStyle() == 0
        } else getMoveBoardState && getState.isFocus
        viewModel.boardSizeHandler.boardBottomPadding(defaultButtonMode || hidedButtonMode)
        viewModel.boardSizeHandler.boardEndPadding()
    }
}

@Composable
private fun Modifier.yInScreenFromClickGetter(
    value: MutableIntState, writingBoardPadding: WritingBoardPadding
): Modifier {
    val density = LocalDensity.current
    val barHeight = WindowInsets.navigationBars.getBottomDp() + getSystemTopBarMaxHeightDp()
    val paddingHeight = writingBoardPadding.bottom.value
    val modifier = this.pointerInteropFilter { motionEvent: MotionEvent ->
        when (motionEvent.action) { //detect this item screen y coordinate when click
            MotionEvent.ACTION_DOWN -> {
                val y = motionEvent.y
                value.intValue = y.toInt() + ((barHeight + paddingHeight) * density.density).toInt()
                Log.d("WritingBoard_Debug", "[value.intValue y: ${value.intValue}] [y: $y]")
            }
        }
        false
    }
    return modifier
}

@Composable
private fun Spacer(
    enable: Boolean, requestHandler: RequestHandler, writingBoardPadding: WritingBoardPadding
) {
    if (enable) {
        val bottomHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current).pxToDp()
        val height = bottomHeight + writingBoardPadding.bottom + 20.dp
        Spacer(Modifier.height(height) then Modifier.pointerInput(Unit) {
            detectTapGestures { _ -> requestHandler.requestWriting() }
        })
    }
}

@Composable
@ReadOnlyComposable
private fun textStyle(
    settings: SettingOption, customFont: FontFamily?, textColor: Color
): TextStyle {
    val fontSize = when (settings.fontSize()) {
        0 -> 18.sp
        1 -> 23.sp
        2 -> 33.sp
        else -> 18.sp
    }
    val fontWeight = when (settings.fontWeight()) {
        0 -> FontWeight.Normal
        1 -> FontWeight.SemiBold
        2 -> FontWeight.W900
        else -> FontWeight.Normal
    }
    val fontFamily = when (settings.fontStyle()) {
        0 -> FontFamily.Monospace
        1 -> FontFamily.Default
        2 -> FontFamily.Serif
        3 -> when (settings.fontStyleExtra()) {
            0 -> FontFamily.Cursive
            1 -> customFont
            else -> FontFamily.Default
        }

        else -> FontFamily.Default
    }
    val fontStyle = if (settings.italics()) FontStyle.Italic else FontStyle.Normal
    return TextStyle.Default.copy(
        fontSize = fontSize, fontWeight = fontWeight,
        fontFamily = fontFamily, fontStyle = fontStyle, color = textColor
    )
}

@Composable
private fun keyboardOptions(settings: SettingOption): KeyboardOptions {
    val capitalization = if (settings.capitalizationSentences()) {
        KeyboardCapitalization.Sentences
    } else {
        KeyboardCapitalization.Unspecified
    }
    return KeyboardOptions.Default.copy(capitalization = capitalization)
}

@Composable
private fun getMoveBoardState(scrollState: LazyListState): Boolean {
    var state by remember { mutableStateOf(false) }
    val highValue = (75 * LocalDensity.current.density).toInt()
    var maxValue by remember { mutableIntStateOf(-1) }
    val offset by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset } }
    if (!scrollState.canScrollForward) {
        LaunchedEffect(offset, Unit) {
            if (scrollState.firstVisibleItemScrollOffset != 0) {
                maxValue = scrollState.firstVisibleItemScrollOffset
            }
            delay(50)
            if (scrollState.firstVisibleItemScrollOffset == maxValue && !state) {
                delay(50)
                state = true
            }
        }
    } else if (offset < maxValue - highValue) {
        state = false
    }
    return state
}
