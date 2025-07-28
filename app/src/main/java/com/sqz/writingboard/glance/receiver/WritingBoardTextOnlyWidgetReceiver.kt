package com.sqz.writingboard.glance.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.sqz.writingboard.glance.widget.TextOnlyWidget

/** WritingBoardTextOnlyWidget **/
class WritingBoardTextOnlyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TextOnlyWidget()
}
