package com.sqz.writingboard.glance

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.TypedValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.core.graphics.createBitmap
import androidx.core.util.TypedValueCompat.spToPx
import com.sqz.writingboard.preference.SettingOption
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GlanceWidgetTextHelper(private val context: Context) {

    @Composable
    fun fontStyleData(): Int {
        val sharedPreferences = context.getSharedPreferences(
            SettingOption(context).preferencesFileName(), Context.MODE_PRIVATE
        )
        val defValue = SettingOption(context).fontStyle()
        return sharedPreferences.intFlow(SettingOption.FONT_STYLE, defValue).collectAsState(
            initial = defValue
        ).value
    }

    @Composable
    fun fontStyleExtraData(): Int {
        val sharedPreferences = context.getSharedPreferences(
            SettingOption(context).preferencesFileName(), Context.MODE_PRIVATE
        )
        val defValue = SettingOption(context).fontStyleExtra()
        return sharedPreferences.intFlow(SettingOption.FONT_STYLE_EXTRA, defValue).collectAsState(
            initial = defValue
        ).value
    }

    @Composable
    fun fontWeightData(): Int {
        val sharedPreferences = context.getSharedPreferences(
            SettingOption(context).preferencesFileName(), Context.MODE_PRIVATE
        )
        val defValue = SettingOption(context).fontWeight()
        return sharedPreferences.intFlow(SettingOption.FONT_WEIGHT, defValue).collectAsState(
            initial = defValue
        ).value
    }

    /** SharedPreferences as Flow **/
    private fun SharedPreferences.intFlow(key: String, defValue: Int): Flow<Int> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, updatedKey ->
            if (key == updatedKey) trySend(getInt(key, defValue))
        }
        registerOnSharedPreferenceChangeListener(listener)
        awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }

    companion object {
        fun Context.textAsBitmap(
            text: String,
            fontSize: TextUnit,
            textColor: Color = Color.Black,
            bgColor: Color = Color.Transparent,
            letterSpacing: Float = 0.1f,
            fontTypeface: Typeface,
            maxWidth: Int
        ): Bitmap {
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = spToPx(fontSize.value, this@textAsBitmap.resources.displayMetrics)
                this.color = textColor.toArgb()
                this.letterSpacing = letterSpacing
                typeface = fontTypeface
            }
            var textIn = text.replace("\n\n\n", "\n\n")

            // Use StaticLayout to handle line breaks
            var staticLayout =
                StaticLayout.Builder.obtain(textIn, 0, textIn.length, paint, maxWidth)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1f)
                    .setIncludePad(true)
                    .build()
            // Create a Bitmap of the appropriate size
            var width = staticLayout.width
            var height = staticLayout.height

            // Remove text if will OOM
            var estimatedSize = estimateBitmapMemoryUsage(width, height)
            val limit = 12 * 1024 * 1024
            while (estimatedSize > limit && textIn.isNotEmpty()) {
                textIn = if (textIn.length > 12288) {
                    textIn.dropLast(textIn.length - 8000)
                } else if (textIn.length > 1100) when {
                    estimatedSize > limit * 1.3 -> textIn.dropLast(1024).plus("\n(...)")
                    estimatedSize > limit * 1.2 -> textIn.dropLast(512).plus("\n(...)")
                    estimatedSize > limit * 1.15 -> textIn.dropLast(256).plus("\n(...)")
                    estimatedSize > limit * 1.1 -> textIn.dropLast(128).plus("\n(...)")
                    else -> textIn.dropLast(8).plus("\n(...)")
                } else when {
                    estimatedSize > limit * 1.1 -> textIn.dropLast(64).plus("\n(...)")
                    estimatedSize > limit * 1.08 -> textIn.dropLast(48).plus("\n(...)")
                    estimatedSize > limit * 1.01 -> textIn.dropLast(32).plus("\n(...)")
                    else -> textIn.dropLast(8).plus("\n(...)")
                }
                staticLayout =
                    StaticLayout.Builder.obtain(textIn, 0, textIn.length, paint, maxWidth)
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0f, 1f)
                        .setIncludePad(true)
                        .build()
                width = staticLayout.width
                height = staticLayout.height
                estimatedSize = estimateBitmapMemoryUsage(width, height)
            }

            // Draw text onto a Bitmap
            val image = createBitmap(width, height, Bitmap.Config.ALPHA_8)
            val canvas = Canvas(image)
            canvas.drawColor(bgColor.toArgb())
            staticLayout.draw(canvas)

            return image
        }

        private fun estimateBitmapMemoryUsage(width: Int, height: Int): Int {
            val bytesPerPixel = 3 // Bitmap.Config.ALPHA_8
            return (width * height * bytesPerPixel).toInt()
        }

        fun dpToPx(context: Context, dp: Float): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
            )
        }

        fun Dp.toPxInt(context: Context): Int {
            return dpToPx(context, this.value.toFloat()).toInt()
        }
    }
}
