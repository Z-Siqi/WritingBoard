package com.sqz.writingboard.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import com.sqz.writingboard.KeyboardVisibilityObserver
import com.sqz.writingboard.R
import com.sqz.writingboard.ValueState
import com.sqz.writingboard.settingState
import com.sqz.writingboard.ui.component.WritingBoardManual
import com.sqz.writingboard.ui.theme.PurpleForManual
import com.sqz.writingboard.ui.theme.RedForManual
import com.sqz.writingboard.ui.theme.themeColor

@Composable
fun WritingBoardLayout(navController: NavController, modifier: Modifier = Modifier) {

    val valueState: ValueState = viewModel()
    val keyboardDone = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var isKeyboardVisible by remember { mutableStateOf(false) }
    var hideModeController by remember { mutableStateOf(false) }

    val readButtonStyle = settingState.readSegmentedButtonState("button_style", context)
    val readEditButton = settingState.readSwitchState("edit_button", context)
    val readCleanAllText = settingState.readSwitchState("clean_all_text", context)

    var editAction by remember { mutableStateOf(false) }
    if (editAction) {
        valueState.editButton = true
        Log.i("WritingBoardTag", "Edit button is clicked")
        editAction = false
    }
    var doneAction by remember { mutableStateOf(false) }
    if (doneAction && valueState.initLayout) {
        keyboardDone?.hide()
        focusManager.clearFocus()
        valueState.saveAction = true
        valueState.doneButton = false
        valueState.editButton = false
        Log.i("WritingBoardTag", "Done action is triggered")
        doneAction = false
    }
    var onClickSetting by remember { mutableStateOf(false) }
    if (onClickSetting) {
        doneAction = true
        navController.navigate("Setting")
        onClickSetting = false
    }

    //Layout
    Surface(
        modifier = modifier
            .imePadding()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    doneAction = true
                }
            },
        color = themeColor("backgroundColor")
    ) {
        if (
            (!hideModeController) &&
            (readButtonStyle == 0)
        ) {
            Column(
                verticalArrangement = Arrangement.Top
            ) {
                val area = modifier
                    .fillMaxWidth()
                    .height(80.dp)
                Spacer(
                    modifier = if (
                        (settingState.readSwitchState("off_button_manual", context))
                    ) {
                        modifier
                            .pointerInput(Unit) {
                                detectTapGestures { _ ->
                                    onClickSetting = true
                                }
                            } then area
                    } else {
                        modifier.background(color = RedForManual) then area
                    }
                )
            }
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                val area = modifier
                    .fillMaxWidth()
                    .height(120.dp)
                Spacer(
                    modifier = if (
                        (settingState.readSwitchState("off_editButton_manual", context)) &&
                        (readEditButton)
                    ) {
                        modifier
                            .pointerInput(Unit) {
                                detectTapGestures { _ ->
                                    editAction = true
                                }
                            } then area
                    } else if (
                        (!settingState.readSwitchState("off_editButton_manual", context)) &&
                        (readEditButton)
                    ) {
                        modifier.background(color = PurpleForManual) then area
                    } else {
                        modifier
                    }
                )
            }
        }
        Box {
            val boardBottom = if (readButtonStyle != 2) {
                modifier
                    .padding(20.dp)
                    .shadow(5.dp, RoundedCornerShape(26.dp))
                    .border(
                        4.dp,
                        color = themeColor("shapeColor"),
                        RoundedCornerShape(26.dp)
                    )
            } else if (valueState.doneButton) {
                modifier
                    .padding(20.dp)
                    .padding(bottom = 55.dp)
                    .shadow(5.dp, RoundedCornerShape(26.dp))
                    .border(
                        4.dp,
                        color = themeColor("shapeColor"),
                        RoundedCornerShape(26.dp)
                    )
            } else {
                modifier
                    .padding(20.dp)
                    .padding(bottom = 70.dp)
                    .shadow(5.dp, RoundedCornerShape(26.dp))
                    .border(
                        4.dp,
                        color = themeColor("shapeColor"),
                        RoundedCornerShape(26.dp)
                    )
            }
            Column(
                modifier = boardBottom,
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
                        WritingBoardText()
                    }
                    if (valueState.cleanButton) { //to reload texts
                        navController.navigate("WritingBoardNone")
                        Handler(Looper.getMainLooper()).postDelayed(380) {
                            navController.popBackStack()
                            Log.i("WritingBoardTag", "Re-Opening WritingBoard Text")
                            valueState.cleanButton = false
                        }
                    }
                }
            }
        }
        if (
            (readButtonStyle == 2)
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                val height = if (valueState.doneButton) {
                    55.dp
                } else {
                    70.dp
                }
                Surface(
                    modifier = modifier
                        .fillMaxWidth()
                        .height(height)
                        .shadow(7.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    val settingButtonLocation = if (readEditButton) {
                        Alignment.Start
                    } else {
                        Alignment.CenterHorizontally
                    }
                    if (!valueState.doneButton) {
                        val padding = if (readEditButton) {
                            modifier.padding(start = 16.dp)
                        } else {
                            modifier.padding(start = 0.dp)
                        }
                        Column(
                            modifier = modifier.fillMaxWidth(),
                            horizontalAlignment = settingButtonLocation
                        ) {
                            OutlinedButton(
                                modifier = modifier
                                    .padding(10.dp)
                                        then padding,
                                onClick = { onClickSetting = true },
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Setting"
                                )
                            }
                        }
                    }
                    if (
                        (readEditButton) &&
                        (!valueState.editButton)
                    ) {
                        Column(
                            modifier = modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.End
                        ) {
                            OutlinedButton(
                                modifier = modifier
                                    .padding(10.dp)
                                        then modifier.padding(end = 16.dp),
                                onClick = { editAction = true },
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit"
                                )
                            }
                        }
                    }
                    if (valueState.editButton || valueState.doneButton) {
                        Column(
                            modifier = modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.End
                        ) {
                            OutlinedButton(
                                modifier = modifier
                                    .padding(10.dp)
                                        then modifier.padding(end = 16.dp),
                                onClick = {
                                    valueState.buttonSaveAction = true
                                    doneAction = true
                                },
                                shape = RoundedCornerShape(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Done,
                                    contentDescription = "Done"
                                )
                            }
                        }
                    }
                    if (valueState.doneButton && readCleanAllText) {
                        Column(
                            modifier = modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            OutlinedButton(
                                modifier = modifier
                                    .padding(10.dp)
                                        then modifier.padding(end = 16.dp),
                                onClick = { valueState.cleanButton = true },
                                shape = RoundedCornerShape(5.dp)
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
        }
        //Buttons
        if (
            (valueState.doneButton) && (readButtonStyle == 1) ||
            (readButtonStyle == 0) && (valueState.editButton) ||
            (readButtonStyle == 0) && (valueState.doneButton)
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
                        valueState.cleanButton = true
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
                    valueState.buttonSaveAction = true
                    doneAction = true
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
            (!valueState.doneButton) &&
            (readEditButton) &&
            (!valueState.editButton) &&
            (readButtonStyle == 1)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(onClick = { editAction = true }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit"
                    )
                }
            }
        }
        //setting button
        if ((!valueState.doneButton) && (readButtonStyle == 1)) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                FloatingActionButton(onClick = { onClickSetting = true }) {
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
    }
    KeyboardVisibilityObserver { isVisible ->
        isKeyboardVisible = isVisible
        if (isVisible) {
            hideModeController = true
        } else {
            hideModeController = false
            if (settingState.readSwitchState("clean_pointer_focus", context)) {
                focusManager.clearFocus()
                doneAction = true
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
        WritingBoardManual(
            modifierPadding = modifier.padding(bottom = 380.dp, end = 58.dp),
            onClick = {
                settingState.writeSwitchState(
                    "off_button_manual",
                    context,
                    true
                )
                valueState.readOnlyText = false
                navController.navigate("WritingBoardNone")
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
        WritingBoardManual(
            modifierPadding = modifier.padding(top = 300.dp, end = 50.dp),
            onClick = {
                settingState.writeSwitchState(
                    "off_editButton_manual",
                    context,
                    true
                )
                valueState.readOnlyText = false
                navController.navigate("WritingBoardNone")
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
fun WritingBoardPreview() {
    val navController = rememberNavController()
    WritingBoardLayout(navController)
}
