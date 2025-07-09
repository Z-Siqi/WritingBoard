package com.sqz.writingboard.ui.layout.main.item

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.component.KeyboardVisibilityObserver
import com.sqz.writingboard.preferences.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.component.drawVerticalScrollbar
import com.sqz.writingboard.ui.layout.LocalState
import com.sqz.writingboard.ui.main.text.BasicTextField2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Composable
fun BoardContent(viewModel: MainViewModel, settings: SettingOption) {
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val focusManager = viewModel.requestHandler.focusManager(
        getValue = viewModel.requestHandler.getValue.collectAsState().value,
        context = context,
        softwareKeyboardController = LocalSoftwareKeyboardController.current,
        focusManager = LocalFocusManager.current,
        focusRequester = remember { FocusRequester() }
    )
    val getState = viewModel.state.collectAsState().value
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .drawVerticalScrollbar(scrollState)
            .pointerInput(Unit) {
                detectTapGestures { _ -> viewModel.requestHandler.requestWriting() }
            },
        state = scrollState
    ) {
        if (getState.isEditable) item {
            BasicTextField2(
                state = viewModel.textFieldState(context),
                lazyListState = scrollState,
                verticalScrollWhenCursorUnderKeyboard = true,
                extraScrollValue = 1,
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusManager)
                    .onFocusEvent { focusState ->
                        viewModel.state.update { it.copy(isFocus = focusState.isFocused) }
                    },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                textStyle = textStyle(settings),
                yInScreenFromClickAsLazyList = 0
            )
            viewModel.requestHandler.saveTextWhenWindowNotFocused(
                windowInfo = LocalWindowInfo.current,
                context = context
            )
        } else item {
            BasicText(
                text = viewModel.textFieldState(context).text.toString(),
                modifier = Modifier.fillMaxSize(),
                style = textStyle(settings)
            )
        }
    }
    ImeVisibilityHandler(
        state = viewModel.state
    )
}

@Composable
@ReadOnlyComposable
private fun textStyle(settings: SettingOption): TextStyle {
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
            //1 -> customFont //TODO: customer font
            else -> FontFamily.Default
        }
        else -> FontFamily.Default
    }
    val fontStyle = if (settings.italics()) FontStyle.Italic else FontStyle.Normal
    return TextStyle.Default.copy(
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        fontStyle = fontStyle,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
private fun ImeVisibilityHandler(state: MutableStateFlow<LocalState>) {
    KeyboardVisibilityObserver { isVisible ->
        state.update { it.copy(isImeOn = isVisible) }
    }
    LaunchedEffect(state.collectAsState().value.isImeOn) {
        if (state.value.isImeOn) Log.d("WritingBoard", "Keyboard is visible") else {
            Log.d("WritingBoard", "Keyboard is close")
        }
    }
}
