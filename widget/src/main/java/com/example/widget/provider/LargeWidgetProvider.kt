package com.example.widget.provider

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import com.example.dsl.WidgetScope
import com.example.widget.component.BatteryComponent

class LargeAppWidget : DslAppWidget() {

    override val sizeMode: SizeMode
        get() = SizeMode.Responsive(
            setOf(
                DpSize(300.dp, 150.dp),
                DpSize(250.dp, 120.dp)
            )
        )

    override fun WidgetScope.DslContent() {
        BatteryComponent()
    }
}

class LargeWidgetProvider : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = LargeAppWidget()
}
