package com.sqz.writingboard.ui.layout.handler

import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.NavRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NavControllerHandler {
    val coroutineScope: CoroutineScope

    constructor(viewModel: MainViewModel?) {
        if (viewModel == null) throw NullPointerException("This class requires be a part of MainViewModel")
        this.coroutineScope = viewModel.viewModelScope
    }

    data class NavState(
        val navTo: NavRoute? = null,
        val requestBack: Boolean = false,
        val currentDestination: NavRoute? = null
    )

    private val _navState = MutableStateFlow(NavState())
    val getNavState: StateFlow<NavState> = _navState

    fun requestBack() = coroutineScope.launch {
        _navState.update { it.copy(requestBack = true) }
    }

    fun navigate(route: NavRoute) = coroutineScope.launch {
        _navState.update { it.copy(navTo = route) }
    }

    fun controller(
        navController: NavHostController,
        getValue: State<NavState>
    ): NavHostController {
        NavRoute.entries.forEach {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.route == it.name) {
                    _navState.update { state -> state.copy(currentDestination = it) }
                }
            }
        }
        if (getValue.value.requestBack) {
            navController.popBackStack()
            _navState.update { it.copy(requestBack = false) }
        }
        if (getValue.value.navTo != null) {
            navController.navigate(getValue.value.navTo!!.name)
            _navState.update { it.copy(navTo = null) }
        }
        return navController
    }
}
