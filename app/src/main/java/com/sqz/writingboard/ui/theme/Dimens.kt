package com.sqz.writingboard.ui.theme

import android.graphics.Rect
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView

val isAndroid15OrAbove = Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE

const val navBarHeightDp = 70
const val navBarHeightDpIsEditing = 55
const val navBarHeightDpLandscape = 88

val isLandscape: Boolean
    @Composable get() = LocalConfiguration.current.screenWidthDp > (LocalConfiguration.current.screenHeightDp * 1.1)

private val rect = Rect()
val getWindowVisibleDisplayDp: Int
    @Composable get() = LocalView.current.getWindowVisibleDisplayFrame(rect).let {
        (rect.bottom / LocalDensity.current.density).toInt()
    }

