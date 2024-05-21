package com.sqz.writingboard.ui.setting.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sqz.writingboard.ui.theme.themeColor

@Composable
fun DoubleButtonCard(
    modifier: Modifier = Modifier,
    title: String,
    buttonStartName: String,
    buttonStartAction: () -> Unit,
    buttonEndName: String,
    buttonEndAction: () -> Unit,
    colors: CardColors,
) {
    Card(
        colors = colors,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(120.dp)
    ) {
        Column(modifier.fillMaxSize()) {
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
                Row (
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = buttonStartAction,
                        modifier = modifier.width(130.dp).padding(end = 16.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Text(
                            text = buttonStartName,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = modifier.width(0.dp))
                    }
                    OutlinedButton(
                        onClick = buttonEndAction,
                        modifier = modifier.width(130.dp).padding(start = 16.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Text(
                            text = buttonEndName,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val cardColors = CardDefaults.cardColors(containerColor = themeColor("cardColor"))
    DoubleButtonCard(
        title = "Test",
        buttonStartName = "Test",
        buttonStartAction = {},
        buttonEndName = "Test",
        buttonEndAction = {},
        colors = cardColors
    )
}