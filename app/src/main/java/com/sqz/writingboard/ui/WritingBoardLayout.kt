package com.sqz.writingboard.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
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
import com.sqz.writingboard.ButtonState
import com.sqz.writingboard.R
import com.sqz.writingboard.WritingBoard
import com.sqz.writingboard.WritingBoardSettingState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

val settingState = WritingBoardSettingState()
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WritingBoard")

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WritingBoardLayout(navController: NavController, modifier: Modifier = Modifier) {

    val doneButtonState: ButtonState = viewModel()
    val keyboardDone = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    keyboardDone?.hide()
                    focusManager.clearFocus()
                    doneButtonState.doneButton = false
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
                WritingBoardText()
            }
        }
        if (doneButtonState.doneButton) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(36.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(onClick = {
                    keyboardDone?.hide()
                    focusManager.clearFocus()
                    doneButtonState.doneButton = false
                }) {
                    Image(
                        painter = painterResource(R.drawable.baseline_done_24),
                        contentDescription = null
                    )
                }
            }
        }
        if (!doneButtonState.doneButton) {
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
                    Text("Setting")
                }
            }
        }
    }
}

@Composable
fun WritingBoardText(modifier: Modifier = Modifier) {

    val doneButtonState: ButtonState = viewModel()
    val viewModel: WritingBoard = viewModel()
    val dataStore = LocalContext.current.dataStore
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(true) {
        val savedText = dataStore.data.first()[stringPreferencesKey("saved_text")] ?: ""
        viewModel.textState = TextFieldValue(savedText)
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
                        newText.trimEnd { it == '\n' }.plus('\n')
                    } else {
                        newText
                    }
                    coroutineScope.launch {
                        dataStore.edit { preferences ->
                            preferences[stringPreferencesKey("saved_text")] = textToSave
                        }
                    }
                }
            }
            doneButtonState.doneButton = true
        },
        singleLine = false,
        modifier = modifier.padding(16.dp),
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