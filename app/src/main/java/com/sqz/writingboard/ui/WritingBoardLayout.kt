package com.sqz.writingboard.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.core.os.postDelayed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.glance.appwidget.updateAll
import com.sqz.writingboard.component.KeyboardVisibilityObserver
import com.sqz.writingboard.R
import com.sqz.writingboard.classes.ValueState
import com.sqz.writingboard.component.Vibrate
import com.sqz.writingboard.glance.WritingBoardTextOnlyWidget
import com.sqz.writingboard.glance.WritingBoardWidget
import com.sqz.writingboard.settingState
import com.sqz.writingboard.ui.component.layout.BottomStyle
import com.sqz.writingboard.ui.component.layout.HideStyle
import com.sqz.writingboard.ui.component.layout.ManualLayout
import com.sqz.writingboard.ui.theme.themeColor
import kotlinx.coroutines.delay

@Composable
fun WritingBoardLayout(navController: NavController, modifier: Modifier = Modifier) {

    val valueState: ValueState = viewModel()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isKeyboardVisible by remember { mutableStateOf(false) }
    var screenController by remember { mutableStateOf(false) }

    val readButtonStyle = settingState.readSegmentedButtonState("button_style", context)
    val readEditButton = settingState.readSwitchState("edit_button", context)
    val readCleanAllText = settingState.readSwitchState("clean_all_text", context)
    val readAlwaysVisibleText = settingState.readSwitchState("always_visible_text", context)
    val readVibrateSettings = settingState.readSegmentedButtonState("vibrate_settings", context)

    if (valueState.editAction) {
        valueState.editButton = true
        if (readVibrateSettings != 0) Vibrate()
        Log.d("WritingBoardTag", "Edit button is clicked")
        valueState.editAction = false
    }
    if (valueState.doneAction && valueState.initLayout) {
        keyboardController?.hide()
        focusManager.clearFocus()
        valueState.saveAction = true
        valueState.isEditing = false
        valueState.editButton = false
        if (readVibrateSettings == 2) Vibrate()
        Log.d("WritingBoardTag", "Done action is triggered")
        valueState.doneAction = false
    }
    if (valueState.onClickSetting) {
        valueState.doneAction = true
        if (readVibrateSettings == 2) Vibrate()
        navController.navigate("Setting")
        valueState.onClickSetting = false
    }
    if (
        (LocalConfiguration.current.screenWidthDp > 600) &&
        (valueState.isEditing) &&
        (valueState.softKeyboard)
    ) {
        valueState.editingHorizontalScreen = true
        Log.d("WritingBoardTag", "editingHorizontalScreen is true")
    } else if (!valueState.softKeyboard) {
        valueState.editingHorizontalScreen = false
    }

    //Layout
    Surface(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    valueState.doneAction = true
                }
            },
        color = themeColor("backgroundColor")
    ) {
        if (
            (!valueState.softKeyboard) &&
            (readButtonStyle == 0)
        ) {
            HideStyle(context)
        }
        Box {
            //for bottom style
            val boardBottom = if (
                (!valueState.editingHorizontalScreen) &&
                (readButtonStyle == 1) &&
                (!valueState.isEditing) &&
                (screenController)
            ) {
                modifier.padding(bottom = 60.dp)
            } else if (
                (valueState.isEditing) &&
                (readButtonStyle == 2) &&
                (!valueState.editingHorizontalScreen)
            ) {
                modifier.padding(bottom = 55.dp)
            } else if (
                (valueState.isEditing) &&
                (screenController) &&
                (!valueState.editingHorizontalScreen)
            ) {
                modifier.padding(bottom = 45.dp)
            } else if (
                (readButtonStyle == 2) &&
                (!valueState.editingHorizontalScreen)
            ) {
                modifier.padding(bottom = 70.dp)
            } else {
                modifier
            }
            //for horizontal screen
            val horizontalScreen = if (valueState.editingHorizontalScreen) {
                modifier.padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 4.dp)
            } else {
                modifier.padding(20.dp)
            }
            //for calculate always visible text
            val highValue = (77 * LocalDensity.current.density).toInt()
            if (screenController && readEditButton && !valueState.isEditing) {
                LaunchedEffect(true) {
                    valueState.readOnlyTextScroll = false
                    delay(100)
                    scrollState.scrollTo(scrollState.maxValue)
                    valueState.readOnlyTextScroll = true
                }
            }
            if (readAlwaysVisibleText && readButtonStyle != 2) {
                if (
                    (scrollState.value == scrollState.maxValue) &&
                    (scrollState.canScrollBackward)
                ) {
                    LaunchedEffect(true) {
                        delay(50)
                        if (scrollState.value == scrollState.maxValue) {
                            delay(50)
                            screenController = true
                            Log.i("WritingBoardTag", "screenController is true")
                        }
                    }
                } else if (scrollState.value < scrollState.maxValue - highValue) {
                    screenController = false
                }
            }
            //writing board
            Column(
                modifier = modifier.animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) then boardBottom then horizontalScreen
                    .shadow(5.dp, RoundedCornerShape(26.dp))
                    .border(
                        4.dp,
                        color = themeColor("shapeColor"),
                        RoundedCornerShape(26.dp)
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = themeColor("boardColor"),
                    modifier = modifier.fillMaxSize(),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        WritingBoardText(scrollState)
                    }
                }
            }
        }
        if (
            (readButtonStyle == 2)
        ) {
            BottomStyle(context)
        }
        //Buttons
        if (
            (valueState.isEditing) && (readButtonStyle == 1) ||
            (readButtonStyle == 0) && (valueState.editButton) ||
            (readButtonStyle == 0) && (valueState.isEditing) ||
            (readButtonStyle == 2) &&
            (valueState.editingHorizontalScreen) && (valueState.isEditing)
        ) {
            if (!readAlwaysVisibleText) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(36.dp),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    //clean all text button
                    if (readCleanAllText) {
                        FloatingActionButton(onClick = {
                            valueState.cleanAllText = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Clean all texts"
                            )
                        }
                    }
                    //done button
                    Spacer(modifier = modifier.height(10.dp))
                    FloatingActionButton(onClick = {
                        valueState.matchText = true
                        valueState.doneAction = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done"
                        )
                    }
                }
            } else {
                val bottom = if (screenController) {
                    2.dp
                } else {
                    25.dp
                }
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    ExtendedFloatingActionButton(
                        modifier = modifier
                            .padding(10.dp)
                            .padding(end = 26.dp, bottom = bottom)
                            .size(80.dp, 45.dp),
                        onClick = {
                            valueState.matchText = true
                            valueState.doneAction = true
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done"
                        )
                    }
                }
                if (readCleanAllText) {
                    Column(
                        modifier = modifier
                            .fillMaxWidth()
                            .height(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        FloatingActionButton(
                            modifier = modifier
                                .padding(10.dp)
                                .padding(start = 50.dp, bottom = bottom)
                                .size(45.dp, 45.dp),
                            onClick = { valueState.cleanAllText = true },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Clean all texts"
                            )
                        }
                    }
                }
            }
        }
        //edit button
        if (
            (!valueState.isEditing) &&
            (readEditButton) &&
            (!valueState.editButton) &&
            (readButtonStyle == 1)
        ) {
            val padding = if (screenController) {
                modifier.padding(end = 36.dp, bottom = 16.dp)
            } else {
                modifier.padding(36.dp)
            }
            Column(
                modifier = modifier.fillMaxSize() then padding,
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(onClick = { valueState.editAction = true }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit"
                    )
                }
            }
        }
        //setting button
        if ((!valueState.isEditing) && (readButtonStyle == 1)) {
            val padding = if (screenController) {
                modifier.padding(start = 36.dp, bottom = 16.dp)
            } else {
                modifier.padding(36.dp)
            }
            Column(
                modifier = modifier.fillMaxSize() then padding,
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                FloatingActionButton(onClick = { valueState.onClickSetting = true }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Setting"
                    )
                }
            }
        }
        //manual of button style
        if (readButtonStyle == 0) {
            Manual(navController)
        }
        if (valueState.ee) {
            navController.navigate("EE")
            Handler(Looper.getMainLooper()).postDelayed(80000) {
                navController.popBackStack()
            }
            valueState.ee = false
        }
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            val bottomShadow = if (readButtonStyle != 2) {
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
            valueState.softKeyboard = true
            Log.d("WritingBoardTag", "Keyboard is visible")
        } else {
            valueState.softKeyboard = false
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
private fun Manual(navController: NavController, modifier: Modifier = Modifier) {
    val valueState: ValueState = viewModel()
    val context = LocalContext.current
    val readEditButton = settingState.readSwitchState("edit_button", context)
    if (
        (!settingState.readSwitchState("off_button_manual", context))
    ) {
        valueState.readOnlyText = true
        ManualLayout(
            modifierPadding = modifier.padding(bottom = 380.dp, end = 58.dp),
            onClick = {
                settingState.writeSwitchState(
                    "off_button_manual",
                    context,
                    true
                )
                valueState.readOnlyText = false
                navController.navigate("UpdateScreen")
                Handler(Looper.getMainLooper()).postDelayed(50) {
                    navController.popBackStack()
                    valueState.updateScreen = false
                }
            },
            text = stringResource(R.string.button_manual)
        )
    }
    //manual of edit button
    if (
        (!settingState.readSwitchState("off_editButton_manual", context)) &&
        (readEditButton)
    ) {
        valueState.readOnlyText = true
        ManualLayout(
            modifierPadding = modifier.padding(top = 300.dp, end = 50.dp),
            onClick = {
                settingState.writeSwitchState(
                    "off_editButton_manual",
                    context,
                    true
                )
                valueState.readOnlyText = false
                navController.navigate("UpdateScreen")
                Handler(Looper.getMainLooper()).postDelayed(50) {
                    navController.popBackStack()
                    valueState.updateScreen = false
                }
            },
            text = stringResource(R.string.edit_button_manual)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WritingBoardPreview() {
    val navController = rememberNavController()
    WritingBoardLayout(navController)
}
