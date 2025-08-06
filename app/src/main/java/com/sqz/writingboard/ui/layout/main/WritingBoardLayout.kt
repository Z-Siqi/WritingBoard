package com.sqz.writingboard.ui.layout.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.common.feedback.AndroidFeedback
import com.sqz.writingboard.common.feedback.Feedback
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.layout.handler.RequestHandler
import com.sqz.writingboard.ui.layout.main.control.DefaultButton
import com.sqz.writingboard.ui.layout.main.control.HidedButton
import com.sqz.writingboard.ui.layout.main.control.NavButtons
import com.sqz.writingboard.ui.layout.main.control.OutsideButton
import com.sqz.writingboard.ui.layout.main.item.BoardContent
import com.sqz.writingboard.ui.layout.main.item.WritingBoard
import com.sqz.writingboard.ui.layout.main.item.WritingBoardPadding
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import com.sqz.writingboard.ui.theme.dpToPxInt
import com.sqz.writingboard.ui.theme.getBottomDp
import com.sqz.writingboard.ui.theme.getBottomPx
import com.sqz.writingboard.ui.theme.getLeftDp
import com.sqz.writingboard.ui.theme.getRightDp
import com.sqz.writingboard.ui.theme.getSystemTopBarMaxHeightDp
import com.sqz.writingboard.ui.theme.getTopDp
import com.sqz.writingboard.ui.theme.isLandscape
import com.sqz.writingboard.ui.theme.pxToDpInt
import kotlinx.coroutines.delay

@Composable
fun WritingBoardLayout(
    viewModel: MainViewModel,
    settings: SettingOption,
    feedback: Feedback,
    modifier: Modifier = Modifier
) {
    val defaultButtonMode = settings.buttonStyle() == 1 && !settings.alwaysVisibleText()
    val hideButtonMode = settings.buttonStyle() == 0
    val navBarButtonMode = settings.buttonStyle() == 2

    var contentSize by remember { mutableStateOf(IntSize(0, 0)) }
    val writingBoardPadding = viewModel.boardSizeHandler.writingBoardPadding(
        screenSize = LocalWindowInfo.current.containerSize.let {
            IntSize(it.width.pxToDpInt(), it.height.pxToDpInt())
        },
        contentSize = contentSize,
        navHeight = WindowInsets.navigationBars.getBottomDp(),
        stateHeight = WindowInsets.let {
            if (isLandscape) it.statusBars.getTopDp() else it.displayCutout.getTopDp()
        },
    )
    val stateValue = viewModel.state.collectAsState().value
    val navButtons = NavButtons(
        state = stateValue,
        requestHandler = viewModel.requestHandler,
        settings = settings,
        feedback = feedback
    )
    Scaffold(
        modifier = modifier.bgClick(viewModel.requestHandler),
        bottomBar = {
            navButtons.NavBar(enable = navBarButtonMode && !isLandscape)
        },
        contentWindowInsets = WindowInsets.waterfall,
        containerColor = WritingBoardTheme.color.backgroundColor,
    ) { paddingValues ->
        if (hideButtonMode) HidedButton(
            state = stateValue,
            requestHandler = viewModel.requestHandler,
            navControllerHandler = viewModel.navControllerHandler,
            settings = settings,
            feedback = feedback
        )
        Row(modifier = modifier.padding(paddingValues)) {
            WritingBoard(
                writingBoardPadding = writingBoardPadding.collectAsState().value,
                modifier = modifier
                    .weight(1f)
                    .fillMaxSize()
                    .windowInsetPaddings(
                        displayCutout = if (!navBarButtonMode) !isLandscape else {
                            WindowInsets.displayCutout.getRightDp() > 1
                        },
                        navigationBars = navBarButtonMode && !isLandscape || stateValue.isImeOn
                    ),
                contentSize = { contentSize = it },
                contentPadding = PaddingValues(
                    start = 15.dp, end = 8.dp, top = 8.dp, bottom = 8.dp
                ),
                imePadding = !navBarButtonMode || isLandscape,
                backgroundColor = WritingBoardTheme.color.boardBackground,
                borderColor = WritingBoardTheme.color.boardShape,
            ) {
                val scrollState = rememberLazyListState()
                var cursorPosition by remember { mutableStateOf(IntSize(0, 0)) }
                BoardContent(
                    viewModel = viewModel,
                    feedback = feedback,
                    writingBoardPadding = writingBoardPadding.collectAsState().value,
                    cursorPosition = { x, y -> cursorPosition = IntSize(x, y) },
                    settings = settings,
                    scrollState = scrollState
                )
                val text = viewModel.textFieldState().text
                var rememberCurrent by remember { mutableIntStateOf(text.hashCode()) }
                if (!navBarButtonMode && rememberCurrent != text.hashCode()) {
                    ScrollToView(
                        buttonWidth = if (settings.alwaysVisibleText()) 80 else 56,
                        cursorPosition = cursorPosition,
                        writingBoardPadding = writingBoardPadding.collectAsState(),
                        scrollState = scrollState
                    ) { rememberCurrent = text.hashCode() }
                }
            }
            navButtons.NavRail(enable = navBarButtonMode && isLandscape)
        }
        if (defaultButtonMode) DefaultButton(
            state = stateValue,
            writingBoardPadding = writingBoardPadding.collectAsState().value,
            requestHandler = viewModel.requestHandler,
            settings = settings,
            feedback = feedback
        )
        if (!navBarButtonMode) OutsideButton(
            onHidedButtonInReadOnly = hideButtonMode,
            enableOutsideButton = settings.alwaysVisibleText(),
            boardSizeHandler = viewModel.boardSizeHandler,
            state = stateValue,
            writingBoardPadding = writingBoardPadding.collectAsState().value,
            requestHandler = viewModel.requestHandler,
            settings = settings,
            feedback = feedback
        )
    }
    BackHandler(stateValue.isFocus) { // Disable back and exit app directly when is editing
        viewModel.requestHandler.freeEditing()
    }
}

