package com.example.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.dsl.WidgetScope
import com.example.widget.content.BatteryComponent
import com.example.widget.dsl.DslAppWidget


class LargeAppWidget : DslAppWidget() {
    override fun WidgetScope.DslContent() {
        BatteryComponent()
    }
}

class LargeWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = LargeAppWidget()
}