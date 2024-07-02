package com.sqz.writingboard.component

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.SoundEffectConstants
import android.view.View
import androidx.core.content.ContextCompat.getSystemService

class Feedback(
    context: Context ?= null,
    view: View ?= null
) {
    private val _vibrator = context?.let { getSystemService(it, Vibrator::class.java) }
    private val _view = view

    /** Feedback(context) request **/
    fun createOneTick() {
        if (_vibrator != null) {
            _vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            Log.e("WritingBoardTag", "vibrator is not null")
        }
    }

    /** Feedback(view) request **/
    fun createClickSound() {
        _view?.playSoundEffect(SoundEffectConstants.CLICK)
    }
}
