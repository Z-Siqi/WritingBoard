package com.sqz.writingboard.ui.theme

import android.app.Activity
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

    val backgroundColor: Color //TODO
        @ReadOnlyComposable @Composable get() = when (localMode) {
            1 -> MaterialTheme.colorScheme.background
            else -> MaterialTheme.colorScheme.background
        }
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
            val window = (view.context as Activity).window
            //WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

enum class ThemeColor {
    StatusBarColor, NavigationBarColor, BackgroundColor, ShapeColor, BoardColor,
    CardColor, ScrolledContainerColor, TitleContentColor, SettingBackgroundColor,
    TextColor, BarColor,
}

@Composable
fun themeColor(themeColor: ThemeColor): Color {
    val readTheme = SettingOption(LocalContext.current).theme()
    when (themeColor) {
        //App Bars
        ThemeColor.StatusBarColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.outline
                1 -> MaterialTheme.colorScheme.secondary
                2 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.secondary
            }
        }

        ThemeColor.NavigationBarColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceVariant
                1 -> MaterialTheme.colorScheme.secondaryContainer
                2 -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            }
        }

        //WritingBoard screen
        ThemeColor.BackgroundColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceContainerLowest
                1 -> MaterialTheme.colorScheme.surfaceVariant
                2 -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        }

        ThemeColor.ShapeColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.tertiary
                1 -> MaterialTheme.colorScheme.primary
                2 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.primary
            }
        }

        ThemeColor.BoardColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceBright
                1 -> MaterialTheme.colorScheme.surfaceContainerLow
                2 -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceContainerLow
            }
        }
        //Setting screen
        ThemeColor.CardColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceContainerHighest
                1 -> MaterialTheme.colorScheme.primaryContainer
                2 -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        }

        ThemeColor.ScrolledContainerColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceDim
                1 -> MaterialTheme.colorScheme.secondaryContainer
                2 -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            }
        }

        ThemeColor.TitleContentColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.secondary
                1 -> MaterialTheme.colorScheme.primary
                2 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            }
        }

        ThemeColor.SettingBackgroundColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceBright
                1 -> MaterialTheme.colorScheme.surfaceVariant
                2 -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        }
        //WritingBoard text
        ThemeColor.TextColor -> {
            return when (readTheme) {
                1 -> MaterialTheme.colorScheme.secondary
                2 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.secondary
            }
        }
        //Scroll bar color
        ThemeColor.BarColor -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.secondary
                1 -> MaterialTheme.colorScheme.surfaceTint
                2 -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        }
    }
}