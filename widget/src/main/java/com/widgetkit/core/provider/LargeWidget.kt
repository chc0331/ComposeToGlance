package com.widgetkit.core.provider

import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import com.widgetkit.core.provider.common.CommonAppWidget
import com.widgetkit.core.provider.common.CommonWidgetProvider

internal class LargeAppWidget : CommonAppWidget()

class LargeWidgetProvider : CommonWidgetProvider() {

    override val glanceAppWidget: GlanceAppWidget
        get() = LargeAppWidget()

    override fun getComponentName(context: Context): ComponentName = ComponentName(
        context,
        LargeWidgetProvider::class.java.name
    )

    override fun getTag(): String = "LargeWidgetProvider"
}
