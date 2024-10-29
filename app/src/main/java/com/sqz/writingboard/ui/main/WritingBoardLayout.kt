package com.sqz.writingboard.ui.main

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
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
import com.sqz.writingboard.ui.main.nav.ButtonClickType
import com.sqz.writingboard.ui.main.nav.HideStyle
import com.sqz.writingboard.ui.main.nav.LayoutButton
import com.sqz.writingboard.ui.main.nav.NavBarStyle
import com.sqz.writingboard.ui.main.text.WritingBoardText
import com.sqz.writingboard.ui.setting.data.SettingOption
import com.sqz.writingboard.ui.theme.ThemeColor
import com.sqz.writingboard.ui.theme.isAndroid15OrAbove
import com.sqz.writingboard.ui.theme.isLandscape
import com.sqz.writingboard.ui.theme.navBarHeightDp
import com.sqz.writingboard.ui.theme.themeColor
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WritingBoardLayout(
    navToSetting: () -> Unit,
    view: View,
    modifier: Modifier = Modifier,
    viewModel: WritingBoardViewModel = viewModel(),
) {
    val scrollState = rememberLazyListState()
    var screenController by remember { mutableStateOf(false) }

    val set = SettingOption(context = view.context)
    val textState = viewModel.textState.collectAsState().value

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
        val editingHorizontalScreen = editingHorizontalScreen(
            textState.isEditing, viewModel.softKeyboardState(), isLandscape
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
            if (textState.isEditing) when {
                set.buttonStyle() == 2 -> if (isLandscape) 0.dp else 45.dp
                screenController -> 45.dp
                else -> 0.dp
            } else when {
                set.buttonStyle() == 1 && screenController -> 60.dp
                set.buttonStyle() == 2 && !isLandscape -> navBarHeightDp.dp
                else -> 0.dp
            }
        } else 0.dp
        val animateBoardBottom by animateDpAsState(targetValue = boardBottom, label = "BoardBottom")
        val boardEnd = when {
            set.buttonStyle() == 2 && isLandscape -> 88.dp
            editingHorizontalScreen -> 100.dp
            textState.isEditing && isLandscape -> 80.dp
            else -> 0.dp
        }
        val animateBoardEnd by animateDpAsState(targetValue = boardEnd, label = "BoardEnd")
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
        val windowInsetsPadding = if (isAndroid15OrAbove) {
            modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .windowInsetsPadding(WindowInsets.displayCutout)
        } else modifier
        Column(
            modifier = modifier.padding(
                bottom = animateBoardBottom,
                end = animateBoardEnd
            ) then windowInsetsPadding then horizontalScreen
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
                    delay(520)
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
                    val getButtonStyle = set.buttonStyle() == 1 || set.buttonStyle() == 0
                    val addSpacer = getButtonStyle && !textState.editButtonState &&
                            set.editButton() || getButtonStyle && !set.alwaysVisibleText()
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

        // The action of ButtonClickType
        val onClickType: (ButtonClickType) -> Unit = { type ->
            when (type) {
                ButtonClickType.Done -> doneAction(true)
                ButtonClickType.Clean -> viewModel.cleanAllText()
                ButtonClickType.Setting -> onClickSetting()
                ButtonClickType.Edit -> viewModel.editAction(set, Feedback(view))
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
            onClickType = onClickType,
            readCleanAllText = set.cleanAllText(),
            defaultStyle = set.buttonStyle() == 1,
            editButton = set.editButton() && !textState.editButtonState,
            readAlwaysVisibleText = set.alwaysVisibleText() && set.buttonStyle() != 2,
            modifier = windowInsetsPadding,
            enable = set.buttonStyle() != 2
        )

        // NavBar control style
        if (set.buttonStyle() == 2) NavBarStyle(
            isEditing = textState.isEditing,
            onClickType = onClickType,
            readCleanAllText = set.cleanAllText(),
            editButton = textState.editButtonState,
            readEditButton = set.editButton(),
            softKeyboard = viewModel.softKeyboardState(),
            modifier = windowInsetsPadding
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
        WritingBoardWidget().updateAll(view.context)
        WritingBoardTextOnlyWidget().updateAll(view.context)
    }
}

private fun editingHorizontalScreen(
    isEditing: Boolean, softKeyboard: Boolean, isLandscape: Boolean,
    withoutSoftKeyboard: Boolean = false
): Boolean {
    return if (isLandscape && softKeyboard && isEditing) {
        Log.d("WritingBoardTag", "editingHorizontalScreen is true")
        true
    } else isLandscape && isEditing && withoutSoftKeyboard
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
    WritingBoardLayout({}, LocalView.current)
}
