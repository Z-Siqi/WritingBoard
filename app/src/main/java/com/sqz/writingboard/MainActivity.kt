package com.sqz.writingboard

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.os.postDelayed
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.ui.MainLayout
import com.sqz.writingboard.ui.WritingBoardViewModel
import com.sqz.writingboard.ui.component.ErrorWithSystemVersionA13
import com.sqz.writingboard.ui.component.WritingBoardEE
import com.sqz.writingboard.ui.component.WritingBoardNone
import com.sqz.writingboard.ui.main.WritingBoardLayout
import com.sqz.writingboard.ui.setting.WritingBoardSetting
import com.sqz.writingboard.ui.theme.WritingBoardTheme
import com.sqz.writingboard.ui.theme.isAndroid15OrAbove

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "WritingBoard")

@Deprecated("")
enum class NavRoute {
    WritingBoard, Setting, UpdateScreen, EE, ErrorWithSystemVersionA13
}

const val newModel: Boolean = true

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val viewModel: WritingBoardViewModel = viewModel()
            val window: Window = this.window
            val navController = rememberNavController()
            WritingBoardTheme {
                val controller = WindowInsetsControllerCompat(window, window.decorView)
                if (!isAndroid15OrAbove) {
                    this.setSystemBarsColor()
                } else { // for set non-gesture nav mode on Android 15+
                    this.setNavBarColor()
                }
                if (newModel) { //TODO: rewrite code
                    MainLayout(applicationContext)
                    controller.isAppearanceLightStatusBars = true
                    controller.isAppearanceLightNavigationBars = true
                } else Surface(
                    modifier = Modifier.fillMaxSize() then if (!isAndroid15OrAbove) {
                        Modifier.systemBarsPadding()
                    } else Modifier.windowInsetsPadding(WindowInsets.statusBars),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    var screen by rememberSaveable { mutableStateOf("N/A") }
                    NavHost(
                        navController = navController,
                        startDestination = NavRoute.WritingBoard.name
                    ) {
                        composable(NavRoute.WritingBoard.name) {
                            WritingBoardLayout(
                                navToSetting = { navController.navigate(NavRoute.Setting.name) },
                                view = window.decorView,
                                viewModel = viewModel
                            )
                            screen = NavRoute.WritingBoard.name
                        }
                        composable(NavRoute.Setting.name) {
                            WritingBoardSetting(
                                navController = navController,
                                context = applicationContext,
                                view = window.decorView,
                                viewModel = viewModel
                            )
                            screen = NavRoute.Setting.name
                        }
                        composable(NavRoute.UpdateScreen.name) {
                            WritingBoardNone()
                            if (!isAndroid15OrAbove) setSystemBarsColor()
                            Log.d("WritingBoardTag", "NavHost: Screen is WritingBoardNone.")
                        }
                        composable(NavRoute.EE.name) {
                            WritingBoardEE()
                        }
                        composable(NavRoute.ErrorWithSystemVersionA13.name) {
                            ErrorWithSystemVersionA13(navController)
                            Log.d(
                                "WritingBoardTag", "NavHost: Screen is ErrorWithSystemVersionA13."
                            )
                        }
                    }
                    var it by rememberSaveable { mutableStateOf("") }
                    if (screen != it) {
                        Log.d("WritingBoardTag", "NavHost: Screen is $screen")
                        it = screen
                    }
                }
            }
            NavScreen.screenUpdate(navController)
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
}

object NavScreen {
    private var _updateScreen by mutableStateOf(false)
    private var _ee by mutableStateOf(false)

    fun updateScreen(ee: Boolean = false) = if (!ee) {
        _updateScreen = true
    } else _ee = true

    fun screenUpdate(navController: NavHostController) {
        if (_updateScreen) {
            navController.navigate("UpdateScreen")
            Handler(Looper.getMainLooper()).postDelayed(50) {
                navController.popBackStack()
            }
            _updateScreen = false
        }
        if (_ee) {
            navController.navigate("EE")
            Handler(Looper.getMainLooper()).postDelayed(80000) {
                navController.popBackStack()
            }
            _ee = false
        }
    }
}
