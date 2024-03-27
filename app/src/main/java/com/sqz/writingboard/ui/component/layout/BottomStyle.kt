package com.sqz.writingboard.ui.component.layout

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.R
import com.sqz.writingboard.classes.ValueState
import com.sqz.writingboard.settingState
import com.sqz.writingboard.ui.theme.themeColor

@Composable
fun BottomStyle(context: Context, modifier: Modifier = Modifier) {
    val valueState: ValueState = viewModel()
    val readEditButton = settingState.readSwitchState("edit_button", context)
    val readCleanAllText = settingState.readSwitchState("clean_all_text", context)
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        val height = if (valueState.isEditing) {
            55.dp
        } else {
            70.dp
        }
        if (!valueState.editingHorizontalScreen) {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .height(height)
                    .shadow(7.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { _ ->
                        }
                    },
                color = themeColor("navigationBarColor")
            ) {
                val settingButtonLocation = if (readEditButton) {
                    Alignment.Start
                } else {
                    Alignment.CenterHorizontally
                }
                if (!valueState.isEditing) {
                    val padding = if (readEditButton) {
                        modifier.padding(start = 16.dp)
                    } else {
                        modifier.padding(start = 0.dp)
                    }
                    Column(
                        modifier = modifier.fillMaxWidth(),
                        horizontalAlignment = settingButtonLocation
                    ) {
                        OutlinedButton(
                            modifier = modifier
                                .padding(10.dp)
                                    then padding,
                            onClick = { valueState.onClickSetting = true },
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                }
                if (
                    (readEditButton) &&
                    (!valueState.editButton)
                ) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.End
                    ) {
                        OutlinedButton(
                            modifier = modifier
                                .padding(10.dp)
                                    then modifier.padding(end = 16.dp),
                            onClick = { valueState.editAction = true },
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                    }
                }
                if (valueState.editButton || valueState.isEditing) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.End
                    ) {
                        OutlinedButton(
                            modifier = modifier
                                .padding(10.dp)
                                    then modifier.padding(end = 16.dp),
                            onClick = {
                                valueState.matchText = true
                                valueState.doneAction = true
                            },
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = stringResource(R.string.done)
                            )
                        }
                    }
                }
                if (valueState.isEditing && readCleanAllText) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            modifier = modifier
                                .padding(10.dp)
                                    then modifier.padding(end = 16.dp),
                            onClick = { valueState.cleanAllText = true },
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.clean_all_texts_button)
                            )
                        }
                    }
                }
            }
        }
    }
}