package com.sqz.writingboard.glance

import android.content.Context
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
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
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import com.sqz.writingboard.MainActivity
import com.sqz.writingboard.dataStore
import kotlinx.coroutines.flow.map

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
        val context = LocalContext.current
        val text = stringPreferencesKey("saved_text")
        val savedText by context.dataStore.data
            .map { preferences ->
                preferences[text] ?: ""
            }
            .collectAsState(initial = "")

        val size = LocalSize.current
        val background = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            GlanceModifier.background(MaterialTheme.colorScheme.primary)
        } else {
            GlanceModifier
        }
        Box(
            modifier = GlanceModifier
                .size(size.width, size.height)
                then background,
            contentAlignment = Alignment.Center
        ) {
            Button(
                text = "",
                onClick = actionStartActivity<MainActivity>(),
                colors = ButtonDefaults.buttonColors(ColorProvider(MaterialTheme.colorScheme.surfaceVariant)),
                modifier = GlanceModifier.size(size.width - 5.dp, size.height - 5.dp)
            )
            LazyColumn(
                modifier = GlanceModifier
                    .size(size.width - 10.dp, size.height - 8.dp)
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                item {
                    Text(
                        text = savedText,
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
            }
        }
        Column(
            verticalAlignment = Alignment.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = GlanceModifier.fillMaxSize().padding(8.dp)
        ) {
            Button(
                text = "Edit",
                onClick = actionStartActivity<MainActivity>(),
                modifier = GlanceModifier
            )
        }
    }
}