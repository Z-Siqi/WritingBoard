package com.sqz.writingboard.common.feedback

interface Feedback {
    /**
     * Expect a normal click sound.
     */
    fun onClickSound()

    /**
     * Expect vibrate after click.
     */
    fun onClickVibrate()

    /**
     * Same as [onClickSound], but may be a vibration under some conditions.
     */
    fun onClickEffect()

    /**
     * Same as [onClickSound], but may be a vibration under some conditions.
     */
    fun onHeavyClickEffect()

    /**
     * Expect twice vibrate after click.
     */
    fun onDoubleTickEffect()
}
