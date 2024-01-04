package com.sqz.writingboard.component

import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService

@Composable
fun Vibrate(){
    val context = LocalContext.current
    val vibrator = getSystemService(context, Vibrator::class.java)

    if (vibrator != null) {
        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        Log.i("WritingBoardTag", "vibrator have on")
    } else {
        Log.e("WritingBoardTag", "vibrator is not null")
    }
}