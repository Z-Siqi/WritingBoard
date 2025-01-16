package com.sqz.writingboard.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

class WritingBoardTextOnlyWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    private fun Content(modifier: GlanceModifier = GlanceModifier) {
        val context = LocalContext.current
        val size = LocalSize.current
        val fontFamily = fontStyleData(context)
        val fontExtraStyleData = fontExtraStyleData(context)

        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            WidgetBoard(size)

            Column(
                modifier = modifier
                    .size(size.width - 8.dp, size.height - 8.dp)
                    .clickable { openAppAction(context) }
            ) {
                LazyColumn(modifier = modifier.cornerRadius(15.dp)) {
                    item {
                        Text(
                            text = savedText(context),
                            modifier = modifier.fillMaxWidth().padding(2.dp)
                                .clickable { openAppAction(context) },
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontWeight = if (fontFamily != 1) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Medium
                                },
                                fontFamily = when (fontFamily) {
                                    0 -> FontFamily.Monospace
                                    1 -> null
                                    2 -> FontFamily.Serif
                                    3 -> when (fontExtraStyleData) {
                                        0 -> FontFamily.Cursive
                                        1 -> null
                                        else -> FontFamily.Cursive
                                    }
                                    else -> null
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}
