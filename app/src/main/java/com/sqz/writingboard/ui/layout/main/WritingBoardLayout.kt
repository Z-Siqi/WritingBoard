package com.sqz.writingboard.ui.layout.main

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.preferences.SettingOption
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.layout.handler.RequestHandler
import com.sqz.writingboard.ui.layout.main.control.DefaultButton
import com.sqz.writingboard.ui.layout.main.item.BoardContent
import com.sqz.writingboard.ui.layout.main.item.WritingBoard
import com.sqz.writingboard.ui.theme.isLandscape
import com.sqz.writingboard.ui.theme.pxToDpInt

@Composable
fun WritingBoardLayout(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val settings = SettingOption(context = LocalContext.current)
    var contentSize by remember { mutableStateOf(IntSize(0, 0)) }
    val writingBoardPadding = viewModel.boardSizeHandler.writingBoardPadding(
        screenSize = LocalWindowInfo.current.containerSize.let {
            IntSize(it.width.pxToDpInt(), it.height.pxToDpInt())
        },
        contentSize = contentSize,
        navHeight = WindowInsets.navigationBars.getBottomDp(),
        stateHeight = WindowInsets.let {
            if (isLandscape) it.statusBars.getTopDp() else it.displayCutout.getTopDp()
        }
    )
    Scaffold(
        bottomBar = {
        },
        modifier = modifier.bgClick(viewModel.requestHandler),
        contentWindowInsets = WindowInsets.waterfall
    ) { paddingValues ->
        WritingBoard(
            writingBoardPadding = writingBoardPadding.collectAsState().value,
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
                .windowInsetPaddings(viewModel.state.collectAsState().value.isImeOn),
            contentSize = { contentSize = it },
            backgroundColor = MaterialTheme.colorScheme.background,
            borderColor = MaterialTheme.colorScheme.primary,
        ) {
            BoardContent(
                viewModel = viewModel,
                settings = settings
            )
        }
        DefaultButton(
            state = viewModel.state.collectAsState(),
            writingBoardPadding = writingBoardPadding.collectAsState().value,
            requestHandler = viewModel.requestHandler,
            settings = settings
        )
    }
}

private fun Modifier.bgClick(requestHandler: RequestHandler): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures { _ -> requestHandler.freeEditing() }
    }
}

@ReadOnlyComposable
@Composable
private fun WindowInsets.getTopDp(): Int = this.getTop(LocalDensity.current).pxToDpInt()

@ReadOnlyComposable
@Composable
private fun WindowInsets.getBottomDp(): Int = this.getBottom(LocalDensity.current).pxToDpInt()

@Composable
private fun Modifier.windowInsetPaddings(isImeOn: Boolean): Modifier {
    val displayCutout = if (!isLandscape) this else {
        this.windowInsetsPadding(WindowInsets.displayCutout)
    }
    val navigationBars = if (isImeOn) this else {
        this.windowInsetsPadding(WindowInsets.navigationBars)
    }
    val stateBars = this.windowInsetsPadding(WindowInsets.statusBars)
    return displayCutout then navigationBars then stateBars
}

@Preview
@Composable
private fun Preview() {
    val viewModel: MainViewModel = viewModel()
    WritingBoardLayout(viewModel)
}
