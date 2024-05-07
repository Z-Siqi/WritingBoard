package com.sqz.writingboard.ui.main.text

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldDecorator
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BasicTextField2(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    inputTransformation: InputTransformation? = null,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onKeyboardAction: KeyboardActionHandler? = null,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    onTextLayout: (Density.(getResult: () -> TextLayoutResult?) -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
    cursorBrush: Brush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
    outputTransformation: OutputTransformation? = null,
    decorator: TextFieldDecorator? = null,
    scrollState: ScrollState = rememberScrollState(),
    // Fixes Control
    enableFixSelection: Boolean = true,
    enableFixNonEnglishKeyboard: Boolean = true,
    verticalScrollWhenCursorUnderKeyboard: Boolean = false,
    extraScrollValue: Int = 1
) {
    //fix delete text with selection issues
    if (enableFixSelection) {
        val isSelected = state.selection.end != state.selection.start
        if (state.selection.start == 0 && isSelected ||
            state.selection.end == state.text.length && isSelected
        ) {
            LaunchedEffect(true) {
                state.edit {
                    this.selection = TextRange(
                        state.selection.start,
                        state.selection.end - 1
                    )
                }
                state.edit {
                    this.selection = TextRange(
                        state.selection.start,
                        state.selection.end + 1
                    )
                }
            }
        }
    }
    //opt edit when scrollable
    val isWindowFocused = LocalWindowInfo.current.isWindowFocused
    var yInScreenFromClick by remember { mutableIntStateOf(0) }
    var isEditing by remember { mutableStateOf(false) }
    val density = LocalDensity.current.density
    if (verticalScrollWhenCursorUnderKeyboard) {
        var initScroll by rememberSaveable { mutableStateOf(false) }
        val keyboardHeight = currentKeyboardHeightInPx()
        val screenHeight = LocalConfiguration.current.screenHeightDp * LocalDensity.current.density

        if (isKeyboardVisible() && !isLandscape()) {
            val high = screenHeight - (extraScrollValue * density).toInt()

            LaunchedEffect(true) {
                delay(300)
                if (yInScreenFromClick >= high - keyboardHeight) {
                    if (!initScroll) {
                        yInScreenFromClick += 100
                        initScroll = true
                    }
                    val scroll = scrollState.value + (yInScreenFromClick - (high - keyboardHeight))
                    scrollState.animateScrollTo(scroll.toInt(), SpringSpec(0.8F))
                    yInScreenFromClick = 0
                }
            }
        }
        var rememberScroll by rememberSaveable { mutableIntStateOf(0) }
        var scrollIt by rememberSaveable { mutableStateOf(false) }
        if (!isWindowFocused && !scrollIt && isEditing) {
            rememberScroll = scrollState.value
            scrollIt = true
        }
        if (isWindowFocused && scrollIt && isEditing) {
            LaunchedEffect(true) {
                delay(200)
                scrollState.scrollTo(rememberScroll)
                scrollIt = false
            }
        }
        if (!isEditing) rememberScroll = 0
    }

    BasicTextField(
        state = state,
        modifier = modifier
            .onKeyEvent { keyEvent ->
                //fix non-english keyboard delete after selected errors
                if (keyEvent.key == Key.Backspace) {
                    if (enableFixNonEnglishKeyboard) {
                        state.edit {
                            val end = state.selection.end
                            val start = state.selection.start

                            if (end < start) {
                                delete(end, start)
                            } else if (start < end) {
                                delete(start, end)
                            }
                        }
                    }
                    true
                } else {
                    false
                }
            }
            .pointerInteropFilter { motionEvent: MotionEvent ->
                //detect screen y coordinate when click
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val y = motionEvent.y
                        yInScreenFromClick = y.toInt() + (48 * density).toInt()
                    }
                }
                false
            }
            .onFocusEvent { focusState ->
                isEditing = focusState.isFocused
            },
        enabled = enabled,
        readOnly = readOnly,
        inputTransformation = inputTransformation,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        onKeyboardAction = onKeyboardAction,
        lineLimits = lineLimits,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        outputTransformation = outputTransformation,
        decorator = decorator,
        scrollState = scrollState
    )
}

@Composable
private fun isLandscape(): Boolean {
    val config = LocalConfiguration.current
    return config.screenWidthDp > (config.screenHeightDp * 1.1)
}

@Composable
private fun isKeyboardVisible(): Boolean {
    return currentKeyboardHeightInPx() != 0
}

@Composable
private fun currentKeyboardHeightInPx(): Int {
    val context = LocalContext.current
    val localScreenHeight =
        (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).toInt()
    val rootView = LocalView.current
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val returnZero = 0
    var height by remember { mutableIntStateOf(0) }
    var notZero by remember { mutableStateOf(false) }
    var calculateNotZeroValue by remember { mutableIntStateOf(0) }
    DisposableEffect(rootView, inputMethodManager) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val keyboardNowHeight = localScreenHeight - rect.bottom
            val keyboardHeight = if (keyboardNowHeight < 0) {
                notZero = true
                calculateNotZeroValue = keyboardNowHeight * -1
                returnZero
            } else {
                if (notZero) {
                    keyboardNowHeight + calculateNotZeroValue
                } else {
                    keyboardNowHeight
                }
            }
            height = keyboardHeight
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    return height
}
