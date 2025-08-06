package com.sqz.writingboard

import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.sqz.writingboard.glance.GlanceWidgetManager
import com.sqz.writingboard.ui.MainLayout
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import com.sqz.writingboard.ui.theme.isAndroid15OrAbove

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val window: Window = this.window
            WritingBoardTheme {
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                this.LightSystemBarsSetter(controller)
                if (!isAndroid15OrAbove) {
                    this.setSystemBarsColor()
                } else { // for set non-gesture nav mode on Android 15+
                    this.setNavBarColor()
                }
                MainLayout(
                    context = applicationContext,
                    view = window.decorView
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setNavBarColor() {
        window.navigationBarColor = Color.Transparent.toArgb()
    }

    @Suppress("DEPRECATION")
    private fun setSystemBarsColor() {
        window.statusBarColor =  Color.Transparent.toArgb()
        this.setNavBarColor()
    }

    @Composable
    @ReadOnlyComposable
    private fun LightSystemBarsSetter(controller: WindowInsetsControllerCompat) {
        if (isSystemInDarkTheme()) {
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        } else {
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }

    override fun onPause() {
        super.onPause()
        GlanceWidgetManager.updateWidget(applicationContext)
    }
}
