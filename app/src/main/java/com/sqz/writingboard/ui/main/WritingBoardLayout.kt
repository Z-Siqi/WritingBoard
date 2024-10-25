package com.sqz.writingboard.ui.main

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.NavScreen
import com.sqz.writingboard.R
import com.sqz.writingboard.component.Feedback
import com.sqz.writingboard.component.KeyboardVisibilityObserver
import com.sqz.writingboard.glance.WritingBoardTextOnlyWidget
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.ui.WritingBoardViewModel
import com.sqz.writingboard.ui.component.ManualLayout
import com.sqz.writingboard.ui.component.drawVerticalScrollbar
import com.sqz.writingboard.ui.main.control.ButtonClickType
import com.sqz.writingboard.ui.main.control.HideStyle
import com.sqz.writingboard.ui.main.control.LayoutButton
import com.sqz.writingboard.ui.main.control.NavBarStyle
import com.sqz.writingboard.ui.main.text.WritingBoardText
import com.sqz.writingboard.ui.setting.data.SettingOption
import com.sqz.writingboard.ui.theme.ThemeColor
import com.sqz.writingboard.ui.theme.themeColor
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WritingBoardLayout(
    navToSetting: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier,
    viewModel: WritingBoardViewModel = viewModel(),
) {
    val config = LocalConfiguration.current
    val view = LocalView.current
    val scrollState = rememberLazyListState()

    var screenController by remember { mutableStateOf(false) }

    val set = SettingOption(context = context)

    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val doneAction: (requestMatch: Boolean) -> Unit = {
        viewModel.doneAction(softwareKeyboardController, focusManager, set, view, requestMatch = it)
    }
    val onClickSetting = { doneAction(false).also { navToSetting() } }

    //Layout
    Surface(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { _ -> doneAction(false) }
            },
        color = themeColor(ThemeColor.BackgroundColor)
    ) {
        val textState = viewModel.textState.collectAsState().value
        val editingHorizontalScreen = editingHorizontalScreen(
            textState.isEditing, viewModel.softKeyboardState(), config
        )
        // HideStyle for WritingBoard
        if (!viewModel.softKeyboardState() && set.buttonStyle() == 0) HideStyle(
            onClickSetting = onClickSetting,
            onClickEdit = { viewModel.editAction(set, Feedback(view)) },
            editButton = set.editButton(),
            readIsOffEditButtonManual = set.offEditButtonManual(),
            readIsOffButtonManual = set.offButtonManual()
        )
        //for bottom style
        val boardBottom = if (!editingHorizontalScreen) {
            if (textState.isEditing) {
                if (set.buttonStyle() == 2) {
                    55.dp
                } else if (screenController) {
                    45.dp
                } else 0.dp
            } else {
                if (set.buttonStyle() == 1 && screenController) {
                    60.dp
                } else if (set.buttonStyle() == 2) {
                    70.dp
                } else 0.dp
            }
        } else 0.dp
        val animateBoardBottom by animateDpAsState( // animate
            targetValue = boardBottom,
            label = "BoardBottom"
        )
        //for horizontal screen
        val horizontalScreen =
            if (editingHorizontalScreen) {
                modifier.padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 4.dp)
            } else {
                modifier.padding(20.dp)
            }
        //for calculate always visible text
        val highValue = (77 * LocalDensity.current.density).toInt()
        //alwaysVisibleText actions
        if (set.alwaysVisibleText() && set.buttonStyle() != 2) {
            var maxValue by remember { mutableIntStateOf(-1) }
            val offset by remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset } }
            if (!scrollState.canScrollForward && textState.editButtonState ||
                !scrollState.canScrollForward && !set.editButton()
            ) {
                LaunchedEffect(true) {
                    if (scrollState.firstVisibleItemScrollOffset != 0) {
                        maxValue = scrollState.firstVisibleItemScrollOffset
                    }
                    delay(50)
                    if (scrollState.firstVisibleItemScrollOffset == maxValue && !screenController) {
                        delay(50)
                        screenController = true
                        Log.i("WritingBoardTag", "screenController is true")
                    }
                }
            } else if (offset < maxValue - highValue) {
                screenController = false
            }
        }
        //writing board
        Column(
            modifier = modifier.padding(bottom = animateBoardBottom) then horizontalScreen
                .shadow(5.dp, RoundedCornerShape(26.dp))
                .border(
                    4.dp, color = themeColor(ThemeColor.ShapeColor),
                    RoundedCornerShape(26.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = themeColor(ThemeColor.BoardColor),
                modifier = modifier.fillMaxSize(),
                shape = RoundedCornerShape(26.dp)
            ) {
                var yInScreenFromClick by remember { mutableIntStateOf(0) }
                val density = LocalDensity.current.density
                if (viewModel.softKeyboardState()) LaunchedEffect(true) {
                    yInScreenFromClick = 0
                }
                val layoutHeight by remember { derivedStateOf { scrollState.layoutInfo.viewportSize.height } }
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .drawVerticalScrollbar(scrollState)
                        .pointerInteropFilter { motionEvent: MotionEvent ->
                            when (motionEvent.action) { //detect screen y coordinate when click
                                MotionEvent.ACTION_DOWN -> {
                                    val y = motionEvent.y
                                    yInScreenFromClick = y.toInt() + (48 * density).toInt()
                                }
                            }
                            false
                        },
                    state = scrollState
                ) {
                    val high = if (set.buttonStyle() == 2) 110 else 58
                    val addSpacer = set.buttonStyle() == 1 && !textState.editButtonState &&
                            set.editButton() || set.buttonStyle() == 1 && !set.alwaysVisibleText()
                    val textAreaHeight = if (addSpacer) 64 else 0
                    item {
                        WritingBoardText(
                            state = scrollState,
                            matchText = { viewModel.matchText(it, textState.requestMatch) },
                            editStateAsTrue = viewModel::textStateSetter,
                            textState = textState,
                            requestSave = viewModel::formatThenSave,
                            bottomHigh = high + if (screenController) 70 else 0,
                            modifier = modifier.heightIn(min = ((layoutHeight - 5) / density - textAreaHeight).dp),
                            yInScreenFromClickAsLazyList = if (
                                !scrollState.canScrollForward &&
                                !set.alwaysVisibleText() && set.buttonStyle() == 1
                            ) {
                                yInScreenFromClick += 28
                                yInScreenFromClick
                            } else yInScreenFromClick,
                            viewModel = viewModel
                        )
                        if (addSpacer) Spacer(modifier = modifier
                            .height(64.dp)
                            .fillMaxWidth()
                            .background(themeColor(ThemeColor.BoardColor))
                            .semantics(mergeDescendants = true) {}
                            .pointerInput(Unit) {
                                if (!set.editButton()) detectTapGestures { _ ->
                                    viewModel.focusRequestState(setter = true)
                                } else detectTapGestures { _ -> }
                            }
                        )
                    }
                }
                if (textState.isEditing) LaunchedEffect(true) {
                    Feedback(view = view).createClickSound()
                }
            }
        }

        // The default control style and editing buttons
        LayoutButton(
            screenController = screenController,
            isEditing = when (set.buttonStyle()) {
                0 -> textState.editButtonState || textState.isEditing
                1 -> textState.isEditing
                2 -> editingHorizontalScreen && textState.isEditing
                else -> textState.isEditing
            },
            onClickType = { type ->
                when (type) {
                    ButtonClickType.Done -> doneAction(true)
                    ButtonClickType.Clean -> viewModel.cleanAllText()
                    ButtonClickType.Setting -> onClickSetting()
                    ButtonClickType.Edit -> viewModel.editAction(set, Feedback(view))
                }
            },
            readCleanAllText = set.cleanAllText(),
            defaultStyle = set.buttonStyle() == 1,
            editButton = set.editButton() && !textState.editButtonState,
            readAlwaysVisibleText = set.alwaysVisibleText()
        )

        // NavBar control style
        if (set.buttonStyle() == 2) NavBarStyle(
            isEditing = textState.isEditing,
            onClickSetting = onClickSetting,
            onClickEdit = { viewModel.editAction(set, Feedback(view)) },
            onClickDone = { doneAction(true) },
            onClickClean = { viewModel.cleanAllText() },
            editButton = textState.editButtonState,
            editingHorizontalScreen = editingHorizontalScreen,
            readEditButton = set.editButton(),
            readCleanAllText = set.cleanAllText()
        )

        //manual of button style
        if (set.buttonStyle() == 0) Manual(
            stateSetter = { viewModel.textStateSetter(it, TextState(readOnlyText = true)) },
            readEditButton = set.editButton(),
            readIsOffButtonManual = set.offButtonManual(),
            readIsOffEditButtonManual = set.offEditButtonManual(),
        )
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            val bottomShadow = if (set.buttonStyle() != 2) {
                modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .shadow(10.dp)
            } else {
                modifier
            }
            Spacer(modifier = bottomShadow)
        }
    }

    KeyboardVisibilityObserver { isVisible ->
        viewModel.softKeyboardState(setter = isVisible)
        if (isVisible) Log.d("WritingBoardTag", "Keyboard is visible") else {
            screenController = false
            Log.d("WritingBoardTag", "Keyboard is close")
        }
    }
    LaunchedEffect(true) {
        WritingBoardWidget().updateAll(context)
        WritingBoardTextOnlyWidget().updateAll(context)
    }
}

