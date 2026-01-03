package com.widgetkit.core.component.update

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetkit.core.SizeType
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.core.provider.common.DslAppWidget
import com.widgetkit.core.provider.LargeAppWidget
interface ComponentUpdateManager<T> {

    val widget: WidgetComponent

    suspend fun syncState(context: Context, data: T)

    suspend fun updateByState(context: Context, data: T)

    suspend fun updateByPartially(context: Context, data: T)
}
