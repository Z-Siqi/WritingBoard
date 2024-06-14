package com.sqz.writingboard.component

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

class Vibrate(context: Context) {
    private val _vibrator = getSystemService(context, Vibrator::class.java)

    fun createOneTick() {
        if (_vibrator != null) {
            _vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            Log.e("WritingBoardTag", "vibrator is not null")
        }
    }
}