package com.sqz.writingboard.glance

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
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
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.sqz.writingboard.R

class WritingBoardWidget : GlanceAppWidget() {

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
        val setSize = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            modifier.size(size.width - 18.dp, size.height - 12.dp)
        } else {
            modifier.size(size.width - 12.dp, size.height - 8.dp)
        }
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            WidgetBoard(size = size)

            LazyColumn(
                modifier = setSize
            ) {
                item {
                    Column {
                        Text(
                            text = savedText(context),
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
                                    3 -> FontFamily.Cursive
                                    else -> null
                                }
                            ),
                            modifier = modifier.fillMaxSize()
                        )
                        Spacer(modifier = modifier.size(size.width, 45.dp))
                    }
                }
            }
        }
        Column(
            verticalAlignment = Alignment.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = modifier.fillMaxSize().padding(8.dp)
        ) {
            Image(
                provider = ImageProvider(R.drawable.widget_button),
                contentDescription = LocalContext.current.getString(R.string.edit),
                modifier = modifier.size(45.dp).cornerRadius(12.dp)
                    .clickable { openAppAction(context) },
                contentScale = ContentScale.FillBounds
            )
        }
    }
}
