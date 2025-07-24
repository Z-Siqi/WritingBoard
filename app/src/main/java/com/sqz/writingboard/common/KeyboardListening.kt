package com.sqz.writingboard.common

import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

@Composable
fun KeyboardVisibilityObserver(
    onKeyboardVisibilityChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val rootView = LocalView.current
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    DisposableEffect(rootView, imm) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height

            // Calculate the remaining visible height of the layout. If it's less than half of the screen height, consider the keyboard visible.
            val keypadHeight = screenHeight - rect.bottom
            val isKeyboardVisible = keypadHeight > screenHeight * 0.15

            onKeyboardVisibilityChanged(isKeyboardVisible)
        }

        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)

        // Remove the listener when the DisposableEffect is disposed.
        onDispose {
            rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
}
