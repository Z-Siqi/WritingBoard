package com.sqz.writingboard.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.R
import com.sqz.writingboard.classes.ValueState

@Composable
fun LayoutButton(
    isEditing: Boolean,
    readAlwaysVisibleText: Boolean,
    valueState: ValueState,
    readCleanAllText: Boolean,
    padding: Modifier,
    modifier: Modifier = Modifier
) = if (isEditing) { // Not implemented
    if (readAlwaysVisibleText) {

    } else {
        DefaultButtonStyle(
            valueState = valueState,
            readCleanAllText = readCleanAllText
        )
    }
} else {
    Column(
        modifier = modifier.fillMaxSize() then padding,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        FloatingActionButton(onClick = { valueState.onClickSetting = true }) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings)
            )
        }
    }
}

@Composable
fun DefaultButtonStyle(
    valueState: ValueState,
    readCleanAllText: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(36.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        //clean all text button
        if (readCleanAllText) {
            FloatingActionButton(onClick = {
                valueState.cleanAllText = true
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.clean_all_texts_button)
                )
            }
        }
        //done button
        Spacer(modifier = modifier.height(10.dp))
        FloatingActionButton(onClick = {
            valueState.matchText = true
            valueState.doneAction = true
        }) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = stringResource(R.string.done)
            )
        }
    }
}

@Preview
@Composable
private fun DefaultButtonStylePreview() {
    val test: ValueState = viewModel()
    DefaultButtonStyle(valueState = test, true)
}
