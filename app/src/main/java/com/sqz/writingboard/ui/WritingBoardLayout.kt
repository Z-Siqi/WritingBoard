package com.sqz.writingboard.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.sqz.writingboard.ui.theme.PurpleForManual
import com.sqz.writingboard.ui.theme.RedForManual

@Composable
fun WritingBoardLayout(navController: NavController, modifier: Modifier = Modifier) {

    val valueState: ValueState = viewModel()
    val keyboardDone = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var isKeyboardVisible by remember { mutableStateOf(false) }
    var hideModeController by remember { mutableStateOf(false) }

    val readTheme = settingState.readSegmentedButtonState("theme", context)
    val readButtonStyle = settingState.readSegmentedButtonState("button_style", context)
    val readEditButton = settingState.readSwitchState("edit_button", context)

    val backgroundColor = when (readTheme) {
        0 -> MaterialTheme.colorScheme.surfaceContainerLowest
        1 -> MaterialTheme.colorScheme.surfaceVariant
        2 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val shapeColor = when (readTheme) {
        0 -> MaterialTheme.colorScheme.primaryContainer
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }
    val boardColor = when (readTheme) {
        0 -> MaterialTheme.colorScheme.surfaceBright
        1 -> MaterialTheme.colorScheme.surfaceContainerLow
        2 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainerLow
    }

    var editAction by remember { mutableStateOf(false) }
    if (editAction) {
        valueState.editButton = true
        valueState.editScroll = 1
        Log.i("WritingBoardTag", "Edit button is clicked")
        editAction = false
    }
    var doneAction by remember { mutableStateOf(false) }
    if (doneAction) {
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
        if (valueState.initScroll > 2) { //to fix an error with open setting
            valueState.editScroll = 0
        } else {
            valueState.editScroll = 1
        }
        onClickSetting = false
    }

    //Layout
    Surface(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    doneAction = true
                }
            },
        color = backgroundColor
    ) {
        when (settingState.readSegmentedButtonState("button_style", context)) {
            0 -> {
                if (!hideModeController) {
                    Column(
                        verticalArrangement = Arrangement.Top
                    ) {
                        val area = modifier
                            .fillMaxWidth()
                            .height(80.dp)
                        Spacer(
                            modifier = if (settingState.readSwitchState(
                                    "off_button_manual",
                                    context
                                )
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
                                (readButtonStyle == 0) &&
                                (readEditButton)
                            ) {
                                modifier.background(color = PurpleForManual) then area
                            } else {
                                modifier
                            }
                        )
                    }
                }
            }
        }
        Column(
            modifier = modifier
                .padding(20.dp)
                .shadow(5.dp, RoundedCornerShape(26.dp))
                .border(
                    4.dp,
                    color = shapeColor,
                    RoundedCornerShape(26.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = boardColor,
                modifier = modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(26.dp)
            ) {
                WritingBoardText()
                if (!valueState.openLayout) { //to fix error with first open edit
                    Handler(Looper.getMainLooper()).postDelayed(550) {
                        valueState.openLayout = true
                        Log.i("WritingBoardTag", "Initializing WritingBoard Text")
                    }
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
        //Buttons
        if (valueState.doneButton) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                //clean all text button
                if (settingState.readSwitchState("clean_all_text", context)) {
                    FloatingActionButton(onClick = {
                        valueState.cleanButton = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
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
        //clean all text button
        if ((settingState.readSwitchState("clean_all_text", context)) &&
            (!valueState.doneButton) &&
            (!readEditButton)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(onClick = {
                    valueState.cleanButton = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clean all texts"
                    )
                }
            }
        }
        //edit button
        if (
            (!valueState.doneButton) &&
            (readEditButton) &&
            (!valueState.editButton) &&
            (readButtonStyle != 0)
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
        if ((!valueState.doneButton) && (readButtonStyle != 0)) {
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
        if (
            (!settingState.readSwitchState("off_button_manual", context)) &&
            (readButtonStyle == 0)
        ) {
            WritingBoardManual(
                modifierPadding = modifier.padding(bottom = 380.dp, end = 58.dp),
                onClick = {
                    settingState.writeSwitchState(
                        "off_button_manual",
                        context,
                        true
                    )
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
            (readButtonStyle == 0) &&
            (readEditButton)
        ) {
            WritingBoardManual(
                modifierPadding = modifier.padding(top = 300.dp, end = 50.dp),
                onClick = {
                    settingState.writeSwitchState(
                        "off_editButton_manual",
                        context,
                        true
                    )
                    navController.navigate("WritingBoardNone")
                    Handler(Looper.getMainLooper()).postDelayed(50) {
                        navController.popBackStack()
                        valueState.updateScreen = false
                    }
                },
                text = stringResource(R.string.edit_button_manual)
            )
        }
        if (valueState.ee) {
            valueState.editScroll = 0
            navController.navigate("EE")
            Handler(Looper.getMainLooper()).postDelayed(80000) {
                navController.popBackStack()
            }
            valueState.ee = false
        }

        KeyboardVisibilityObserver { isVisible ->
            isKeyboardVisible = isVisible
            if (isVisible) {
                valueState.doneButton = true
                hideModeController = true
            } else {
                doneAction = true
                hideModeController = false
                if (settingState.readSwitchState("clean_pointer_focus", context)) {
                    focusManager.clearFocus()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WritingBoardPreview() {
    val navController = rememberNavController()
    WritingBoardLayout(navController)
}
