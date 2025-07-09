package com.sqz.writingboard.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.ui.layout.main.WritingBoardLayout
import com.sqz.writingboard.ui.layout.settings.SettingsLayout

enum class NavRoute {
    WritingBoard, Setting, EE
}

@Composable
fun MainLayout(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = viewModel()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavRoute.WritingBoard.name,
        modifier = modifier
    ) {
        composable(NavRoute.WritingBoard.name) {
            WritingBoardLayout(viewModel = viewModel)
        }
        composable(NavRoute.Setting.name) {
            SettingsLayout(viewModel = viewModel)
        }
    }
    viewModel.navControllerHandler.Controller(navController)
}
