package com.sqz.writingboard.ui.main

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.glance.appwidget.updateAll
import com.sqz.writingboard.NavScreen
import com.sqz.writingboard.component.KeyboardVisibilityObserver
import com.sqz.writingboard.R
import com.sqz.writingboard.component.Feedback
import com.sqz.writingboard.glance.WritingBoardTextOnlyWidget
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.ui.WritingBoardViewModel
import com.sqz.writingboard.ui.main.control.NavBarStyle
import com.sqz.writingboard.ui.main.control.HideStyle
import com.sqz.writingboard.ui.component.ManualLayout
import com.sqz.writingboard.ui.component.drawVerticalScrollbar
import com.sqz.writingboard.ui.main.control.LayoutButton
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

    var isKeyboardVisible by remember { mutableStateOf(false) }
    var screenController by remember { mutableStateOf(false) }

    val set = SettingOption(context = context)
    val valueObject = WritingBoardObject
    valueObject.doneRequest(
        softwareKeyboardController = LocalSoftwareKeyboardController.current,
        focusManager = LocalFocusManager.current,
        settingOption = set,
        context = context,
        view = view
    )

    //Layout
    Surface(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    valueObject.doneAction()
                }
            },
        color = themeColor(ThemeColor.BackgroundColor)
    ) {
        // HideStyle for WritingBoard
        if (!valueObject.softKeyboard && set.buttonStyle() == 0) HideStyle(
            onClickSetting = { valueObject.onClickSetting(navToSetting) },
            onClickEdit = { valueObject.editAction(set, Feedback(context, view)) },
            editButton = set.editButton(),
            readIsOffEditButtonManual = set.offEditButtonManual(),
            readIsOffButtonManual = set.offButtonManual()
        )
        Box {
            //for bottom style
            val boardBottom = if (!valueObject.editingHorizontalScreen(config)) {
                if (valueObject.isEditing) {
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
            val horizontalScreen = if (valueObject.editingHorizontalScreen(config)) {
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
                if (!scrollState.canScrollForward && valueObject.editButton ||
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
                    if (valueObject.softKeyboard) LaunchedEffect(true) {
                        yInScreenFromClick = 0
                    }
                    val layoutHeight by remember { derivedStateOf { scrollState.layoutInfo.viewportSize.height } }
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .drawVerticalScrollbar(scrollState)
                            .pointerInteropFilter { motionEvent: MotionEvent ->
                                //detect screen y coordinate when click
                                when (motionEvent.action) {
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
                        val addSpacer = set.buttonStyle() == 1 && !valueObject.editButton &&
                                set.editButton() || set.buttonStyle() == 1 && !set.alwaysVisibleText()
                        val textAreaHeight = if (addSpacer) 64 else 0
                        item {
                            WritingBoardText(
                                state = scrollState,
                                savableState = { isSaved, reset, toSave ->
                                    if (isSaved) {
                                        valueObject.saveAction = false
                                        reset(true)
                                    } else {
                                        toSave(valueObject.saveAction)
                                    }
                                },
                                matchText = { state, text ->
                                    if (valueObject.matchText) {
                                        valueObject.saveAction = true
                                        state.text.let(text)
                                        valueObject.matchText = false
                                    }
                                },
                                editState = { valueObject.isEditing = true },
                                isEditing = valueObject.isEditing,
                                readOnly = valueObject.readOnlyText,
                                editButton = valueObject.editButton,
                                requestSave = { valueObject.saveAction = true },
                                bottomHigh = high + if (screenController) 70 else 0,
                                modifier = modifier
                                    .heightIn(min = ((layoutHeight - 5) / density - textAreaHeight).dp),
                                yInScreenFromClickAsLazyList = if (
                                    !scrollState.canScrollForward &&
                                    !set.alwaysVisibleText() && set.buttonStyle() == 1
                                ) {
                                    yInScreenFromClick += 28
                                    yInScreenFromClick
                                } else yInScreenFromClick,
                                viewModel = viewModel
                            )
                            if (addSpacer) {
                                Spacer(modifier = modifier
                                    .height(64.dp)
                                    .fillMaxWidth()
                                    .background(Color.Unspecified)
                                    .semantics(mergeDescendants = true) {}
                                    .pointerInput(Unit) {
                                        if (!set.editButton()) detectTapGestures { _ ->
                                            valueObject.focusRequest()
                                        } else detectTapGestures { _ -> }
                                    }
                                )
                            }
                        }
                    }
                    if (valueObject.isEditing) LaunchedEffect(true) {
                        Feedback(view = view).createClickSound()
                    }
                }
            }
        }

        // The default control style and editing buttons
        LayoutButton(
            screenController = screenController,
            isEditing = when (set.buttonStyle()) {
                0 -> valueObject.editButton || valueObject.isEditing
                1 -> valueObject.isEditing
                2 -> valueObject.editingHorizontalScreen(config) && valueObject.isEditing
                else -> valueObject.isEditing
            },
            onClickDone = { valueObject.doneAction(true) },
            onClickClean = { valueObject.cleanAllText(viewModel.textFieldState) },
            readCleanAllText = set.cleanAllText(),
            defaultStyle = set.buttonStyle() == 1,
            onClickSetting = { valueObject.onClickSetting(navToSetting) },
            editButton = set.editButton() && !valueObject.editButton,
            onClickEdit = { valueObject.editAction(set, Feedback(context, view)) },
            readAlwaysVisibleText = set.alwaysVisibleText()
        )

        // NavBar control style
        if (set.buttonStyle() == 2) NavBarStyle(
            isEditing = valueObject.isEditing,
            onClickSetting = { valueObject.onClickSetting(navToSetting) },
            onClickEdit = { valueObject.editAction(set, Feedback(context, view)) },
            onClickDone = { valueObject.doneAction(true) },
            onClickClean = { valueObject.cleanAllText(viewModel.textFieldState) },
            editButton = valueObject.editButton,
            editingHorizontalScreen = valueObject.editingHorizontalScreen(config),
            readEditButton = set.editButton(),
            readCleanAllText = set.cleanAllText()
        )

        //manual of button style
        if (set.buttonStyle() == 0) {
            Manual(
                readEditButton = set.editButton(),
                readIsOffButtonManual = set.offButtonManual(),
                readIsOffEditButtonManual = set.offEditButtonManual(),
            )
        }
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
        isKeyboardVisible = isVisible
        if (isVisible) {
            valueObject.softKeyboard = true
            Log.d("WritingBoardTag", "Keyboard is visible")
        } else {
            valueObject.softKeyboard = false
            screenController = false
            Log.d("WritingBoardTag", "Keyboard is close")
        }
    }
    LaunchedEffect(true) {
        WritingBoardWidget().updateAll(context)
        WritingBoardTextOnlyWidget().updateAll(context)
    }
}

@Composable
private fun Manual(
    readEditButton: Boolean,
    readIsOffButtonManual: Boolean,
    readIsOffEditButtonManual: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val valueObject = WritingBoardObject
    if (!readIsOffButtonManual) {
        valueObject.readOnlyText = true
        ManualLayout(
            modifierPadding = modifier.padding(bottom = 380.dp, end = 58.dp),
            onClick = {
                SettingOption(context).offButtonManual(true)

                valueObject.readOnlyText = false
                NavScreen.updateScreen()
            },
            text = stringResource(R.string.button_manual)
        )
    }
    //manual of edit button
    if (!readIsOffEditButtonManual && readEditButton) {
        valueObject.readOnlyText = true
        ManualLayout(
            modifierPadding = modifier.padding(top = 300.dp, end = 50.dp),
            onClick = {
                SettingOption(context).offEditButtonManual(true)

                valueObject.readOnlyText = false
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
