package com.sqz.writingboard.ui.main.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.component.TextTooltipBox
import com.sqz.writingboard.ui.theme.*

@Deprecated("")
@Composable
fun NavBarStyle(
    isEditing: Boolean,
    onClickType: (ButtonClickType) -> Unit,
    readCleanAllText: Boolean,
    editButton: Boolean,
    readEditButton: Boolean,
    softKeyboard: Boolean,
    modifier: Modifier = Modifier,
    landscapeMode: Boolean = isLandscape,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.displayCutout),
        verticalArrangement = Arrangement.Bottom
    ) {
        if (!landscapeMode) DefaultNavBar(
            isEditing = isEditing,
            onClickType = onClickType,
            editButton = editButton,
            readEditButton = readEditButton,
            readCleanAllText = readCleanAllText
        ) else Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End) {
            LandscapeNavBar(
                isEditing = isEditing,
                onClickType = onClickType,
                editButton = editButton,
                readEditButton = readEditButton,
                readCleanAllText = readCleanAllText
            )
        }
        Spacer(modifier = modifier) // this Spacer is for landscape mode
    }
    val density = LocalDensity.current
    val rightInsets = WindowInsets.displayCutout.getRight(density, LayoutDirection.Rtl)
    val modifierBar = if (isAndroid15OrAbove) {
        if (!isLandscape && !softKeyboard) Modifier
            .fillMaxWidth()
            .height((WindowInsets.navigationBars.getBottom(density) / density.density).dp)
        else Modifier
            .fillMaxHeight()
            .width((rightInsets / density.density).dp)
    } else modifier
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = AbsoluteAlignment.Right
    ) {
        Spacer(modifier = modifierBar.background(themeColor(ThemeColor.NavigationBarColor)))
    }
}

@Composable
private fun DefaultNavBar(
    isEditing: Boolean,
    onClickType: (ButtonClickType) -> Unit,
    editButton: Boolean,
    readEditButton: Boolean,
    readCleanAllText: Boolean,
    height: Dp = if (isEditing) navBarHeightDpIsEditing.dp else navBarHeightDp.dp
) = Surface(
    modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .shadow(7.dp)
        .pointerInput(Unit) {
            detectTapGestures { _ -> }
        },
    color = themeColor(ThemeColor.NavigationBarColor)
) {
    val settingButtonLocation =
        if (readEditButton) Alignment.Start else Alignment.CenterHorizontally
    if (!isEditing) Column( // Setting button (Appear only when NOT editing)
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = settingButtonLocation
    ) {
        TextTooltipBox(tooltipText = stringResource(R.string.settings)) {
            OutlinedButton(
                modifier = Modifier.padding(10.dp)
                        then Modifier.padding(start = if (readEditButton) 16.dp else 0.dp),
                onClick = { onClickType(ButtonClickType.Setting) },
                shape = RoundedCornerShape(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        }
    }
    if (readEditButton && !editButton) Column( // Edit button (Appear only when read-only mode is on)
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End
    ) {
        TextTooltipBox(tooltipText = stringResource(R.string.edit)) {
            OutlinedButton(
                modifier = Modifier
                    .padding(10.dp)
                        then Modifier.padding(end = 16.dp),
                onClick = { onClickType(ButtonClickType.Edit) },
                shape = RoundedCornerShape(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit)
                )
            }
        }
    }
    if (editButton || isEditing) Column( // Done button
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End
    ) {
        TextTooltipBox(tooltipText = stringResource(id = R.string.done)) {
            OutlinedButton(
                modifier = Modifier
                    .padding(10.dp)
                        then Modifier.padding(end = 16.dp),
                onClick = { onClickType(ButtonClickType.Done) },
                shape = RoundedCornerShape(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.done)
                )
            }
        }
    }
    if (isEditing && readCleanAllText) Column( // Clean button
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextTooltipBox(stringResource(R.string.clean_all_texts_button)) {
            OutlinedButton(
                modifier = Modifier
                    .padding(10.dp)
                        then Modifier.padding(end = 16.dp),
                onClick = { onClickType(ButtonClickType.Clean) },
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

@Composable
private fun LandscapeNavBar(
    isEditing: Boolean,
    onClickType: (ButtonClickType) -> Unit,
    editButton: Boolean,
    readEditButton: Boolean,
    readCleanAllText: Boolean,
    width: Dp = navBarHeightDpLandscape.dp
) = Surface(
    modifier = Modifier
        .fillMaxHeight()
        .width(width)
        .shadow(7.dp)
        .pointerInput(Unit) {
            detectTapGestures { _ -> }
        },
    color = themeColor(ThemeColor.NavigationBarColor)
) {
    val settingButtonLocation =
        if (readEditButton) Arrangement.Top else Arrangement.Center
    if (!isEditing) Column( // Setting button (Appear only when NOT editing)
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = settingButtonLocation
    ) {
        TextTooltipBox(tooltipText = stringResource(R.string.settings)) {
            OutlinedButton(
                modifier = Modifier.padding(8.dp)
                        then Modifier.padding(top = if (readEditButton) 16.dp else 0.dp),
                onClick = { onClickType(ButtonClickType.Setting) },
                shape = RoundedCornerShape(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings)
                )
            }
        }
    }
    val buttonPadding = Modifier.padding(8.dp) then Modifier.padding(bottom = 16.dp)
    if (readEditButton && !editButton) Column( // Edit button (Appear only when read-only mode is on)
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        TextTooltipBox(tooltipText = stringResource(R.string.edit)) {
            OutlinedButton(
                modifier = buttonPadding,
                onClick = { onClickType(ButtonClickType.Edit) },
                shape = RoundedCornerShape(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit)
                )
            }
        }
    }
    if (editButton || isEditing) Column( // Done button
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        TextTooltipBox(tooltipText = stringResource(id = R.string.done)) {
            OutlinedButton(
                modifier = buttonPadding,
                onClick = { onClickType(ButtonClickType.Done) },
                shape = RoundedCornerShape(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.done)
                )
            }
        }
    }
    val getBottom = getWindowVisibleDisplayDp // detect and hide when space not enough
    if (isEditing && readCleanAllText && getBottom > 150) Column( // Clean button
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        TextTooltipBox(stringResource(R.string.clean_all_texts_button)) {
            OutlinedButton(
                modifier = buttonPadding,
                onClick = { onClickType(ButtonClickType.Clean) },
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

@Preview(showBackground = true)
@Composable
private fun NavBarStylePreview() {
    NavBarStyle(
        isEditing = false, onClickType = {},
        editButton = false, landscapeMode = false, readEditButton = false,
        readCleanAllText = false, softKeyboard = false
    )
}
