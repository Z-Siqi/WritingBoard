package com.sqz.writingboard.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.sqz.writingboard.settingState

private val darkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val lightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun WritingBoardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
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
            window.statusBarColor = colorScheme.secondary.toArgb()
            window.navigationBarColor = colorScheme.secondaryContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun themeColor(themeColor: String): Color {
    val context = LocalContext.current
    val readTheme = settingState.readSegmentedButtonState("theme", context)
    when (themeColor) {
        //WritingBoard screen
        "backgroundColor" -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceContainerLowest
                1 -> MaterialTheme.colorScheme.surfaceVariant
                2 -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        }

        "shapeColor" -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.secondaryContainer
                1 -> MaterialTheme.colorScheme.primary
                2 -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.primary
            }
        }

        "boardColor" -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceBright
                1 -> MaterialTheme.colorScheme.surfaceContainerLow
                2 -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceContainerLow
            }
        }
        //Setting screen
        "cardColor" -> {
            return when (settingState.readSegmentedButtonState("theme", context)) {
                0 -> MaterialTheme.colorScheme.surfaceContainerHighest
                1 -> MaterialTheme.colorScheme.primaryContainer
                2 -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        }

        "scrolledContainerColor" -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceDim
                1 -> MaterialTheme.colorScheme.secondaryContainer
                2 -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            }
        }

        "titleContentColor" -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.secondary
                1 -> MaterialTheme.colorScheme.primary
                2 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            }
        }

        "settingBackgroundColor" -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.surfaceBright
                1 -> MaterialTheme.colorScheme.surfaceVariant
                2 -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        }
        //WritingBoard text
        "textColor" -> {
            return when (readTheme) {
                1 -> MaterialTheme.colorScheme.secondary
                2 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.secondary
            }
        }
        //Scroll bar color
        "barColor" -> {
            return when (readTheme) {
                0 -> MaterialTheme.colorScheme.secondary
                1 -> MaterialTheme.colorScheme.surfaceTint
                2 -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        }

        else -> {
            return MaterialTheme.colorScheme.errorContainer
        }
    }
}