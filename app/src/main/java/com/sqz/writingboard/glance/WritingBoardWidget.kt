package com.sqz.writingboard.glance

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import com.sqz.writingboard.MainActivity

class WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WritingBoardWidget()
}

class WritingBoardWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MyContent()
        }
    }

    @Composable
    private fun MyContent() {
        val size = LocalSize.current
        Box(
            modifier = GlanceModifier
                .size(size.width, size.height)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Button(
                text = "",
                onClick = actionStartActivity<MainActivity>(),
                colors = ButtonDefaults.buttonColors(ColorProvider(MaterialTheme.colorScheme.surfaceVariant)),
                modifier = GlanceModifier.size(size.width - 5.dp, size.height - 5.dp)
            )
            LazyColumn(
                modifier = GlanceModifier.size(size.width - 10.dp, size.height - 8.dp)
            ) {
                item {
                    Text(
                        text = "Click to open WritingBoard App",
                        modifier = GlanceModifier
                            .fillMaxSize()
                            .clickable(actionStartActivity<MainActivity>())
                    )
                }
                item {
                    Spacer(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clickable(actionStartActivity<MainActivity>())
                    )
                }
            }
        }
    }
}