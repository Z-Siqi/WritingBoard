package com.sqz.writingboard.ui.layout.main.item

import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.layout.LocalState
import com.sqz.writingboard.ui.theme.landscapeUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class BoardSizeHandler {
    private val _state: MutableStateFlow<LocalState>

    constructor(viewModel: MainViewModel?, state: MutableStateFlow<LocalState>) {
        if (viewModel == null) throw NullPointerException("This class requires be a part of MainViewModel")
        this._state = state
    }

    private data class LocalData(
        val stateHeight: Int = 0,
        val navHeight: Int = 0,
        val isLandscape: Boolean = false
    )

    private val _localData = MutableStateFlow<LocalData>(LocalData())

    private fun defaultSize(): WritingBoardPadding {
        val stateHeight = _localData.value.stateHeight
        val top = when {
            stateHeight > 80 -> 5.dp
            stateHeight > 50 -> 8.dp
            stateHeight > 30 -> 12.dp
            stateHeight > 20 -> 15.dp
            stateHeight > 5 -> 20.dp
            else -> 15.dp
        }
        val navHeight = _localData.value.navHeight
        val bottom = when {
            navHeight > 100 -> 5.dp
            navHeight > 80 -> 8.dp
            navHeight > 50 -> 12.dp
            navHeight > 30 -> 15.dp
            navHeight > 10 -> 18.dp
            navHeight > 5 -> 20.dp
            else -> 15.dp
        }
        return WritingBoardPadding(
            top = top, bottom = bottom, start = 20.dp, end = 20.dp
        )
    }

    private val _writingBoardPadding = MutableStateFlow(defaultSize())
    val writingBoardPadding: StateFlow<WritingBoardPadding> = _writingBoardPadding

    private fun defaultPadding(contentSize: IntSize) {
        // Edit via ime with low screen height
        if (_state.value.isImeOn && contentSize.height < 300) {
            _writingBoardPadding.update { it.copy(top = 2.dp, bottom = 3.dp) }
        } else _writingBoardPadding.update { update ->
            defaultSize().let { update.copy(top = it.top, bottom = it.bottom) }
        }
        // With a long width screen
        if (_localData.value.isLandscape && contentSize.width > 580) {
            _writingBoardPadding.update { it.copy(start = 30.dp, end = 30.dp) }
        } else _writingBoardPadding.update { update ->
            defaultSize().let { update.copy(start = it.start, end = it.end) }
        }
    }

    fun writingBoardPadding(
        screenSize: IntSize,
        contentSize: IntSize,
        navHeight: Int,
        stateHeight: Int,
    ): StateFlow<WritingBoardPadding> {
        this.defaultPadding(contentSize)
        _localData.update {
            it.copy(isLandscape = landscapeUnit(screenSize.height, screenSize.width))
        }
        _localData.update {
            when {
                navHeight != 0 && stateHeight != 0 -> it.copy(stateHeight, navHeight)
                stateHeight != 0 -> it.copy(stateHeight = stateHeight)
                else -> it.copy(navHeight = navHeight)
            }
        }
        return this.writingBoardPadding
    }
}