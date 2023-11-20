package com.sqz.writingboard.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.theme.Blue
import com.sqz.writingboard.ui.theme.Pink
import com.sqz.writingboard.ui.theme.PinkText
import com.sqz.writingboard.ui.theme.Purple
import com.sqz.writingboard.ui.theme.White

@Composable
fun WritingBoardNone(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant
            )
    ) { Log.i("WritingBoardTag", "NoneScreen is open") }
}

@Composable
fun WritingBoardEE(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = modifier
                .padding(20.dp)
                .shadow(5.dp, RoundedCornerShape(26.dp))
                .border(
                    4.dp,
                    color = MaterialTheme.colorScheme.tertiary,
                    RoundedCornerShape(26.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Column(modifier = modifier.fillMaxSize()) {
                    val blueColor = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(color = Blue)
                    val pinkColor = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(color = Pink)
                    val whiteColor = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(color = White)
                    Spacer(blueColor)
                    Spacer(pinkColor)
                    Spacer(whiteColor)
                    Spacer(pinkColor)
                    Spacer(blueColor)
                }
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    val itemPink = Modifier
                        .weight(1f)
                        .padding(top = 15.dp)
                    val itemBlue = Modifier
                        .weight(1f)
                        .padding(top = 30.dp)
                        .verticalScroll(rememberScrollState())
                    Text(
                        text = stringResource(R.string.easter_eggs),
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        modifier = itemPink,
                        color = PinkText
                    )
                    Text(
                        text = stringResource(R.string.may_all_people_equal),
                        fontSize = 19.sp,
                        fontFamily = FontFamily.Cursive,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = itemBlue,
                        color = Purple
                    )
                    Text(
                        text = stringResource(R.string.to_the_special_you),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        modifier = modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        color = PinkText
                    )
                    Text(
                        text = stringResource(R.string.may_everyone),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Cursive,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        modifier = itemBlue,
                        color = Purple
                    )
                    Text(
                        text = stringResource(R.string.see_easter_egg_again),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        modifier = itemPink,
                        color = PinkText
                    )
                }
            }
        }
    }
}

@Composable
fun ErrorWithSystemVersionA13(navController: NavController, modifier: Modifier = Modifier) {
    Log.i("WritingBoardTag", "ErrorWithSystemVersion has open")
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.errorContainer
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenText(text = stringResource(R.string.lower_than_android13))
        Spacer(modifier = modifier.height(10.dp))
        ScreenText(text = stringResource(R.string.feature_supports_android13))
        Spacer(modifier = modifier.height(18.dp))
        Button(
            onClick = {
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary)
        ) {
            Text(stringResource(R.string.back))
        }
    }
}

@Composable
private fun ScreenText(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onErrorContainer,
        textAlign = TextAlign.Center
    )
}
