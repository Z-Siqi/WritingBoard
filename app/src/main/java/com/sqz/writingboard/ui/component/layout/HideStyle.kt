package com.sqz.writingboard.ui.component.layout

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.classes.ValueState
import com.sqz.writingboard.settingState
import com.sqz.writingboard.ui.theme.PurpleForManual
import com.sqz.writingboard.ui.theme.RedForManual

@Composable
fun HideStyle(context: Context, modifier: Modifier = Modifier) {
    val valueState: ValueState = viewModel()
    val readEditButton = settingState.readSwitchState("edit_button", context)
    Column(
        verticalArrangement = Arrangement.Top
    ) {
        val area = modifier
            .fillMaxWidth()
            .height(80.dp)
        Spacer(
            modifier = if (
                (settingState.readSwitchState("off_button_manual", context))
            ) {
                modifier
                    .pointerInput(Unit) {
                        detectTapGestures { _ ->
                            valueState.onClickSetting = true
                        }
                    } then area
            } else {
                modifier.background(color = RedForManual) then area
            }
        )
    }
    Column(
        verticalArrangement = Arrangement.Bottom
    ) {
        val area = modifier
            .fillMaxWidth()
            .height(120.dp)
        Spacer(
            modifier = if (
                (settingState.readSwitchState("off_editButton_manual", context)) &&
                (readEditButton)
            ) {
                modifier
                    .pointerInput(Unit) {
                        detectTapGestures { _ ->
                            valueState.editAction = true
                        }
                    } then area
            } else if (
                (!settingState.readSwitchState("off_editButton_manual", context)) &&
                (readEditButton)
            ) {
                modifier.background(color = PurpleForManual) then area
            } else {
                modifier
            }
        )
    }
}