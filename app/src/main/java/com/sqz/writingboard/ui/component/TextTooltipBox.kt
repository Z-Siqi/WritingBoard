package com.sqz.writingboard.ui.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import com.sqz.writingboard.component.Feedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTooltipBox(
    tooltipText: String,
    content: @Composable () -> Unit
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(tooltipText)
                val view = LocalView.current
                LaunchedEffect(true) {
                    Feedback(view).createOneTick()
                }
            }
        },
        state = rememberTooltipState()
    ) {
        content()
    }
}
