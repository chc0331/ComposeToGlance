package com.widgetkit.core.provider

import android.content.ComponentName
import android.content.Context
import androidx.compose.ui.unit.DpSize
import androidx.glance.appwidget.GlanceAppWidget
import com.widgetkit.core.proto.SizeType
import com.widgetkit.core.provider.common.CommonAppWidget
import com.widgetkit.core.provider.common.CommonWidgetProvider


internal class ExtraLargeAppWidget : CommonAppWidget()

class ExtraLargeWidgetProvider : CommonWidgetProvider() {

    override val glanceAppWidget: GlanceAppWidget
        get() = ExtraLargeAppWidget()

    override fun getComponentName(context: Context): ComponentName = ComponentName(
        context,
        ExtraLargeWidgetProvider::class.java.name
    )

    override fun getTag(): String = "ExtraLargeWidgetProvider"
}

