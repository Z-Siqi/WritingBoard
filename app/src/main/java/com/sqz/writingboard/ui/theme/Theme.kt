package com.sqz.writingboard.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.sqz.writingboard.preference.SettingOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class WritingBoardTheme private constructor(private val localMode: Int) {
    companion object {
        private val mode = MutableStateFlow(-1)
        val getInit = this.mode.value != -1

        fun updateState(theme: Int): Int {
            this.mode.update { theme }
            return theme
        }

        val color @Composable get() = WritingBoardTheme(this.mode.collectAsState().value)
    }

    val backgroundColor: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.surfaceContainer
            2 -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.background
        }
    val boardBackground: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.surfaceContainerLow
            2 -> MaterialTheme.colorScheme.surfaceContainer
            else -> MaterialTheme.colorScheme.background
        }
    val boardShape: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.primary
            2 -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.primary
        }
    val boardText: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.secondary
            2 -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
    val navBars: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.surfaceContainerHigh
            2 -> MaterialTheme.colorScheme.inversePrimary
            else -> MaterialTheme.colorScheme.surfaceContainer
        }
    val settingsCardBackground: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.surfaceContainer
            2 -> MaterialTheme.colorScheme.inversePrimary
            else -> MaterialTheme.colorScheme.surface
        }
    val settingsBackground: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.surfaceContainerLow
            2 -> MaterialTheme.colorScheme.surfaceContainerHigh
            else -> MaterialTheme.colorScheme.surface
        }
    val settingsBgTopBarScrolled: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.surfaceContainerHigh
            2 -> MaterialTheme.colorScheme.surfaceContainerHighest
            else -> MaterialTheme.colorScheme.surfaceContainer
        }
    val settingsTopBarContent: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.secondary
            2 -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurface
        }
    val scrollBar: Color
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.surfaceTint
            2 -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.secondary
        }
    val transparent: Color = Color.Transparent
}

@Composable
fun WritingBoardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    if (!WritingBoardTheme.getInit) {
        WritingBoardTheme.updateState(SettingOption(LocalContext.current).theme())
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            //val window = (view.context as Activity).window
            //WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
