package com.sqz.writingboard.ui

import android.util.Log
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.component.KeyboardVisibilityObserver
import com.sqz.writingboard.ui.layout.LocalState
import com.sqz.writingboard.ui.layout.main.WritingBoardLayout
import com.sqz.writingboard.ui.layout.main.item.WritingBoard
import com.sqz.writingboard.ui.layout.settings.SettingsLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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
            ImeVisibilityHandler(state = viewModel.state)
        }
        composable(NavRoute.Setting.name) {
            SettingsLayout(viewModel = viewModel)
        }
        composable(NavRoute.EE.name) {
            Surface {
                WritingBoard(navControllerHandler = viewModel.navControllerHandler)
            }
        }
    }
    viewModel.requestHandler.saveTextWhenWindowNotFocused(
        windowInfo = LocalWindowInfo.current,
        context = LocalContext.current
    )
    viewModel.navControllerHandler.Controller(navController)
}

@Composable
private fun ImeVisibilityHandler(state: MutableStateFlow<LocalState>) {
    KeyboardVisibilityObserver { isVisible ->
        state.update { it.copy(isImeOn = isVisible) }
    }
    LaunchedEffect(state.collectAsState().value.isImeOn) {
        if (state.value.isImeOn) Log.d("WritingBoard", "Keyboard is visible") else {
            Log.d("WritingBoard", "Keyboard is close")
        }
    }
}
