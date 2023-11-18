package com.sqz.writingboard.ui.component

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.ValueState
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
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .shadow(7.dp)
                .pointerInput(Unit) {
                    detectTapGestures { _ ->
                    }
                },
            color = MaterialTheme.colorScheme.secondaryContainer
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
                            contentDescription = "Setting"
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
                            contentDescription = "Edit"
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
                            contentDescription = "Done"
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
                            contentDescription = "Clean all texts"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ManualLayout(
    @SuppressLint("ModifierParameter") modifierPadding: Modifier,
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifierPadding,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        Card(
            modifier = modifier
                .size(200.dp, 100.dp)
                .shadow(5.dp, RoundedCornerShape(10.dp))
        ) {
            Column(
                modifier = modifier.fillMaxSize()
            ) {
                Text(
                    modifier = modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
                    text = text,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 20.sp,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Button(
                    modifier = modifier
                        .padding(8.dp)
                        .align(Alignment.End),
                    onClick = onClick
                ) {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = "Okay")
                }
            }
        }
    }
}
