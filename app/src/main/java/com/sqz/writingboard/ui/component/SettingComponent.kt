package com.sqz.writingboard.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtonCardLayout(
    modifier: Modifier = Modifier,
    title: String,
    options: List<Int>,
    selectedOption: Int,
    colors: CardColors,
    onOptionSelected: (Int) -> Unit
) {
    Card(
        colors = colors,
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
    colors: CardColors,
    modifier: Modifier = Modifier
) {
    Card(
        colors = colors,
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
    colors: CardColors,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = colors,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(85.dp)
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