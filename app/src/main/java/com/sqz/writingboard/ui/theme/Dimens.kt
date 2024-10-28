package com.sqz.writingboard.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

const val navBarHeightDp = 70
const val navBarHeightDpIsEditing = 55
const val navBarHeightDpLandscape = 88

val isLandscape: Boolean
    @Composable get() = LocalConfiguration.current.screenWidthDp > (LocalConfiguration.current.screenHeightDp * 1.1)
