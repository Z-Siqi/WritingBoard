package com.sqz.writingboard.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.postDelayed
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.emptyPreferences
import com.sqz.writingboard.ButtonState
import com.sqz.writingboard.KeyboardVisibilityObserver
import com.sqz.writingboard.WritingBoard
import com.sqz.writingboard.WritingBoardSettingState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException

val settingState = WritingBoardSettingState()
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WritingBoard")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WritingBoardLayout(navController: NavController, modifier: Modifier = Modifier) {

    val buttonState: ButtonState = viewModel()
    val keyboardDone = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var openLayout by remember { mutableStateOf(false) }
    var isKeyboardVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    keyboardDone?.hide()
                    focusManager.clearFocus()
                    buttonState.doneButton = false
                }
            },
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = modifier
                .padding(20.dp)
                .shadow(5.dp, RoundedCornerShape(26.dp))
                .border(
                    4.dp,
                    color = MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(26.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(26.dp)
            ) {
                if (openLayout) {
                    WritingBoardText()
                    Log.i("WritingBoardTag", "Pre-Opening WritingBoard Text")
                } else {
                    WritingBoardText()
                }
                Handler(Looper.getMainLooper()).postDelayed(550) {
                    openLayout = true
                    Log.i("WritingBoardTag", "Opening WritingBoard Text")
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
        if (buttonState.doneButton) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
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
                Spacer(modifier = modifier.height(10.dp))
                FloatingActionButton(onClick = {
                    keyboardDone?.hide()
                    focusManager.clearFocus()
                    buttonState.doneButton = false
                }) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done"
                    )
                }
            }
        }
        if (settingState.readSwitchState("clean_all_text", context) && !buttonState.doneButton) {
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
        if (!buttonState.doneButton) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                FloatingActionButton(onClick = {
                    navController.navigate("Setting")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Setting"
                    )
                }
            }
        }

        KeyboardVisibilityObserver { isVisible ->
            isKeyboardVisible = isVisible
            if (isVisible) {
                buttonState.doneButton = true
            }
        }
    }
}

@Composable
fun WritingBoardText(modifier: Modifier = Modifier) {

    val buttonState: ButtonState = viewModel()
    val viewModel: WritingBoard = viewModel()
    val dataStore = LocalContext.current.dataStore
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(true) {
        val savedText: String = context.dataStore.data
            .catch {
                if (it is IOException) {
                    Log.e(TAG, "Error reading preferences.", it)
                    emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preferences ->
                preferences[stringPreferencesKey("saved_text")] ?: ""
            }.first()
        viewModel.textState = TextFieldValue(savedText)
    }

    if (buttonState.cleanButton) {
        viewModel.textState.text.let { newText ->
            coroutineScope.launch {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("saved_text")] = newText.drop(Int.MAX_VALUE)
                }
                Log.i("WritingBoardTag", "Save writing board texts")
            }
        }
    }
    BasicTextField(
        value = viewModel.textState.text,
        onValueChange = { newText ->
            viewModel.textState = TextFieldValue(newText)
            Handler(Looper.getMainLooper()).postDelayed(1200) {
                viewModel.textState.text.let {
                    val textToSave = if (!settingState.readSwitchState(
                            "allow_multiple_lines",
                            context
                        ) && newText.isNotEmpty() && newText.last() == '\n'
                    ) {
                        Log.i("WritingBoardTag", "Removing line breaks and adding a new line.")
                        newText.trimEnd { it == '\n' }.plus('\n')
                    } else {
                        newText
                    }
                    coroutineScope.launch {
                        dataStore.edit { preferences ->
                            preferences[stringPreferencesKey("saved_text")] = textToSave
                        }
                        Log.i("WritingBoardTag", "Save writing board texts")
                    }
                }
            }
            buttonState.doneButton = true
        },
        singleLine = false,
        modifier = modifier
            .padding(16.dp),
        textStyle = TextStyle.Default.copy(
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary
        )
    )
}

@Preview(showBackground = true)
@Composable
fun WritingBoardPreview() {
    val navController = rememberNavController()
    WritingBoardLayout(navController)
}