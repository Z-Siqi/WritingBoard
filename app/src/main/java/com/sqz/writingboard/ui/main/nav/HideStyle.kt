package com.sqz.writingboard.ui.main.nav

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.component.TextTooltipBox
import com.sqz.writingboard.ui.theme.PurpleForManual
import com.sqz.writingboard.ui.theme.RedForManual

@Composable
fun HideStyle(
    onClickSetting: () -> Unit,
    onClickEdit: () -> Unit,
    editButton: Boolean,
    readIsOffEditButtonManual: Boolean,
    readIsOffButtonManual: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Top
    ) {
        val area = modifier
            .fillMaxWidth()
            .height(80.dp)
        TextTooltipBox(tooltipText = stringResource(R.string.settings)) {
            Spacer(
                modifier = if (readIsOffButtonManual) {
                    modifier
                        .pointerInput(Unit) {
                            detectTapGestures { _ ->
                                onClickSetting()
                            }
                        } then area
                } else {
                    modifier.background(color = RedForManual) then area
                }
            )
        }
    }
    Column(
        verticalArrangement = Arrangement.Bottom
    ) {
        val area = modifier
            .fillMaxWidth()
            .height(120.dp)
        TextTooltipBox(tooltipText = stringResource(R.string.edit)) {
            Spacer(
                modifier = if (readIsOffEditButtonManual && editButton) {
                    modifier
                        .pointerInput(Unit) {
                            detectTapGestures { _ ->
                                onClickEdit()
                            }
                        } then area
                } else if (!readIsOffEditButtonManual && editButton) {
                    modifier.background(color = PurpleForManual) then area
                } else modifier
            )
        }
    }
}
