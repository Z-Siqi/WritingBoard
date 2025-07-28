package com.sqz.writingboard.ui.layout.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import com.sqz.writingboard.ui.theme.getBottomDp
import com.sqz.writingboard.ui.theme.getRightDp
import com.sqz.writingboard.ui.theme.getTopDp
import com.sqz.writingboard.ui.theme.isLandscape
import com.sqz.writingboard.ui.theme.pxToDpInt

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
                BoardContent(
                    viewModel = viewModel,
                    feedback = feedback,
                    writingBoardPadding = writingBoardPadding.collectAsState().value,
                    settings = settings
                )
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
