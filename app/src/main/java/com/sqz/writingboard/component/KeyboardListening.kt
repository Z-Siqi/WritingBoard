package com.sqz.writingboard.component

import android.content.Context
import android.graphics.Rect
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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

@Suppress("unused")
class KeyboardHeight {
    companion object {
        val currentPx @Composable get() = KeyboardHeight().currentPx()
        val screenHigh @Composable get() = KeyboardHeight().screenHigh()
    }

    @Composable
    private fun screenHigh(): Int {
        return (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).toInt()
    }

    @Composable
    private fun currentPx(): Int {
        val context = LocalContext.current
        val localScreenHeight =
            (LocalConfiguration.current.screenHeightDp * LocalDensity.current.density).toInt()
        val rootView = LocalView.current
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val returnZero = 0
        var height by remember { mutableIntStateOf(0) }
        var notZero by remember { mutableStateOf(false) }
        var calculateNotZeroValue by remember { mutableIntStateOf(0) }
        DisposableEffect(rootView, inputMethodManager) {
            val listener = ViewTreeObserver.OnGlobalLayoutListener {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val keyboardNowHeight = localScreenHeight - rect.bottom
                val keyboardHeight = if (keyboardNowHeight < 0) {
                    notZero = true
                    calculateNotZeroValue = keyboardNowHeight * -1
                    returnZero
                } else {
                    if (notZero) {
                        keyboardNowHeight + calculateNotZeroValue
                    } else {
                        keyboardNowHeight
                    }
                }
                height = keyboardHeight
            }
            rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
            onDispose {
                rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }
        return height
    }
}