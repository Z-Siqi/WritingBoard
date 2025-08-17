package com.sqz.writingboard.ui.component

import android.content.Context
import android.graphics.Rect
import android.view.MotionEvent
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Basic text composable that extend from [BasicTextField]. Below param is the modify by
 * the [BasicTextField2].
 *
 * @param cursorBrush [Brush] to paint cursor with. [BasicTextField2] modify the default brush color
 *   to MaterialTheme.colorScheme.onSurfaceVariant for fix [BasicTextField] default brush color
 *   not change to light color when system in dark mode.
 * @param enableFixSelection A [BasicTextField2] param, enable this will fix the delete text
 *   with selection issues.
 * @param enableFixNonEnglishKeyboard A [BasicTextField2] param, enable this will fix cannot delete
 *   selected text (at Text editing page on soft-keyboard) in non-english keyboard.
 * @param verticalScrollWhenCursorUnderKeyboard A [BasicTextField2] param, enable this will allow
 *   the text field scroll to visible when user input the cursor is under the keyboard.
 * @param lazyListState A [BasicTextField2] param, for [verticalScrollWhenCursorUnderKeyboard] work,
 *   please link the [LazyListState] to parent [androidx.compose.foundation.lazy.LazyColumn] method.
 * @param yInScreenFromClickAsLazyList A [BasicTextField2] param, for
 *   [verticalScrollWhenCursorUnderKeyboard] work, please add [Modifier.pointerInteropFilter] to
 *   [androidx.compose.foundation.lazy.LazyColumn] and get the screen y coordinate from user click,
 *   and pass it to this param.
 * @param extraScrollValue A [BasicTextField2] param, for [verticalScrollWhenCursorUnderKeyboard];
 *   this value is a extra scroll value for solve may not able to scroll to right position caused by
 *   nav bars height or other system bars height. Dp value as Int unit.
 * @param onFailure when [BasicTextField] throw exception, this function will be called.
 */
@OptIn(ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
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
    onFailure: () -> Unit = {},
    // Fixes Control
    enableFixSelection: Boolean = true,
    enableFixNonEnglishKeyboard: Boolean = true,
    verticalScrollWhenCursorUnderKeyboard: Boolean = false,
    lazyListState: LazyListState = rememberLazyListState(),
    yInScreenFromClickAsLazyList: Int = 0, // Please give the value of yInScreenFromClick if is LazyListState
    extraScrollValue: Int = 1
) {
    //opt edit when scrollable
    val isWindowFocused = LocalWindowInfo.current.isWindowFocused
    var yInScreenFromClick by remember { mutableIntStateOf(0) }
    var isEditing by remember { mutableStateOf(false) }
    val density = LocalDensity.current.density
    if (verticalScrollWhenCursorUnderKeyboard) {
        var initScroll by rememberSaveable { mutableStateOf(false) }
        val keyboardHeight = currentKeyboardHeightInPx()
        val screenHeight = LocalWindowInfo.current.containerSize.height.toFloat()

        if (WindowInsets.isImeVisible && !isLandscape()) {
            val high = screenHeight - (extraScrollValue * density).toInt()

            val isLazyList by remember { derivedStateOf { lazyListState.layoutInfo.totalItemsCount != 0 } }
            if (!isLazyList) LaunchedEffect(true) {
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
            } else LaunchedEffect(true) {
                delay(300)
                if (yInScreenFromClickAsLazyList >= high - keyboardHeight) {
                    val scroll = yInScreenFromClickAsLazyList - (high - keyboardHeight)
                    lazyListState.animateScrollBy(scroll, SpringSpec(0.8F))
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
    }
    runCatching {
        BasicTextField(
            state = state,
            modifier = modifier
                .deleteTextWithSelectionFix(state, enableFixSelection)
                .nonEnglishKeyboardFix(state, enableFixNonEnglishKeyboard)
                // If the scroll is a LazyListState,
                // please add this pointerInteropFilter to top of the function!
                // (Such as add this to LazyColumn modifier)
                // And the value of yInScreenFromClick should be the value of yInScreenFromClickAsLazyList
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
                .onFocusEvent { focusState -> isEditing = focusState.isFocused },
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
    }.onFailure {
        onFailure()
    }
}

/** Fix delete text with selection issues **/
@Composable
private fun Modifier.deleteTextWithSelectionFix(state: TextFieldState, enabled: Boolean): Modifier {
    val coroutineScope = rememberCoroutineScope()
    var runFix by remember { mutableStateOf(false) }

    val isSelected = state.selection.end != state.selection.start
    return this.onKeyEvent { keyEvent ->
        if (keyEvent.nativeKeyEvent.keyCode == 59 && isSelected && enabled) {
            if (state.selection.start == 0 ||
                state.selection.end == state.text.length
            ) {
                if (runFix) coroutineScope.launch {
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
                    runFix = false
                }
            }
            true
        } else {
            runFix = true
            false
        }
    }
}

/** Fix non-english keyboard delete after selected errors **/
@Composable
private fun Modifier.nonEnglishKeyboardFix(state: TextFieldState, enabled: Boolean): Modifier {
    return this.onPreviewKeyEvent { keyEvent ->
        if (keyEvent.key == Key.Backspace) {
            if (enabled) state.edit {
                val end = state.selection.end
                val start = state.selection.start

                if (end < start) {
                    delete(end, start)
                }
                if (start < end) {
                    delete(start, end)
                }
            }
        }
        false
    }
}

@Composable
private fun isLandscape(): Boolean {
    return LocalWindowInfo.current.containerSize.let { it.width > (it.height * 1.1) }
}

@Composable
private fun currentKeyboardHeightInPx(): Int {
    val context = LocalContext.current
    val localScreenHeight = LocalWindowInfo.current.containerSize.height
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