// Scroll to view when cursor under Finish button
@Composable
private fun ScrollToView(
    buttonWidth: Int,
    cursorPosition: IntSize,
    writingBoardPadding: State<WritingBoardPadding>,
    scrollState: LazyListState,
    then: () -> Unit = {}
) {
    val imeHeight = WindowInsets.ime.getBottomPx()
    val barHeight = getSystemTopBarMaxHeightDp()
    val displayCutout = WindowInsets.displayCutout.getLeftDp()
    val containerSize = LocalWindowInfo.current.containerSize
    val padding = writingBoardPadding.value

    val buttonEnd = containerSize.width - (padding.end + buttonWidth.dp + 32.dp + displayCutout.dp)
        .value.dpToPxInt()
    val buttonHeightPx = (padding.bottom + padding.top + 96.dp).value.dpToPxInt()
    val buttonBottom = containerSize.height - buttonHeightPx - imeHeight - barHeight
    val cursorWidth = cursorPosition.width + (padding.start + 12.dp).value.dpToPxInt()
    val extraValue = 25.dp.value.dpToPxInt()

    var scrollToView by remember { mutableStateOf(false) }
    if (cursorPosition.height > (buttonBottom - extraValue) && cursorWidth > buttonEnd) {
        scrollToView = true
    } else then()
    if (scrollToView) {
        val scrollValue = (padding.bottom.value + 88).toInt().dpToPxInt().let {
            if (cursorPosition.height < buttonBottom) it - (extraValue / 2) else it
        }
        val isEnoughScreenHeight: Boolean =
            (containerSize.height - imeHeight - barHeight) > (scrollValue * 2.2)
        if (isEnoughScreenHeight) LaunchedEffect(Unit) {
            scrollState.animateScrollBy(scrollValue.toFloat())
            delay(50)
            then().also { scrollToView = false }
        }
    }
}

// On background click
private fun Modifier.bgClick(requestHandler: RequestHandler): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures { _ -> requestHandler.freeEditing() }
    }
}

@Composable
private fun Modifier.windowInsetPaddings( // WindowInsets padding for WritingBoard
    displayCutout: Boolean, navigationBars: Boolean
): Modifier {
    val displayCutoutLocal = if (displayCutout) this else {
        this.windowInsetsPadding(WindowInsets.displayCutout)
    }
    val navigationBarsLocal = if (navigationBars) this else {
        this.windowInsetsPadding(WindowInsets.navigationBars)
    }
    val stateBars = this.windowInsetsPadding(WindowInsets.statusBars)
    return displayCutoutLocal then navigationBarsLocal then stateBars
}

@Preview
@Composable
private fun Preview() {
    val viewModel: MainViewModel = viewModel()
    val settings = SettingOption(LocalContext.current)
    WritingBoardLayout(viewModel, settings, AndroidFeedback(settings, LocalView.current))
}
