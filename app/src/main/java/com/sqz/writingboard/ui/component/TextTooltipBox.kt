package com.sqz.writingboard.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.sqz.writingboard.common.feedback.Feedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTooltipBox(
    tooltipText: String,
    enable: Boolean = true,
    feedback: Feedback? = null,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(tooltipText)
                feedback?.let {
                    LaunchedEffect(Unit) { feedback.onClickVibrate() }
                }
            }
        },
        state = rememberTooltipState(),
        enableUserInput = enable,
        content = content
    )
}
