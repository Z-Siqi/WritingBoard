package com.sqz.writingboard.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun WritingBoardNone(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant
            )
    ) { Log.i("WritingBoardTag", "NoneScreen has open") }
}

@Preview(showBackground = true)
@Composable
fun WritingBoardScreenPreview() {
    WritingBoardNone()
}