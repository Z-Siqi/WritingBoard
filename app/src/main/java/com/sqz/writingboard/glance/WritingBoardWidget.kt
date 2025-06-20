package com.sqz.writingboard.glance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Spacer
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import com.sqz.writingboard.MainActivity
import com.sqz.writingboard.dataStore
import com.sqz.writingboard.preferences.SettingOption
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

/** WritingBoardWidget **/
class WritingBoardWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WritingBoardWidget()
}

/** WritingBoardTextOnlyWidget **/
class WritingBoardTextOnlyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WritingBoardTextOnlyWidget()
}

/** UI function **/
@SuppressLint("RestrictedApi")
@Composable
internal fun WidgetBoard(size: DpSize, modifier: GlanceModifier = GlanceModifier) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        WidgetBoardLayout(size)
    } else {
        WidgetBoardLayoutForOldApi(size)
    }
}

@RequiresApi(31)
@Composable
private fun WidgetBoardLayout(size: DpSize, modifier: GlanceModifier = GlanceModifier) {
    val roundValue =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) 23.dp else 15.dp
    val round = modifier.cornerRadius(roundValue)
    Spacer(
        modifier = modifier.bgSize(size) then round then modifier.background(
            GlanceTheme.colors.primary
        )
    )
    Spacer(
        modifier = modifier.contentSize(size) then round then modifier.background(
            GlanceTheme.colors.surfaceVariant
        )
    )
}

@SuppressLint("RestrictedApi")
@Composable
private fun WidgetBoardLayoutForOldApi(size: DpSize, modifier: GlanceModifier = GlanceModifier) {
    Button(
        text = "", onClick = {},
        modifier = modifier.bgSize(size) then modifier.background(
            ColorProvider(Color(0xFF00668B))
        )
    )
    Button(
        text = "", onClick = {},
        modifier = modifier.contentSize(size) then modifier.background(
            ColorProvider(Color(0xFFDCE3E9))
        ),
        colors = ButtonDefaults.buttonColors(ColorProvider(Color(0xFFDCE3E9)))
    )
}

@Composable
private fun GlanceModifier.bgSize(size: DpSize): GlanceModifier {
    val bgSize = this.size(size.width, size.height)
    return bgSize
}

@Composable
private fun GlanceModifier.contentSize(size: DpSize): GlanceModifier {
    val contentSize = this.size(size.width - 5.dp, size.height - 5.dp)
    return contentSize
}

/** SharedPreferences as Flow **/
private fun SharedPreferences.intFlow(key: String, defaultValue: Int): Flow<Int> = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, updatedKey ->
        if (key == updatedKey) {
            trySend(getInt(key, defaultValue))
        }
    }
    registerOnSharedPreferenceChangeListener(listener)
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}

/** Load savedText **/
@Suppress("FlowOperatorInvokedInComposition")
@Composable
fun savedText(context: Context): String {
    val text = stringPreferencesKey("saved_text")
    val savedText by context.dataStore.data.map { preferences ->
        preferences[text] ?: " "
    }.collectAsState(initial = " ")
    return savedText
}

/** Open WritingBoard app action**/
internal fun openAppAction(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

/** Load font_style **/
@Composable
internal fun fontStyleData(context: Context): Int {
    val sharedPreferences =
        context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
    val defaultFontFamily = SettingOption(context).fontStyle()
    return sharedPreferences.intFlow(
        key = "font_style", defaultFontFamily
    ).collectAsState(initial = defaultFontFamily).value
}

/** Load font_style_extra **/
@Composable
internal fun fontExtraStyleData(context: Context): Int {
    val sharedPreferences =
        context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
    val defaultFontFamily = SettingOption(context).fontStyle()
    return sharedPreferences.intFlow(
        key = "font_style_extra", defaultFontFamily
    ).collectAsState(initial = defaultFontFamily).value
}

/*fun Context.textAsBitmap(
    text: String,
    fontSize: TextUnit,
    color: Color = Color.Black,
    letterSpacing: Float = 0.1f,
    fontTypeface: Typeface
): Bitmap {
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    paint.textSize = spToPx(fontSize.value, this.resources.displayMetrics)
    paint.color = color.toArgb()
    paint.letterSpacing = letterSpacing
    paint.typeface = fontTypeface

    val baseline = -paint.ascent()
    val width = (paint.measureText(text)).toInt()
    val height = (baseline + paint.descent()).toInt()
    val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawText(text, 0f, baseline, paint)
    return image
}

fun dpToPx(context: Context, dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
}*/
