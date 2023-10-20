package com.sqz.writingboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.sqz.writingboard.ui.theme.WritingBoardTheme

val settingState = WritingBoardSettingState()
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WritingBoard")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            WritingBoardTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    NavHost(
                        navController = navController,
                        startDestination = "WritingBoard"
                    ) {
                        composable("WritingBoard") {
                            WritingBoardLayout(navController)
                            Log.i("WritingBoardTag", "NavHost: Screen is WritingBoardLayout.")
                        }
                        composable("Setting") {
                            WritingBoardSetting(navController, context = context)
                            Log.i("WritingBoardTag", "NavHost: Screen is WritingBoardSetting.")
                        }
                        composable("WritingBoardNone") {
                            WritingBoardNone()
                            Log.i("WritingBoardTag", "NavHost: Screen is WritingBoardNone.")
                        }
                        composable("EE") {
                            WritingBoardEE()
                        }
                    }
                }
            }
        }
    }
}