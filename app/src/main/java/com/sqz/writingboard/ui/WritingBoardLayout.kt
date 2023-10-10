package com.sqz.writingboard.ui

import android.content.Context
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.KeyboardVisibilityObserver
import com.sqz.writingboard.R
import com.sqz.writingboard.ValueState
import com.sqz.writingboard.WritingBoardSettingState

val settingState = WritingBoardSettingState()
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WritingBoard")

@Composable
fun WritingBoardTextInit(context: Context) {
    when (settingState.readSegmentedButtonState("font_size", context)) {
        0 -> WritingBoardText(18.sp)
        1 -> WritingBoardText(23.sp)
        2 -> WritingBoardText(33.sp)
    }
}

@Composable
fun WritingBoardLayout(navController: NavController, modifier: Modifier = Modifier) {

    val buttonState: ValueState = viewModel()
    val keyboardDone = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var isKeyboardVisible by remember { mutableStateOf(false) }

    val backgroundColor = when (settingState.readSegmentedButtonState("theme", context)) {
        0 -> MaterialTheme.colorScheme.surfaceContainerLowest
        1 -> MaterialTheme.colorScheme.surfaceVariant
        2 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val shapeColor = when (settingState.readSegmentedButtonState("theme", context)) {
        0 -> MaterialTheme.colorScheme.primaryContainer
        1 -> MaterialTheme.colorScheme.primary
        2 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }
    val boardColor = when (settingState.readSegmentedButtonState("theme", context)) {
        0 -> MaterialTheme.colorScheme.surfaceBright
        1 -> MaterialTheme.colorScheme.surfaceContainerLow
        2 -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainerLow
    }

    //Layout
    Surface(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    keyboardDone?.hide()
                    focusManager.clearFocus()
                    buttonState.doneButton = false
                    buttonState.editButton = false
                }
            },
        color = backgroundColor
    ) {
        when (settingState.readSegmentedButtonState("button_style", context)) {
            0 -> {
                Spacer(
                    modifier = if (settingState.readSwitchState("off_button_manual", context)) {
                        modifier
                            .pointerInput(Unit) {
                                detectTapGestures { _ ->
                                    buttonState.saveAction = true
                                    navController.navigate("Setting")
                                }
                            }
                    } else {
                        modifier.background(color = MaterialTheme.colorScheme.error)
                    }
                )
                Spacer(
                    modifier = modifier
                        .padding(top = 100.dp)
                        .background(color = backgroundColor)
                        .pointerInput(Unit) {
                            detectTapGestures { _ ->
                                keyboardDone?.hide()
                                focusManager.clearFocus()
                                buttonState.doneButton = false
                                buttonState.editButton = false
                            }
                        }
                )
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
                WritingBoardTextInit(context)
                Handler(Looper.getMainLooper()).postDelayed(550) {
                    buttonState.openLayout = true
                    Log.i("WritingBoardTag", "Initializing WritingBoard Text")
                }
                if (buttonState.cleanButton) {
                    navController.navigate("WritingBoardNone")
                    Handler(Looper.getMainLooper()).postDelayed(380) {
                        navController.popBackStack()
                        Log.i("WritingBoardTag", "Re-Opening WritingBoard Text")
                        buttonState.cleanButton = false
                    }
                }
            }
        }
        //Buttons
        if (buttonState.doneButton) {
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
                        buttonState.cleanButton = true
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
                    keyboardDone?.hide()
                    focusManager.clearFocus()
                    buttonState.saveAction = true
                    buttonState.doneButton = false
                    buttonState.editButton = false
                    Log.i("WritingBoardTag", "Done button is clicked")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done"
                    )
                }
            }
        }
        //clean all text button
        if (settingState.readSwitchState(
                "clean_all_text",
                context
            ) && !buttonState.doneButton && !settingState.readSwitchState("edit_button", context)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(onClick = {
                    buttonState.cleanButton = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Clean all texts"
                    )
                }
            }
        }
        //edit button
        if (!buttonState.doneButton && settingState.readSwitchState(
                "edit_button",
                context
            ) && !buttonState.editButton
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(onClick = {
                    buttonState.requestFocus.requestFocus()
                    buttonState.editButton = true
                    buttonState.editScroll = true
                    Log.i("WritingBoardTag", "Edit button is clicked")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit"
                    )
                }
            }
        }
        //setting button
        if (!buttonState.doneButton && settingState.readSegmentedButtonState("button_style", context) != 0) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                FloatingActionButton(onClick = {
                    buttonState.saveAction = true
                    navController.navigate("Setting")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Setting"
                    )
                }
            }
        }
        //manual of button style
        if (!settingState.readSwitchState(
                "off_button_manual",
                context
            ) && settingState.readSegmentedButtonState("button_style", context) == 0
        )
            Column(
                modifier = modifier.padding(bottom = 350.dp, end = 50.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Card(
                    modifier = modifier
                        .size(200.dp, 100.dp)
                        .shadow(5.dp, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        modifier = modifier.padding(8.dp),
                        text = stringResource(R.string.button_manual),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Button(
                        modifier = modifier.padding(start = 100.dp),
                        onClick = {
                            settingState.writeSwitchState(
                                "off_button_manual",
                                context,
                                true
                            )
                            navController.navigate("WritingBoardNone")
                            Handler(Looper.getMainLooper()).postDelayed(50) {
                                navController.popBackStack()
                                buttonState.updateScreen = false
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Done, contentDescription = "Okay")
                    }
                }
            }

        KeyboardVisibilityObserver { isVisible ->
            isKeyboardVisible = isVisible
            if (isVisible) {
                buttonState.doneButton = true
                buttonState.editScroll = false
            } else {
                buttonState.doneButton = false
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
