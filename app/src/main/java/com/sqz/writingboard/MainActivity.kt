package com.sqz.writingboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.ui.component.WritingBoardEE
import com.sqz.writingboard.ui.WritingBoardLayout
import com.sqz.writingboard.ui.component.WritingBoardNone
import com.sqz.writingboard.ui.WritingBoardSetting
import com.sqz.writingboard.ui.component.ErrorWithSystemVersionA13
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import com.sqz.writingboard.ui.theme.themeColor

val settingState = WritingBoardSettingState()
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WritingBoard")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val window: Window = this.window
            WritingBoardTheme {
                window.statusBarColor = themeColor("statusBarColor").toArgb()
                window.navigationBarColor = themeColor("navigationBarColor").toArgb()
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "WritingBoard"
                    ) {
                        composable("WritingBoard") {
                            WritingBoardLayout(navController)
                            Log.i("WritingBoardTag", "NavHost: Screen is WritingBoardLayout.")
                        }
                        composable("Setting") {
                            WritingBoardSetting(navController)
                            Log.i("WritingBoardTag", "NavHost: Screen is WritingBoardSetting.")
                        }
                        composable("UpdateScreen") {
                            WritingBoardNone()
                            window.statusBarColor = themeColor("statusBarColor").toArgb()
                            window.navigationBarColor = themeColor("navigationBarColor").toArgb()
                            Log.i("WritingBoardTag", "NavHost: Screen is WritingBoardNone.")
                        }
                        composable("EE") {
                            WritingBoardEE()
                        }
                        composable("ErrorWithSystemVersionA13") {
                            ErrorWithSystemVersionA13(navController)
                            Log.i("WritingBoardTag", "NavHost: Screen is ErrorWithSystemVersionA13.")
                        }
                    }
                }
            }
        }
    }
}