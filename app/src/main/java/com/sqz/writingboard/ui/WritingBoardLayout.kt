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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import com.sqz.writingboard.KeyboardVisibilityObserver
import com.sqz.writingboard.R
import com.sqz.writingboard.ValueState
import com.sqz.writingboard.settingState
import com.sqz.writingboard.ui.component.layout.BottomStyle
import com.sqz.writingboard.ui.component.layout.HideStyle
import com.sqz.writingboard.ui.component.layout.ManualLayout
import com.sqz.writingboard.ui.theme.themeColor

@Composable
fun WritingBoardLayout(navController: NavController, modifier: Modifier = Modifier) {

    val valueState: ValueState = viewModel()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var isKeyboardVisible by remember { mutableStateOf(false) }
    var softKeyboard by remember { mutableStateOf(false) }
    var screenController by remember { mutableStateOf(false) }

    val readButtonStyle = settingState.readSegmentedButtonState("button_style", context)
    val readEditButton = settingState.readSwitchState("edit_button", context)
    val readCleanAllText = settingState.readSwitchState("clean_all_text", context)
    val readAlwaysVisibleText = settingState.readSwitchState("always_visible_text", context)

    if (valueState.editAction) {
        valueState.editButton = true
        Log.i("WritingBoardTag", "Edit button is clicked")
        valueState.editAction = false
    }
    if (valueState.doneAction && valueState.initLayout) {
        keyboardController?.hide()
        focusManager.clearFocus()
        valueState.saveAction = true
        valueState.isEditing = false
        valueState.editButton = false
        Log.i("WritingBoardTag", "Done action is triggered")
        valueState.doneAction = false
    }
    if (valueState.onClickSetting) {
        valueState.doneAction = true
        navController.navigate("Setting")
        valueState.onClickSetting = false
    }
    if (
        (LocalConfiguration.current.screenWidthDp > 600) &&
        (valueState.isEditing) &&
        (softKeyboard)
    ) {
        valueState.editingHorizontalScreen = true
    } else if (!softKeyboard) {
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
            (!softKeyboard) &&
            (readButtonStyle == 0)
        ) {
            HideStyle(context)
        }
        Box {
            //for bottom style
            val boardBottom = if (
                (valueState.editingHorizontalScreen) ||
                (readButtonStyle != 2)
            ) {
                modifier
            } else if (valueState.isEditing) {
                modifier.padding(bottom = 55.dp)
            } else {
                modifier.padding(bottom = 70.dp)
            }
            //for horizontal screen
            val horizontalScreen = if (valueState.editingHorizontalScreen) {
                modifier.padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 4.dp)
            } else {
                modifier.padding(20.dp)
            }
            //for always visible test
            if (readAlwaysVisibleText) {
                if (scrollState.value == scrollState.maxValue && !valueState.isEditing) {
                    Handler(Looper.getMainLooper()).postDelayed(50) {
                        if (scrollState.value == scrollState.maxValue) {
                            Handler(Looper.getMainLooper()).postDelayed(50) {
                                screenController = true
                            }
                        }
                    }
                } else if (scrollState.value < scrollState.maxValue - 200) {
                    screenController = false
                }
            }
            val screen = if (screenController) {
                modifier.padding(bottom = 60.dp)
            } else {
                modifier
            }
            Column(
                modifier = modifier.animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                ) then boardBottom then horizontalScreen then screen
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
                    if (valueState.cleanAllText) { //to reload texts
                        navController.navigate("UpdateScreen")
                        Handler(Looper.getMainLooper()).postDelayed(380) {
                            navController.popBackStack()
                            Log.i("WritingBoardTag", "Re-Opening WritingBoard Text")
                            valueState.cleanAllText = false
                        }
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
        }
        //edit button
        if (
            (!valueState.isEditing) &&
            (readEditButton) &&
            (!valueState.editButton) &&
            (readButtonStyle == 1)
        ) {
            val padding = if (screenController) {
                modifier.padding(end = 36.dp, top = 16.dp)
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
            softKeyboard = true
        } else {
            softKeyboard = false
            if (settingState.readSwitchState("clean_pointer_focus", context)) {
                focusManager.clearFocus()
                valueState.doneAction = true
            }
        }
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
