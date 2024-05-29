package com.sqz.writingboard.ui.main.control

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.theme.ThemeColor
import com.sqz.writingboard.ui.theme.themeColor

@Composable
fun NavBarStyle(
    isEditing: Boolean,
    onClickSetting: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDone: () -> Unit,
    onClickClean: () -> Unit,
    editButton: Boolean,
    editingHorizontalScreen: Boolean,
    readEditButton: Boolean,
    readCleanAllText: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        val height = if (isEditing) 55.dp else {
            70.dp
        }
        if (!editingHorizontalScreen) {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .height(height)
                    .shadow(7.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { _ -> }
                    },
                color = themeColor(ThemeColor.NavigationBarColor)
            ) {
                val settingButtonLocation = if (readEditButton) {
                    Alignment.Start
                } else {
                    Alignment.CenterHorizontally
                }
                if (!isEditing) {
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
                            onClick = onClickSetting,
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                }
                if (readEditButton && !editButton) {
                    // Edit button
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.End
                    ) {
                        OutlinedButton(
                            modifier = modifier
                                .padding(10.dp)
                                    then modifier.padding(end = 16.dp),
                            onClick = onClickEdit,
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                    }
                }
                if (editButton || isEditing) {
                    // Done button
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.End
                    ) {
                        OutlinedButton(
                            modifier = modifier
                                .padding(10.dp)
                                    then modifier.padding(end = 16.dp),
                            onClick = onClickDone,
                            shape = RoundedCornerShape(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = stringResource(R.string.done)
                            )
                        }
                    }
                }
                if (isEditing && readCleanAllText) {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedButton(
                            modifier = modifier
                                .padding(10.dp)
                                    then modifier.padding(end = 16.dp),
                            onClick = onClickClean,
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

@Preview(showBackground = true)
@Composable
private fun NavBarStylePreview() {
    NavBarStyle(
        isEditing = false,
        onClickSetting = {}, onClickEdit = {}, onClickDone = {}, onClickClean = {},
        editButton = false, editingHorizontalScreen = false, readEditButton = true,
        readCleanAllText = false
    )
}
