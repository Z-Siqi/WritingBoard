package com.sqz.writingboard.tile

import android.app.StatusBarManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import com.sqz.writingboard.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class QSTileRequestHelper(private val callback: Int) {
    companion object {
        fun callbackToString(callback: Int, context: Context): String {
            return when (callback) {
                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED -> context.getString(R.string.TILE_ADD_REQUEST_0)
                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> context.getString(R.string.TILE_ADD_REQUEST_1)
                StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> context.getString(R.string.TILE_ADD_REQUEST_2)
                StatusBarManager.TILE_ADD_REQUEST_ERROR_REQUEST_IN_PROGRESS -> context.getString(R.string.REQUEST_IN_PROGRESS)
                StatusBarManager.TILE_ADD_REQUEST_ERROR_MISMATCHED_PACKAGE -> "Failed: TILE_ADD_REQUEST_ERROR_MISMATCHED_PACKAGE"
                StatusBarManager.TILE_ADD_REQUEST_ERROR_APP_NOT_IN_FOREGROUND -> "Failed: TILE_ADD_REQUEST_ERROR_APP_NOT_IN_FOREGROUND"
                StatusBarManager.TILE_ADD_REQUEST_ERROR_BAD_COMPONENT -> "Failed: TILE_ADD_REQUEST_ERROR_BAD_COMPONENT"
                StatusBarManager.TILE_ADD_REQUEST_ERROR_NOT_CURRENT_USER -> "Failed: TILE_ADD_REQUEST_ERROR_NOT_CURRENT_USER"
                StatusBarManager.TILE_ADD_REQUEST_ERROR_NO_STATUS_BAR_SERVICE -> "Failed: TILE_ADD_REQUEST_ERROR_NO_STATUS_BAR_SERVICE"
                else -> "Unknown Failed: $callback"
            }
        }

        val callbackState = MutableStateFlow<Int?>(null)

        fun requestTileAdd(context: Context) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
                QSTileService.requestAdd(context) { result -> callbackState.update { result } }
            } else {
                Toast.makeText(context, R.string.not_support, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun makeToast(context: Context) {
        Toast.makeText(context, callbackToString(callback, context), Toast.LENGTH_SHORT).show()
        callbackState.update { null }
    }
}
