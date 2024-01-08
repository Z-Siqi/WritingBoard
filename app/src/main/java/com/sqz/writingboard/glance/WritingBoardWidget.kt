package com.sqz.writingboard.glance

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
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
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.sqz.writingboard.MainActivity
import com.sqz.writingboard.dataStore
import com.sqz.writingboard.R
import kotlinx.coroutines.flow.map

class WritingBoardWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WritingBoardWidget()
}

class WritingBoardWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current
        val text = stringPreferencesKey("saved_text")
        val savedText by context.dataStore.data
            .map { preferences ->
                preferences[text] ?: ""
            }
            .collectAsState(initial = "")

        val size = LocalSize.current
        Box(
            modifier = GlanceModifier,
            contentAlignment = Alignment.Center
        ) {
            Button(
                text = "",
                onClick = actionStartActivity<MainActivity>(),
                colors = ButtonDefaults.buttonColors(GlanceTheme.colors.primary),
                modifier = GlanceModifier.size(size.width, size.height)
            )
            Button(
                text = "",
                onClick = actionStartActivity<MainActivity>(),
                colors = ButtonDefaults.buttonColors(GlanceTheme.colors.surfaceVariant),
                modifier = GlanceModifier.size(size.width - 5.dp, size.height - 5.dp)
            )
            LazyColumn(
                modifier = GlanceModifier
                    .size(size.width - 12.dp, size.height - 8.dp)
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                item {
                    Text(
                        text = savedText,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontWeight = androidx.glance.text.FontWeight.Medium
                        ),
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
                item {
                    Spacer(modifier = GlanceModifier.size(size.width, 45.dp))
                }
            }
        }
        Column(
            verticalAlignment = Alignment.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = GlanceModifier.fillMaxSize().padding(8.dp)
        ) {
            Image(
                provider = ImageProvider(R.drawable.widget_button),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
                modifier = GlanceModifier.size(45.dp).clickable(action {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                })
            )
        }
    }
}

class WritingBoardTextOnlyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WritingBoardTextOnlyWidget()
}

class WritingBoardTextOnlyWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current
        val text = stringPreferencesKey("saved_text")
        val savedText by context.dataStore.data
            .map { preferences ->
                preferences[text] ?: ""
            }
            .collectAsState(initial = "")

        val size = LocalSize.current
        Box(
            modifier = GlanceModifier,
            contentAlignment = Alignment.Center
        ) {
            Button(
                text = "",
                onClick = actionStartActivity<MainActivity>(),
                colors = ButtonDefaults.buttonColors(GlanceTheme.colors.primary),
                modifier = GlanceModifier.size(size.width, size.height)
            )
            Button(
                text = "",
                onClick = actionStartActivity<MainActivity>(),
                colors = ButtonDefaults.buttonColors(GlanceTheme.colors.surfaceVariant),
                modifier = GlanceModifier.size(size.width - 5.dp, size.height - 5.dp)
            )
            LazyColumn(
                modifier = GlanceModifier
                    .size(size.width - 12.dp, size.height - 8.dp)
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                item {
                    Text(
                        text = savedText,
                        modifier = GlanceModifier.fillMaxSize()
                            .clickable(actionStartActivity<MainActivity>()),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontWeight = androidx.glance.text.FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}