package com.sqz.writingboard.ui.layout.handler

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.NavRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NavControllerHandler {
    val coroutineScope: CoroutineScope

    constructor(viewModel: MainViewModel?) {
        if (viewModel == null) throw NullPointerException("This class requires be a part of MainViewModel")
        this.coroutineScope = viewModel.viewModelScope
    }

    private val _requestBack = MutableStateFlow(false)

    private val _navTo = MutableStateFlow<NavRoute?>(null)

    fun requestBack() = coroutineScope.launch {
        _requestBack.value = true
    }

    fun navigate(route: NavRoute) = coroutineScope.launch {
        _navTo.value = route
    }

    @Composable
    fun Controller(navController: NavHostController) {
        if (_requestBack.collectAsState().value) {
            navController.popBackStack()
            _requestBack.update { false }
        }
        if (_navTo.collectAsState().value != null) {
            navController.navigate(_navTo.collectAsState().value!!.name)
            _navTo.update { null }
        }
    }
}
