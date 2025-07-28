package com.sqz.writingboard.glance.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.size
import com.sqz.writingboard.glance.GlanceWidgetManager

class TextOnlyWidget : GlanceWidgetManager() {

    private val widthDecrease = if (isAndroid15Min) 18.dp else 8.dp
    private val heightDecrease = if (isAndroid15Min) 12.dp else 8.dp

    @Composable
    private fun WidgetTextLayout(size: DpSize, context: Context, modifier: GlanceModifier) {
        LazyColumn(modifier = modifier) {
            item {
                super.WidgetText(
                    size = size,
                    widthDecrease = widthDecrease,
                    context = context,
                    modifier = modifier.clickable { super.openAppAction(context) }
                )
            }
        }
    }

    @Composable
    @Override
    override fun Content(context: Context, modifier: GlanceModifier) {
        super.Content(context, modifier)
        val size = LocalSize.current
        this.WidgetTextLayout(
            size = size,
            context = context,
            modifier = modifier.size(size.width - widthDecrease, size.height - heightDecrease)
        )
    }
}
