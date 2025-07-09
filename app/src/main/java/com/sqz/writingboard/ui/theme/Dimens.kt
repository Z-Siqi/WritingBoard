package com.sqz.writingboard.ui.theme

import android.graphics.Rect
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

val isAndroid15OrAbove = Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE

const val navBarHeightDp = 70
const val navBarHeightDpIsEditing = 55
const val navBarHeightDpLandscape = 88

fun landscapeUnit(height: Int, width: Int): Boolean {
    return width > (height * 1.1)
}

val isLandscape: Boolean
    @ReadOnlyComposable @Composable get() = LocalWindowInfo.current.containerSize.let {
        landscapeUnit(it.height, it.width)
    }

private val rect = Rect()
val getWindowVisibleDisplayDp: Int
    @Composable get() = LocalView.current.getWindowVisibleDisplayFrame(rect).let {
        (rect.bottom / LocalDensity.current.density).toInt()
    }

@Composable
@ReadOnlyComposable
fun Int.pxToDp(): Dp {
    val it = this
    val density = LocalDensity.current
    return with(density) { it.toDp() }
}

@Composable
@ReadOnlyComposable
fun Int.pxToDpInt(): Int {
    return this.pxToDp().value.toInt()
}
