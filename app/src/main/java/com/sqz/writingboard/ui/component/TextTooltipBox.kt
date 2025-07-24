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
import com.sqz.writingboard.common.feedback.AndroidFeedback
import com.sqz.writingboard.preference.SettingOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTooltipBox(
    tooltipText: String,
    enable: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val settings = SettingOption(view.context)
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(tooltipText)
                LaunchedEffect(true) {
                    AndroidFeedback(settings, view).onClickEffect()
                }
            }
        },
        state = rememberTooltipState(),
        enableUserInput = enable
    ) {
        content()
    }
}
