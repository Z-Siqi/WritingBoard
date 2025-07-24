package com.sqz.writingboard.ui.layout.main.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
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
import com.sqz.writingboard.ui.layout.main.item.WritingBoardPadding

@Composable
fun DefaultButton(
    state: LocalState,
    writingBoardPadding: WritingBoardPadding,
    settings: SettingOption,
    requestHandler: RequestHandler,
    feedback: Feedback,
) {
    if (!state.isFocus) SettingButton(
        onClick = { requestHandler.onSettingsClick(feedback) },
        writingBoardPadding = writingBoardPadding
    )
    if (state.isFocus) {
        val context = LocalContext.current
        OnEditTextButton(
            onClick = { requestHandler.finishClick(context, feedback) },
            writingBoardPadding = writingBoardPadding
        )
    }
    if (settings.editButton() && !state.isEditable) EditButton(
        onClick = { requestHandler.onEditClick(feedback) },
        writingBoardPadding = writingBoardPadding
    )
}

@Composable
private fun Modifier.buttonPaddings(): Modifier {
    val modifier = this
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.navigationBars)
        .windowInsetsPadding(WindowInsets.displayCutout)
        .padding(16.dp)
    return modifier
}

@Composable
private fun SettingButton(
    onClick: () -> Unit,
    writingBoardPadding: WritingBoardPadding,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .buttonPaddings()
        .padding(start = writingBoardPadding.start, bottom = writingBoardPadding.bottom),
    verticalArrangement = Arrangement.Bottom,
    horizontalAlignment = Alignment.Start
) {
    TextTooltipBox(tooltipText = stringResource(R.string.settings)) {
        FloatingActionButton(onClick = onClick) {
            Icon(Icons.Filled.Settings, stringResource(R.string.settings))
        }
    }
}

@Composable
private fun ColumnEnd(
    writingBoardPadding: WritingBoardPadding,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) = Column(
    modifier = modifier
        .buttonPaddings()
        .padding(end = writingBoardPadding.end, bottom = writingBoardPadding.bottom),
    verticalArrangement = Arrangement.Bottom,
    horizontalAlignment = Alignment.End,
    content = content
)

@Composable
fun OnEditTextButton(
    onClick: () -> Unit,
    writingBoardPadding: WritingBoardPadding,
    modifier: Modifier = Modifier
) = ColumnEnd(
    writingBoardPadding = writingBoardPadding,
    modifier = modifier.windowInsetsPadding(WindowInsets.ime)
) {
    TextTooltipBox(tooltipText = stringResource(id = R.string.done)) {
        FloatingActionButton(onClick = onClick) {
            Icon(Icons.Filled.Done, stringResource(R.string.done))
        }
    }
}

@Composable
private fun EditButton(
    onClick: () -> Unit,
    writingBoardPadding: WritingBoardPadding,
) = ColumnEnd(writingBoardPadding) {
    TextTooltipBox(tooltipText = stringResource(R.string.edit)) {
        FloatingActionButton(onClick = onClick) {
            Icon(Icons.Filled.Edit, stringResource(R.string.edit))
        }
    }
}
