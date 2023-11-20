package com.sqz.writingboard.ui.component.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ManualLayout(
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