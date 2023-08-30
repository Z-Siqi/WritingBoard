package com.sqz.writingboard

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WritingBoardTheme{
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WritingBoardLayout()
                }
            }
        }
    }
}

class TextViewModel : ViewModel() {
    var textState by mutableStateOf(TextFieldValue())
}

@Composable
fun WritingBoardLayout(modifier: Modifier = Modifier) {

    val viewModel: TextViewModel = viewModel()

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("WritingBoard", Context.MODE_PRIVATE)

    LaunchedEffect(true) {
        val savedText = sharedPreferences.getString("saved_text", "")
        viewModel.textState = TextFieldValue(savedText ?: "")
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Column(
            modifier = modifier
                .padding(25.dp)
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
                modifier = modifier.fillMaxSize(),
                shape = RoundedCornerShape(26.dp)
            ) {
                BasicTextField(
                    value = viewModel.textState,
                    onValueChange = { newValue ->
                        viewModel.textState = newValue
                        Timer().schedule(1000L){
                            sharedPreferences.edit()
                                .putString("saved_text", viewModel.textState.text)
                                .apply()
                        }
                    },
                    modifier = modifier.padding(16.dp),
                    textStyle = TextStyle.Default.copy(
                        fontSize = 23.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WritingBoardPreview() {
    WritingBoardLayout()
}