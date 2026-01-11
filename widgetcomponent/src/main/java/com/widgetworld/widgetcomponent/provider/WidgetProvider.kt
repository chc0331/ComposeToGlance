package com.widgetworld.widgetcomponent.provider

import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import com.widgetworld.widgetcomponent.provider.common.CommonAppWidget
import com.widgetworld.widgetcomponent.provider.common.CommonWidgetProvider

internal class MediumAppWidget : CommonAppWidget()

class MediumWidgetProvider : CommonWidgetProvider() {
    override val glanceAppWidget: GlanceAppWidget
        get() = MediumAppWidget()

    override fun getComponentName(context: Context): ComponentName = ComponentName(
        context,
        MediumWidgetProvider::class.java.name
    )

    override fun getTag(): String = "MediumWidgetProvider"
}

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