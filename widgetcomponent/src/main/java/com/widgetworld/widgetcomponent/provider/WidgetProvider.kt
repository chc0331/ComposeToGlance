package com.widgetworld.widgetcomponent.provider

import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import com.widgetworld.widgetcomponent.provider.common.ComponentContainerWidget
import com.widgetworld.widgetcomponent.provider.common.ComponentContainerWidgetReceiver

internal class MediumAppWidget : ComponentContainerWidget()

class MediumWidgetProvider : ComponentContainerWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MediumAppWidget()

    override fun getComponentName(context: Context): ComponentName = ComponentName(
        context,
        MediumWidgetProvider::class.java.name
    )

    override fun getTag(): String = "MediumWidgetProvider"
}

internal class LargeAppWidget : ComponentContainerWidget()

class LargeWidgetProvider : ComponentContainerWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = LargeAppWidget()

    override fun getComponentName(context: Context): ComponentName = ComponentName(
        context,
        LargeWidgetProvider::class.java.name
    )

    override fun getTag(): String = "LargeWidgetProvider"
}

internal class ExtraLargeAppWidget : ComponentContainerWidget()

class ExtraLargeWidgetProvider : ComponentContainerWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = ExtraLargeAppWidget()

    override fun getComponentName(context: Context): ComponentName = ComponentName(
        context,
        ExtraLargeWidgetProvider::class.java.name
    )

    override fun getTag(): String = "ExtraLargeWidgetProvider"
}