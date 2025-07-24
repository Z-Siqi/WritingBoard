package com.sqz.writingboard.common.feedback

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.SoundEffectConstants
import android.view.View
import com.sqz.writingboard.common.isEmulator
import com.sqz.writingboard.preference.SettingOption

class AndroidFeedback(private val settings: SettingOption, private val view: View) : Feedback {
    private fun lessVibrateEffect(): Boolean = settings.vibrate() == 0
    private fun moderatelyVibrateEffect(): Boolean = settings.vibrate() == 1
    private fun moreVibrateEffect(): Boolean = settings.vibrate() == 2

    private fun errVibratorLogPrinter(message: String) {
        Log.d("WritingBoard", "Vibrator service error, message: $message")
        Log.d("WritingBoardTag", "Change to sound effect due to Vibrator service error")
    }

    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            view.context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        view.context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    @Override
    override fun onClickSound() {
        view.playSoundEffect(SoundEffectConstants.CLICK)
    }

    @Override
    override fun onClickVibrate() {
        try {
            if (!vibrator.hasVibrator()) throw NullPointerException("Vibrator service is null")
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            if (isEmulator()) this.onClickSound()
        } catch (e: Exception) {
            e.message?.let { this.errVibratorLogPrinter(it) }
            this.onClickSound()
        }
    }

    @Override
    override fun onClickEffect() {
        when {
            lessVibrateEffect() -> this.onClickSound()
            moreVibrateEffect() -> this.onClickVibrate()
            else -> this.onClickSound()
        }
    }

    @Override
    override fun onHeavyClickEffect() {
        when {
            lessVibrateEffect() -> this.onClickSound()
            moderatelyVibrateEffect() -> this.onClickVibrate().also { this.onClickSound() }
            moreVibrateEffect() -> this.onClickVibrate()
            else -> this.onClickSound()
        }
    }

    @Override
    override fun onDoubleTickEffect() {
        try {
            if (!vibrator.hasVibrator()) throw NullPointerException("Vibrator service is null")
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
            if (isEmulator()) this.onClickSound()
        } catch (e: Exception) {
            e.message?.let { this.errVibratorLogPrinter(it) }
            this.onClickSound()
            this.onClickSound()
        }
    }
}
