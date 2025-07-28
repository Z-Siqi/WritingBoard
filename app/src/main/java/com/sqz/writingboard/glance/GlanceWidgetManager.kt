package com.sqz.writingboard.glance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.sqz.writingboard.MainActivity
import com.sqz.writingboard.common.io.importedFontName
import com.sqz.writingboard.glance.GlanceWidgetManager.Companion.isAndroid15Min
import com.sqz.writingboard.glance.GlanceWidgetTextHelper.Companion.textAsBitmap
import com.sqz.writingboard.glance.GlanceWidgetTextHelper.Companion.toPxInt
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

open class GlanceWidgetManager : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    @Composable
    private fun Content(context: Context) {
        Box(contentAlignment = Alignment.Center) {
            Content(context = context, modifier = GlanceModifier)
        }
    }

    @Composable
    protected open fun Content(context: Context, modifier: GlanceModifier) {
        val size = LocalSize.current
        if (isAndroid12Min) {
            WidgetBoardLayout(size, modifier)
        } else {
            WidgetBoardLayoutForOldApi(size, modifier)
        }
    }

    protected fun openAppAction(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @Composable
    protected fun WidgetText(
        size: DpSize,
        widthDecrease: Dp,
        context: Context,
        modifier: GlanceModifier = GlanceModifier
    ) {
        val textHelper = GlanceWidgetTextHelper(context)
        val isCustomFontSetting =
            textHelper.fontStyleData() == 3 && textHelper.fontStyleExtraData() == 1
        val fontFile = if (isCustomFontSetting) File(context.filesDir, importedFontName) else null
        if (isCustomFontSetting && fontFile?.exists() == true) {
            Image(
                provider = ImageProvider(
                    context.textAsBitmap(
                        text = textHelper.getSavedText(),
                        fontSize = 15.sp,
                        letterSpacing = 0.1f,
                        fontTypeface = Typeface.Builder(fontFile).build(),
                        maxWidth = (size.width - (widthDecrease * 2)).toPxInt(context)
                    )
                ),
                contentDescription = textHelper.getSavedText(),
                modifier = modifier.fillMaxSize()
            )
        } else Text(
            text = textHelper.getSavedText(),
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontWeight = if (
                    textHelper.fontWeightData() != 1 ||
                    textHelper.fontStyleData() != 1
                ) FontWeight.Bold else FontWeight.Medium,
                fontFamily = when (textHelper.fontStyleData()) {
                    0 -> FontFamily.Monospace
                    1 -> null
                    2 -> FontFamily.Serif
                    3 -> if (textHelper.fontStyleExtraData() != 0) null else FontFamily.Cursive
                    else -> null
                }
            ),
            modifier = modifier.fillMaxSize()
        )
    }

    @Override
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("WritingBoard_Debug", "GlanceWidgetManager: provideGlance id = $id")
        provideContent { Content(context) }
    }

    companion object {
        val isAndroid12Min = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        val isAndroid15Min = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM

        @OptIn(DelicateCoroutinesApi::class)
        fun updateWidget(context: Context) {
            GlobalScope.launch(Dispatchers.IO) { GlanceWidgetManager().updateAll(context) }
        }
    }
}

@RequiresApi(31)
@Composable
private fun WidgetBoardLayout(size: DpSize, modifier: GlanceModifier) {
    val roundValue = if (isAndroid15Min) 23.dp else 15.dp
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
private fun WidgetBoardLayoutForOldApi(size: DpSize, modifier: GlanceModifier) {
    Button(
        text = "", onClick = {}, modifier = modifier.bgSize(size) then modifier.background(
            ColorProvider(Color(0xFF00668B))
        )
    )
    Button(
        text = "", onClick = {}, modifier = modifier.contentSize(size) then modifier.background(
            ColorProvider(Color(0xFFDCE3E9))
        ), colors = ButtonDefaults.buttonColors(ColorProvider(Color(0xFFDCE3E9)))
    )
}

private fun GlanceModifier.bgSize(size: DpSize): GlanceModifier {
    return this.size(size.width, size.height)
}

private fun GlanceModifier.contentSize(size: DpSize): GlanceModifier {
    return this.size(size.width - 5.dp, size.height - 5.dp)
}
