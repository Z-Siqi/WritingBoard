package com.sqz.writingboard.glance.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import com.sqz.writingboard.R
import com.sqz.writingboard.glance.GlanceWidgetManager

class DefaultWidget : GlanceWidgetManager() {

    @Composable
    private fun WidgetButton(context: Context, modifier: GlanceModifier = GlanceModifier) {
        Column(
            verticalAlignment = Alignment.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = modifier.fillMaxSize().padding(8.dp)
        ) {
            Image(
                provider = ImageProvider(R.drawable.widget_button),
                contentDescription = LocalContext.current.getString(R.string.edit),
                modifier = modifier.size(45.dp).cornerRadius(12.dp).clickable {
                    super.openAppAction(context)
                },
                contentScale = ContentScale.FillBounds
            )
        }
    }

    private val widthDecrease = if (isAndroid15Min) 18.dp else 12.dp
    private val heightDecrease = if (isAndroid15Min) 12.dp else 8.dp

    @Composable
    private fun WidgetTextLayout(size: DpSize, context: Context, modifier: GlanceModifier) {
        LazyColumn(modifier = modifier) {
            item {
                Column {
                    super.WidgetText(
                        size = size,
                        widthDecrease = widthDecrease,
                        context = context,
                        modifier = modifier
                    )
                    Spacer(modifier = modifier.height(45.dp))
                }
            }
        }
    }

    @Override
    @Composable
    override fun Content(context: Context, modifier: GlanceModifier) {
        super.Content(context, modifier)
        val size = LocalSize.current
        val sizeModifier = modifier.size(size.width - widthDecrease, size.height - heightDecrease)
        this.WidgetTextLayout(
            size = size, context = context, modifier = sizeModifier
        )
        this.WidgetButton(context)
    }
}
