package com.sqz.writingboard.glance

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
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
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontFamily
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.sqz.writingboard.MainActivity
import com.sqz.writingboard.dataStore
import com.sqz.writingboard.R
import com.sqz.writingboard.classes.WritingBoardSettingState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

/** WritingBoardWidget **/
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
        val size = LocalSize.current
        val fontFamily = fontStyleData(context)
        Box(
            modifier = GlanceModifier,
            contentAlignment = Alignment.Center
        ) {
            WidgetBoard(size)

            LazyColumn(
                modifier = GlanceModifier.size(size.width - 12.dp, size.height - 8.dp)
            ) {
                item {
                    Text(
                        text = savedText(context),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontWeight = if (fontFamily != 1) {
                                androidx.glance.text.FontWeight.Bold
                            } else {
                                androidx.glance.text.FontWeight.Medium
                            },
                            fontFamily = when (fontFamily) {
                                0 -> FontFamily.Monospace
                                1 -> null
                                2 -> FontFamily.Serif
                                3 -> FontFamily.Cursive
                                else -> null
                            }
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
            LazyColumn {
                item {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = GlanceModifier.fillMaxWidth()
                    ) {
                        Image(
                            provider = ImageProvider(R.drawable.widget_button),
                            contentDescription = stringResource(R.string.edit),
                            modifier = GlanceModifier.size(45.dp)
                                .clickable { openAppAction(context) },
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
            }
        }
    }
}

/** WritingBoardTextOnlyWidget **/
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
        val size = LocalSize.current
        val fontFamily = fontStyleData(context)
        Box(
            modifier = GlanceModifier,
            contentAlignment = Alignment.Center
        ) {
            WidgetBoard(size)

            Column(
                modifier = GlanceModifier
                    .size(size.width - 12.dp, size.height - 8.dp)
                    .clickable { openAppAction(context) }
            ) {
                LazyColumn {
                    item {
                        Text(
                            text = savedText(context),
                            modifier = GlanceModifier.fillMaxWidth()
                                .clickable { openAppAction(context) },
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontWeight = if (fontFamily != 1) {
                                    androidx.glance.text.FontWeight.Bold
                                } else {
                                    androidx.glance.text.FontWeight.Medium
                                },
                                fontFamily = when (fontFamily) {
                                    0 -> FontFamily.Monospace
                                    1 -> null
                                    2 -> FontFamily.Serif
                                    3 -> FontFamily.Cursive
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

/** UI function **/
@Composable
private fun WidgetBoard(size: DpSize) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
    } else {
        Button(
            text = "",
            onClick = actionStartActivity<MainActivity>(),
            colors = ButtonDefaults.buttonColors(ColorProvider(Color(0xFF00668B))),
            modifier = GlanceModifier.size(size.width, size.height)
        )
        Button(
            text = "",
            onClick = actionStartActivity<MainActivity>(),
            colors = ButtonDefaults.buttonColors(ColorProvider(Color(0xFFDCE3E9))),
            modifier = GlanceModifier.size(size.width - 5.dp, size.height - 5.dp)
        )
    }
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
@Composable
private fun savedText(context: Context): String {
    val text = stringPreferencesKey("saved_text")
    val savedText by context.dataStore.data
        .map { preferences ->
            preferences[text] ?: ""
        }
        .collectAsState(initial = "")
    return savedText
}

/** Open WritingBoard app action**/
private fun openAppAction(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

/** Load font_style **/
@Composable
private fun fontStyleData(context: Context): Int {
    val sharedPreferences =
        context.getSharedPreferences("WritingBoardSetting", Context.MODE_PRIVATE)
    val defaultFontFamily =
        WritingBoardSettingState().readSegmentedButtonState("font_style", context)
    return sharedPreferences.intFlow(
        key = "font_style", defaultFontFamily
    ).collectAsState(initial = defaultFontFamily).value
}