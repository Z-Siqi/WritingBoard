package com.sqz.writingboard.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.theme.Blue
import com.sqz.writingboard.ui.theme.Pink
import com.sqz.writingboard.ui.theme.Pink40
import com.sqz.writingboard.ui.theme.Purple40
import com.sqz.writingboard.ui.theme.White

@Composable
fun WritingBoardNone(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant
            )
    ) { Log.i("WritingBoardTag", "NoneScreen has open") }
}

@Composable
fun WritingBoardManual(
    @SuppressLint("ModifierParameter") modifierPadding: Modifier,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifierPadding,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        Card(
            modifier = modifier
                .size(200.dp, 100.dp)
                .shadow(5.dp, RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                Text(
                    modifier = modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                    text = text,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Button(
                    modifier = modifier
                        .padding(8.dp)
                        .align(Alignment.End),
                    onClick = onClick
                ) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = "Okay")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtonCardLayout(
    modifier: Modifier = Modifier,
    title: String,
    options: List<Int>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(120.dp)
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier
                    .padding(16.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.secondary
            )
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .wrapContentHeight(Alignment.Bottom)
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = modifier
                        .height(43.dp)
                        .fillMaxWidth()
                ) {
                    options.forEachIndexed { index, label ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            onClick = {
                                onOptionSelected(index)
                            },
                            selected = index == selectedOption,
                        ) {
                            Text(
                                text = stringResource(label),
                                lineHeight = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardLayout(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(80.dp)
    ) {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = modifier
                    .padding(10.dp)
                    .size(width = 280.dp, height = 60.dp)
            )
            Switch(
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentWidth(Alignment.End)
                    .padding(16.dp),
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
fun ClickCardLayout(
    intent: () -> Unit,
    text: String,
    painter: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(80.dp)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clickable(onClick = intent)
        ) {
            Text(
                text = text,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = modifier
                    .padding(16.dp)
                    .wrapContentHeight(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.secondary
            )
            Icon(
                painter = painterResource(painter),
                contentDescription = contentDescription,
                modifier = modifier
                    .fillMaxSize()
                    .wrapContentWidth(Alignment.End)
                    .padding(end = 27.dp),
            )
        }
    }
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
                        color = Pink40
                    )
                    Text(
                        text = stringResource(R.string.may_all_people_equal),
                        fontSize = 19.sp,
                        fontFamily = FontFamily.Cursive,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = itemBlue,
                        color = Purple40
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
                        color = Pink40
                    )
                    Text(
                        text = stringResource(R.string.may_everyone),
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Cursive,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        modifier = itemBlue,
                        color = Purple40
                    )
                    Text(
                        text = stringResource(R.string.see_easter_egg_again),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        modifier = itemPink,
                        color = Pink40
                    )
                }
            }
        }
    }
}
