package com.sqz.writingboard.ui.main

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.glance.appwidget.updateAll
import com.sqz.writingboard.NavScreen
import com.sqz.writingboard.component.KeyboardVisibilityObserver
import com.sqz.writingboard.R
import com.sqz.writingboard.component.Vibrate
import com.sqz.writingboard.glance.WritingBoardTextOnlyWidget
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.ui.WritingBoardViewModel
import com.sqz.writingboard.ui.main.control.NavBarStyle
import com.sqz.writingboard.ui.main.control.HideStyle
import com.sqz.writingboard.ui.component.ManualLayout
import com.sqz.writingboard.ui.main.control.LayoutButton
import com.sqz.writingboard.ui.main.text.WritingBoardText
import com.sqz.writingboard.ui.setting.SettingOption
import com.sqz.writingboard.ui.theme.ThemeColor
import com.sqz.writingboard.ui.theme.themeColor
import kotlinx.coroutines.delay

@Composable
fun WritingBoardLayout(
    navToSetting: () -> Unit,
    context: Context,
    modifier: Modifier = Modifier,
    viewModel: WritingBoardViewModel = viewModel(),
) {
    val config = LocalConfiguration.current
    val scrollState = rememberScrollState()
    var isKeyboardVisible by remember { mutableStateOf(false) }
    var screenController by remember { mutableStateOf(false) }

    val set = SettingOption(context = context)
    val valueObject = WritingBoardObject
    valueObject.doneRequest(
        softwareKeyboardController = LocalSoftwareKeyboardController.current,
        focusManager = LocalFocusManager.current,
        settingOption = set,
        context = context
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
        if (!valueObject.softKeyboard && set.buttonStyle() == 0) HideStyle(
            onClickSetting = { valueObject.onClickSetting(navToSetting) },
            onClickEdit = { valueObject.editAction(set, Vibrate(context)) },
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
            //temporary solve scroll interrupted (by over scroll) when size change with alwaysVisibleText
            if (screenController && set.editButton() && !valueObject.isEditing) {
                LaunchedEffect(true) {
                    valueObject.readOnlyTextScroll = false
                    delay(100)
                    scrollState.scrollTo(scrollState.maxValue)
                    valueObject.readOnlyTextScroll = true
                }
            }
            //alwaysVisibleText actions
            if (set.alwaysVisibleText() && set.buttonStyle() != 2) {
                var onEditing by remember { mutableStateOf(false) }
                if (
                    (scrollState.value == scrollState.maxValue) &&
                    (scrollState.canScrollBackward)
                ) {
                    LaunchedEffect(true) {
                        delay(50)
                        if (scrollState.value == scrollState.maxValue && !screenController) {
                            delay(50)
                            screenController = true
                            Log.i("WritingBoardTag", "screenController is true")
                        }
                    }
                } else if (scrollState.value < scrollState.maxValue - highValue) {
                    if (valueObject.isEditing && !onEditing) LaunchedEffect(true) {
                        //solve size change will cover text when edit bottom text
                        screenController = false
                        onEditing = true
                    } else screenController = false
                }
                if (!valueObject.isEditing) onEditing = false
                //solve size change will cover text when edit bottom text
                if (valueObject.softKeyboard && screenController && onEditing) LaunchedEffect(true) {
                    delay(200)
                    scrollState.scrollTo(scrollState.maxValue)
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
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        val high = if (set.buttonStyle() == 2) 110 else 58
                        WritingBoardText(
                            scrollState = scrollState,
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
                            isEditing = valueObject.isEditing, readOnly = valueObject.readOnlyText,
                            editButton = valueObject.editButton,
                            requestSave = { valueObject.saveAction = true },
                            bottomHigh = high + if (screenController) 70 else 0
                        )
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
            onClickEdit = { valueObject.editAction(set, Vibrate(context)) },
            readAlwaysVisibleText = set.alwaysVisibleText()
        )

        // NavBar control style
        if (set.buttonStyle() == 2) NavBarStyle(
            isEditing = valueObject.isEditing,
            onClickSetting = { valueObject.onClickSetting(navToSetting) },
            onClickEdit = { valueObject.editAction(set, Vibrate(context)) },
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