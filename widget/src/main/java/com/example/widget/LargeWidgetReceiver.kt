package com.example.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.example.toolkit.dsl.WidgetScope
import com.example.widget.dsl.DslAppWidget
import com.example.widget.content.MusicWidget


class LargeAppWidget : DslAppWidget() {
    override fun WidgetScope.DslContent() {
        MusicWidget()
    }
}

class LargeWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = LargeAppWidget()
}