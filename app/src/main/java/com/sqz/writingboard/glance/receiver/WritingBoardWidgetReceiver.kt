package com.sqz.writingboard.glance.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.sqz.writingboard.glance.widget.DefaultWidget

/** WritingBoardWidget **/
class WritingBoardWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DefaultWidget()
}
