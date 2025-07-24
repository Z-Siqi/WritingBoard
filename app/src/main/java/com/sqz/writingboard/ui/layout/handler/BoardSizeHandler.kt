package com.sqz.writingboard.ui.layout.handler

import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.sqz.writingboard.ui.MainViewModel
import com.sqz.writingboard.ui.layout.LocalState
import com.sqz.writingboard.ui.layout.main.item.WritingBoardPadding
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
        val isLandscape: Boolean = false,
        val contentSize: IntSize = IntSize(0, 0)
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
        val contentSize = _localData.value.contentSize
        val widthLowHeight =
            contentSize.width > (contentSize.height * 1.8964) && contentSize.width > 520
        val startAndEnd = if (widthLowHeight) 30.dp else 20.dp
        return WritingBoardPadding(
            top = top, bottom = bottom, start = startAndEnd, end = startAndEnd
        )
    }

    private val _writingBoardPadding = MutableStateFlow(defaultSize())
    val writingBoardPadding: StateFlow<WritingBoardPadding> = _writingBoardPadding

    fun editWithLowScreenHeight(): Boolean {
        val height = _localData.value.contentSize.height
        return _state.value.isImeOn && height < 300 || height < 184
    }

    // Edit via ime with low screen height
    private var _lowHeightPadding: Boolean = false
    fun lowHeightPadding(boardBottomPaddingHeight: Int = 64) {
        if (editWithLowScreenHeight()) {
            _lowHeightPadding = true
            _writingBoardPadding.update { it.copy(top = 2.dp, bottom = 3.dp) }
        } else if (_lowHeightPadding) _writingBoardPadding.update { update ->
            _lowHeightPadding = false
            defaultSize().let {
                val bottom = if (_increasedBottom.value) {
                    it.bottom + boardBottomPaddingHeight.dp
                } else it.bottom
                update.copy(top = it.top, bottom = bottom)
            }
        }
    }

    private val _increasedBottom = MutableStateFlow(false)
    val increasedBottom: StateFlow<Boolean> = _increasedBottom

    fun boardBottomPadding(increase: Boolean, height: Int = 64) {
        if (increase && !_increasedEnd.value) {
            if (!editWithLowScreenHeight() && !_increasedBottom.value) _writingBoardPadding.update {
                _increasedBottom.value = true
                it.copy(bottom = it.bottom + height.dp)
            }
        } else {
            if (_increasedBottom.value) _writingBoardPadding.update {
                _increasedBottom.value = false
                it.copy(bottom = (it.bottom - height.dp).let { if (it < 0.dp) 0.dp else it })
            }
        }
    }


    private val _increasedEnd = MutableStateFlow(false)
    val increasedEnd: StateFlow<Boolean> = _increasedEnd

    fun boardEndPadding(width: Int = 90) {
        if (_localData.value.isLandscape && _state.value.isFocus) {
            if (!_increasedEnd.value) _writingBoardPadding.update {
                _increasedEnd.value = true
                it.copy(end = it.end + width.dp)
            }
        } else if (_increasedEnd.value) _writingBoardPadding.update {
            _increasedEnd.value = false
            it.copy(end = (it.end - width.dp).let { if (it < 0.dp) 0.dp else it })
        }
    }

    fun resetPadding() {
        if (_increasedEnd.value || _increasedBottom.value) _writingBoardPadding.update {
            _increasedBottom.value = false
            _increasedEnd.value = false
            it.copy(bottom = defaultSize().bottom, end = defaultSize().end)
        }
    }

    fun writingBoardPadding(
        screenSize: IntSize,
        contentSize: IntSize,
        navHeight: Int,
        stateHeight: Int
    ): StateFlow<WritingBoardPadding> {
        _localData.update { // set landscape and content size value
            it.copy(
                isLandscape = landscapeUnit(screenSize.height, screenSize.width),
                contentSize = contentSize
            )
        }
        _localData.update { // set navHeight and stateHeight value
            when {
                navHeight != 0 && stateHeight != 0 -> it.copy(stateHeight, navHeight)
                stateHeight != 0 -> it.copy(stateHeight = stateHeight)
                else -> it.copy(navHeight = navHeight)
            }
        }
        this.lowHeightPadding()
        when {
            !_lowHeightPadding && !_increasedBottom.value && !_increasedEnd.value -> {
                _writingBoardPadding.update { this.defaultSize() }
            }

            else -> _writingBoardPadding.update { update ->
                defaultSize().let { def ->
                    val top = !_lowHeightPadding
                    val bottom = !_increasedBottom.value && !_lowHeightPadding
                    val startAndEnd = !_increasedEnd.value
                    update.copy(
                        top = if (top) def.top else update.top,
                        bottom = if (bottom) def.bottom else update.bottom,
                        start = if (startAndEnd) def.start else update.start,
                        end = if (startAndEnd) def.end else update.end,
                    )
                }
            }
        }
        return this.writingBoardPadding
    }
}