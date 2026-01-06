package com.widgetkit.widgetcomponent.component.update

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetkit.widgetcomponent.SizeType
import com.widgetkit.widgetcomponent.component.WidgetComponent
import com.widgetkit.widgetcomponent.provider.common.DslAppWidget
import com.widgetkit.widgetcomponent.provider.LargeAppWidget
interface ComponentUpdateManager<T> {

    val widget: WidgetComponent

    suspend fun syncState(context: Context, data: T)

    suspend fun updateByState(context: Context, widgetId: Int?, data: T)

    suspend fun updateByPartially(context: Context, widgetId: Int?, data: T)
}
