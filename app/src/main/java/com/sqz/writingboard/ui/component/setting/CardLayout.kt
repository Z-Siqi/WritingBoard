package com.sqz.writingboard.ui.component.setting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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