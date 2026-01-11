package com.widgetworld.widgetcomponent.component.update

import android.content.Context
import com.widgetworld.widgetcomponent.component.WidgetComponent

interface ComponentUpdateManager<T> {

    val widget: WidgetComponent

    suspend fun syncState(context: Context, data: T)

    suspend fun updateByState(context: Context, widgetId: Int?, data: T)

    suspend fun updateByPartially(context: Context, widgetId: Int?, data: T)
}
