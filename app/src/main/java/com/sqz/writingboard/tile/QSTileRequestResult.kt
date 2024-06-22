package com.sqz.writingboard.tile

import android.annotation.SuppressLint
import android.app.StatusBarManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sqz.writingboard.R
import com.sqz.writingboard.ui.WritingBoardViewModel

class QSTileRequestResult {

    @SuppressLint("ComposableNaming")
    @Composable
    fun makeToast(viewModel: WritingBoardViewModel = viewModel()) {
        val context = LocalContext.current
        if (viewModel.resultOfQST == -1) {
            Toast.makeText(context, result(), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, result(), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("ComposableNaming")
    @Composable
    fun makeErrorLog(viewModel: WritingBoardViewModel = viewModel()) {
        if (viewModel.resultOfQST >= 1000) {
            Log.e("WritingBoardTag", result())
        }
    }

    @Composable
    fun result(viewModel: WritingBoardViewModel = viewModel()): String {
        return when (val resultOfQST = viewModel.resultOfQST) {
            -5 -> stringResource(R.string.TILE_ADD_REQUEST_NotDetected)
            -1 -> stringResource(R.string.TILE_ADD_REQUEST_NO_SUPPORT)
            StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED -> stringResource(R.string.TILE_ADD_REQUEST_0)
            StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> stringResource(R.string.TILE_ADD_REQUEST_1)
            StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> stringResource(R.string.TILE_ADD_REQUEST_2)
            StatusBarManager.TILE_ADD_REQUEST_ERROR_REQUEST_IN_PROGRESS -> stringResource(R.string.REQUEST_IN_PROGRESS)
            StatusBarManager.TILE_ADD_REQUEST_ERROR_MISMATCHED_PACKAGE -> "TILE_ADD_REQUEST_ERROR_MISMATCHED_PACKAGE"
            StatusBarManager.TILE_ADD_REQUEST_ERROR_APP_NOT_IN_FOREGROUND -> "TILE_ADD_REQUEST_ERROR_APP_NOT_IN_FOREGROUND"
            StatusBarManager.TILE_ADD_REQUEST_ERROR_BAD_COMPONENT -> "TILE_ADD_REQUEST_ERROR_BAD_COMPONENT"
            StatusBarManager.TILE_ADD_REQUEST_ERROR_NOT_CURRENT_USER -> "TILE_ADD_REQUEST_ERROR_NOT_CURRENT_USER"
            StatusBarManager.TILE_ADD_REQUEST_ERROR_NO_STATUS_BAR_SERVICE -> "TILE_ADD_REQUEST_ERROR_NO_STATUS_BAR_SERVICE"
            else -> "Unknown: $resultOfQST"
        }
    }
}
