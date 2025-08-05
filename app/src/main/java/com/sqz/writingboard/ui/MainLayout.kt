package com.sqz.writingboard.ui

import android.content.Context
import android.util.Log
import android.view.View
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sqz.writingboard.common.KeyboardVisibilityObserver
import com.sqz.writingboard.common.feedback.AndroidFeedback
import com.sqz.writingboard.preference.SettingOption
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
fun MainLayout(
    context: Context,
    view: View,
    modifier: Modifier = Modifier
) {
    val viewModel: MainViewModel = viewModel()
    val settings = SettingOption(context = context)
    val feedback = AndroidFeedback(settings = settings, view = view)

    val navController = viewModel.navControllerHandler.controller(
        navController = rememberNavController(),
        getValue = viewModel.navControllerHandler.getNavState.collectAsState()
    )
    NavHost(
        navController = navController,
        startDestination = NavRoute.WritingBoard.name,
        modifier = modifier
    ) {
        composable(NavRoute.WritingBoard.name) {
            WritingBoardLayout(
                viewModel = viewModel, settings = settings, feedback = feedback
            )
            ImeVisibilityHandler(state = viewModel.state)
        }
        composable(NavRoute.Setting.name) {
            SettingsLayout(viewModel = viewModel, feedback = feedback, context = context)
        }
        composable(NavRoute.EE.name) {
            Surface {
                WritingBoard(navControllerHandler = viewModel.navControllerHandler)
            }
        }
    }
    viewModel.requestHandler.saveTextWhenWindowNotFocused(
        windowInfo = LocalWindowInfo.current, context = context
    )
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
