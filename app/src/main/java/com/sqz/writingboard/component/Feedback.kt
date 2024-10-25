package com.sqz.writingboard.component

import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.SoundEffectConstants
import android.view.View
import androidx.core.content.ContextCompat.getSystemService

class Feedback(view: View) {

    private val _view = view
    private val _vibrator = _view.context?.let { getSystemService(it, Vibrator::class.java) }

    fun createOneTick() {
        if (_vibrator != null) {
            _vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else {
            Log.e("WritingBoardTag", "vibrator is not null")
            _view.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    fun createDoubleTick() {
        if (_vibrator != null) {
            _vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
        } else {
            Log.e("WritingBoardTag", "vibrator is not null")
            _view.playSoundEffect(SoundEffectConstants.CLICK)
            _view.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    fun createClickSound() {
        _view.playSoundEffect(SoundEffectConstants.CLICK)
    }
}