private fun editingHorizontalScreen(
    isEditing: Boolean,
    softKeyboard: Boolean,
    config: Configuration,
    withoutSoftKeyboard: Boolean = false
): Boolean {
    val isHorizontal = config.screenWidthDp > (config.screenHeightDp * 1.1)
    return if (isHorizontal && softKeyboard && isEditing) {
        Log.d("WritingBoardTag", "editingHorizontalScreen is true")
        true
    } else isHorizontal && isEditing && withoutSoftKeyboard
}

@Composable
private fun Manual(
    stateSetter: (Boolean) -> Unit,
    readEditButton: Boolean, readIsOffButtonManual: Boolean, readIsOffEditButtonManual: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (!readIsOffButtonManual) {
        stateSetter(true)
        ManualLayout(
            modifierPadding = modifier.padding(bottom = 380.dp, end = 58.dp),
            onClick = {
                SettingOption(context).offButtonManual(true)
                stateSetter(false)
                NavScreen.updateScreen()
            },
            text = stringResource(R.string.button_manual)
        )
    }
    //manual of edit button
    if (!readIsOffEditButtonManual && readEditButton) {
        stateSetter(true)
        ManualLayout(
            modifierPadding = modifier.padding(top = 300.dp, end = 50.dp),
            onClick = {
                SettingOption(context).offEditButtonManual(true)
                stateSetter(false)
                NavScreen.updateScreen()
            },
            text = stringResource(R.string.edit_button_manual)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WritingBoardPreview() {
    WritingBoardLayout({}, LocalContext.current)
}
