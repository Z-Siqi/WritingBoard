package com.sqz.writingboard.ui.main.nav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.component.TextTooltipBox
import com.sqz.writingboard.ui.theme.getWindowVisibleDisplayDp
import com.sqz.writingboard.ui.theme.isLandscape

/**
 * The default control style and editing buttons.
 * AlwaysVisibleText; ButtonStyle is 0 & 1
 **/
@Deprecated("")
@Composable
fun LayoutButton(
    screenController: Boolean,
    isEditing: Boolean,
    onClickType: (ButtonClickType) -> Unit,
    readCleanAllText: Boolean,
    defaultStyle: Boolean,
    editButton: Boolean,
    readAlwaysVisibleText: Boolean,
    modifier: Modifier = Modifier,
    enable: Boolean = true
) = if (isEditing && enable) {
    if (readAlwaysVisibleText) {
        EditingButtonWithOpt(
            alwaysVisibleMode = screenController,
            onClick = { onClickType(ButtonClickType.Done) },
            onClickClean = { onClickType(ButtonClickType.Clean) },
            readCleanAllText = readCleanAllText
        )
    } else {
        DefaultEditingButtonStyle(
            onClick = { onClickType(ButtonClickType.Done) },
            onClickClean = { onClickType(ButtonClickType.Clean) },
            readCleanAllText = readCleanAllText
        )
    }
} else {
    if (!defaultStyle) Spacer(modifier = modifier) else {
        val padding = if (screenController) {
            modifier.padding(end = 36.dp, start = 36.dp, bottom = 16.dp)
        } else modifier.padding(36.dp)
        DefaultButtonStyle(
            onClickSetting = { onClickType(ButtonClickType.Setting) },
            onClickEdit = { onClickType(ButtonClickType.Edit) },
            editButton = editButton,
            padding = padding
        )
    }
}

@Composable
private fun DefaultButtonStyle(
    onClickSetting: () -> Unit,
    onClickEdit: () -> Unit,
    editButton: Boolean,
    modifier: Modifier = Modifier,
    padding: Modifier
) {
    //setting button
    Column(
        modifier = modifier.fillMaxSize() then padding,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        TextTooltipBox(tooltipText = stringResource(R.string.settings)) {
            FloatingActionButton(onClick = onClickSetting) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        }
    }
    //edit button
    if (editButton) {
        Column(
            modifier = modifier.fillMaxSize() then padding,
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            TextTooltipBox(tooltipText = stringResource(R.string.edit)) {
                FloatingActionButton(onClick = onClickEdit) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultButtonStylePreview() {
    DefaultButtonStyle({}, {}, true, Modifier, Modifier)
}

@Composable
private fun DefaultEditingButtonStyle(
    onClick: () -> Unit,
    onClickClean: () -> Unit,
    readCleanAllText: Boolean,
    modifier: Modifier = Modifier
) {
    val cleanButton = @Composable {
        TextTooltipBox(tooltipText = stringResource(R.string.clean_all_texts_button)) {
            FloatingActionButton(
                onClick = onClickClean,
                modifier = modifier.heightIn(max = if (getWindowVisibleDisplayDp > 200) 100.dp else 35.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.clean_all_texts_button)
                )
            }
        }
    }
    val doneButton = @Composable {
        TextTooltipBox(tooltipText = stringResource(id = R.string.done)) {
            FloatingActionButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.done)
                )
            }
        }
    }
    val layoutModifier = modifier.fillMaxSize() then if (getWindowVisibleDisplayDp > 180) {
        modifier.padding(36.dp)
    } else modifier.padding(end = 36.dp, bottom = 18.dp)
    if (isLandscape) Column(
        modifier = layoutModifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        //clean all text button
        if (readCleanAllText && getWindowVisibleDisplayDp > 175) cleanButton()
        //done button
        Spacer(modifier = modifier.height(10.dp))
        doneButton()
    } else Row(
        modifier = layoutModifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        if (readCleanAllText) cleanButton()
        Spacer(modifier = modifier.width(10.dp))
        doneButton()
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultEditingButtonStylePreview() {
    DefaultEditingButtonStyle({}, {}, true)
}

@Composable
private fun EditingButtonWithOpt(
    alwaysVisibleMode: Boolean,
    onClick: () -> Unit,
    onClickClean: () -> Unit,
    readCleanAllText: Boolean,
    modifier: Modifier = Modifier
) {
    val bottom = if (alwaysVisibleMode) 2.dp else 25.dp
    val end = if (alwaysVisibleMode && readCleanAllText) 0.dp else 26.dp
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ) {
        //clean all text button
        if (readCleanAllText) TextTooltipBox(tooltipText = stringResource(R.string.clean_all_texts_button)) {
            FloatingActionButton(
                modifier = modifier
                    .padding(10.dp)
                    .padding(start = 50.dp, bottom = bottom)
                    .size(45.dp, 45.dp),
                onClick = onClickClean,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.clean_all_texts_button)
                )
            }
        }
        //done button
        TextTooltipBox(tooltipText = stringResource(id = R.string.done)) {
            FloatingActionButton(
                modifier = modifier
                    .padding(10.dp)
                    .padding(end = end, bottom = bottom)
                    .size(if (alwaysVisibleMode && readCleanAllText) 50.dp else 80.dp, 45.dp),
                onClick = onClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.done)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditingButtonWithOptPreview() {
    EditingButtonWithOpt(false, {}, {}, true)
}
