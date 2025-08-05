package com.sqz.writingboard.ui.theme

import android.graphics.Rect
import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

val isAndroid15OrAbove = Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE

val isAndroid13OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

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
fun getSystemTopBarMaxHeightDp(): Int {
    return if (WindowInsets.statusBars.getTopDp() > WindowInsets.displayCutout.getTopDp()) {
        WindowInsets.statusBars.getTopDp()
    } else {
        WindowInsets.displayCutout.getTopDp()
    }
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

@Composable
@ReadOnlyComposable
fun Int.dpToPxInt(): Int {
    val density = LocalDensity.current.density
    return (density * this).toInt()
}

@Composable
@ReadOnlyComposable
fun Float.dpToPxInt(): Int {
    return this.toInt().dpToPxInt()
}

@Composable
@ReadOnlyComposable
fun WindowInsets.getTopDp(): Int = this.getTop(LocalDensity.current).pxToDpInt()

@Composable
@ReadOnlyComposable
fun WindowInsets.getBottomPx(): Int = this.getBottom(LocalDensity.current)

@Composable
@ReadOnlyComposable
fun WindowInsets.getBottomDp(): Int = this.getBottomPx().pxToDpInt()

@Composable
@ReadOnlyComposable
fun WindowInsets.getLeftDp(): Int = this.getLeft(
    LocalDensity.current, LocalLayoutDirection.current
).pxToDpInt()

@Composable
@ReadOnlyComposable
fun WindowInsets.getRightDp(): Int = this.getRight(
    LocalDensity.current, LocalLayoutDirection.current
).pxToDpInt()
