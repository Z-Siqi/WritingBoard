package com.sqz.writingboard.ui.layout.main.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.R
import com.sqz.writingboard.common.feedback.Feedback
import com.sqz.writingboard.preference.SettingOption
import com.sqz.writingboard.ui.component.TextTooltipBox
import com.sqz.writingboard.ui.layout.LocalState
import com.sqz.writingboard.ui.layout.handler.RequestHandler
import com.sqz.writingboard.ui.layout.handler.BoardSizeHandler
import com.sqz.writingboard.ui.layout.main.item.WritingBoardPadding

@Composable
fun OutsideButton(
    onHidedButtonInReadOnly: Boolean,
    enableOutsideButton: Boolean,
    boardSizeHandler: BoardSizeHandler,
    state: LocalState,
    writingBoardPadding: WritingBoardPadding,
    requestHandler: RequestHandler,
    settings: SettingOption,
    feedback: Feedback
) {
    val context = LocalContext.current
    if (onHidedButtonInReadOnly && !enableOutsideButton) {
        if (settings.editButton() && state.isEditable) OnEditTextButton(
            onClick = { requestHandler.finishClick(context, feedback) },
            writingBoardPadding = writingBoardPadding
        ) else if (state.isFocus) OnEditTextButton(
            onClick = { requestHandler.finishClick(context, feedback) },
            writingBoardPadding = writingBoardPadding,
        )
    }
    if (enableOutsideButton) {
        val outside = boardSizeHandler.increasedBottom.collectAsState().value
        val boardEnd = boardSizeHandler.increasedEnd.collectAsState().value
        if (settings.buttonStyle() == 1) {
            if (!state.isFocus) SettingOutsideButton(
                onClick = { requestHandler.onSettingsClick(feedback) },
                writingBoardPadding = writingBoardPadding,
                modifier = Modifier.buttonPaddings(outside, writingBoardPadding)
            )
            if (settings.editButton() && !state.isEditable) EditOutsideButton(
                onClick = { requestHandler.onEditClick(feedback) },
                writingBoardPadding = writingBoardPadding,
                modifier = Modifier.buttonPaddings(outside, writingBoardPadding)
            )
        }
        if (settings.editButton() && state.isEditable) {
            OnEditTextOutsideButton(
                onClick = { requestHandler.finishClick(context, feedback) },
                writingBoardPadding = writingBoardPadding,
                boardEnd = boardEnd,
                modifier = Modifier.buttonPaddings(outside, writingBoardPadding)
            )
        } else if (state.isFocus) {
            OnEditTextOutsideButton(
                onClick = { requestHandler.finishClick(context, feedback) },
                writingBoardPadding = writingBoardPadding,
                boardEnd = boardEnd,
                modifier = Modifier.buttonPaddings(outside, writingBoardPadding)
            )
        }
    }
}

@Composable
private fun Modifier.buttonPaddings(
    outside: Boolean, writingBoardPadding: WritingBoardPadding
): Modifier {
    val bottom = if (outside) 1.dp else writingBoardPadding.bottom
    val modifier = this
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.navigationBars)
        .windowInsetsPadding(WindowInsets.displayCutout)
        .padding(15.dp)
        .padding(bottom = bottom)
    return modifier
}

@Composable
private fun OnEditTextOutsideButton(
    onClick: () -> Unit,
    writingBoardPadding: WritingBoardPadding,
    boardEnd: Boolean,
    modifier: Modifier = Modifier
) {
    val boardEndPadding = if (boardEnd) 0.dp else writingBoardPadding.end
    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.ime)
            .padding(end = boardEndPadding),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        TextTooltipBox(tooltipText = stringResource(id = R.string.done)) {
            FloatingActionButton(
                onClick = onClick, modifier = Modifier.size(80.dp, 45.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Filled.Done, stringResource(R.string.done))
            }
        }
    }
}

@Composable
private fun SettingOutsideButton(
    onClick: () -> Unit,
    writingBoardPadding: WritingBoardPadding,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(start = writingBoardPadding.start),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        TextTooltipBox(tooltipText = stringResource(R.string.settings)) {
            FloatingActionButton(onClick = onClick) {
                Icon(Icons.Filled.Settings, stringResource(R.string.settings))
            }
        }
    }
}

@Composable
private fun EditOutsideButton(
    onClick: () -> Unit,
    writingBoardPadding: WritingBoardPadding,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(end = writingBoardPadding.end),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
        TextTooltipBox(tooltipText = stringResource(R.string.edit)) {
            FloatingActionButton(onClick = onClick) {
                Icon(Icons.Filled.Edit, stringResource(R.string.edit))
            }
        }
    }
}
