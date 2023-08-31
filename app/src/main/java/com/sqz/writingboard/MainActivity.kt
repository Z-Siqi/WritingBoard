package com.sqz.writingboard

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.ui.theme.WritingBoardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            WritingBoardTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WritingBoardLayout()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WritingBoardLayout(modifier: Modifier = Modifier) {

    val viewModel: TextViewModel = viewModel()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("WritingBoard", Context.MODE_PRIVATE)
    LaunchedEffect(true) {
        val savedText = sharedPreferences.getString("saved_text", "")
        viewModel.textState = TextFieldValue(savedText ?: "")
    }

    val keyboardDone = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var doneButton by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    keyboardDone?.hide()
                    focusManager.clearFocus()
                    doneButton = false
                }
            },
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = modifier
                .padding(20.dp)
                .shadow(5.dp, RoundedCornerShape(26.dp))
                .border(
                    5.dp,
                    color = MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(26.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(26.dp)
            ) {
                BasicTextField(
                    value = viewModel.textState,
                    onValueChange = { newValue ->
                        viewModel.textState = newValue
                        Handler(Looper.getMainLooper()).postDelayed(1200) {
                            sharedPreferences.edit()
                                .putString(
                                    "saved_text",
                                    viewModel.textState.text
                                        .trimEnd { it.isWhitespace() })
                                .apply()
                        }
                        doneButton = true
                    },
                    singleLine = false,
                    modifier = modifier.padding(16.dp),
                    textStyle = TextStyle.Default.copy(
                        fontSize = 23.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    ),
                )
            }
        }
        if (doneButton) {
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
                    doneButton = false
                }) {
                    Image(
                        painter = painterResource(R.drawable.baseline_done_24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WritingBoardPreview() {
    WritingBoardLayout()
}