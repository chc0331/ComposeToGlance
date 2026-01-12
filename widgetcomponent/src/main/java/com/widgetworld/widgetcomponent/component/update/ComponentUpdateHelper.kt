package com.widgetworld.widgetcomponent.component.update

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.widgetworld.widgetcomponent.R
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent
import com.widgetworld.widgetcomponent.proto.WidgetLayout
import com.widgetworld.widgetcomponent.provider.ExtraLargeWidgetProvider
import com.widgetworld.widgetcomponent.provider.LargeWidgetProvider
import com.widgetworld.widgetcomponent.provider.MediumWidgetProvider
import com.widgetworld.widgetcomponent.provider.common.ComponentContainerWidget.Companion.layoutKey

internal object ComponentUpdateHelper {
    private const val TAG = "ComponentUpdateHelper"

    suspend fun findPlacedComponents(
        context: Context,
        componentTag: String
    ): List<Pair<Int, PlacedWidgetComponent>> {
        val glanceManager = GlanceAppWidgetManager(context)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetIds = listOf(
            MediumWidgetProvider::class.java,
            LargeWidgetProvider::class.java,
            ExtraLargeWidgetProvider::class.java
        ).flatMap { providerClass ->
            appWidgetManager.getAppWidgetIds(ComponentName(context, providerClass)).asIterable()
        }.toIntArray()
        val results = mutableListOf<Pair<Int, PlacedWidgetComponent>>()

        widgetIds.forEach { widgetId ->
            try {
                val glanceId = glanceManager.getGlanceIdBy(widgetId)
                val currentState = getAppWidgetState(
                    context,
                    PreferencesGlanceStateDefinition,
                    glanceId
                )
                val currentLayout: WidgetLayout = WidgetLayout.parseFrom(currentState[layoutKey])
                val placedComponents = currentLayout.placedWidgetComponentList
                    .filter { it.widgetTag == componentTag }

                placedComponents.forEach { component ->
                    results.add(widgetId to component)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error finding placed components for widget $widgetId", e)
            }
        }

        return results
    }

    fun createRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.glance_root_layout)
    }

    fun partiallyUpdateWidget(context: Context, widgetId: Int, remoteViews: RemoteViews) {
        try {
            AppWidgetManager.getInstance(context)
                .partiallyUpdateAppWidget(widgetId, remoteViews)
        } catch (e: Exception) {
            Log.e(TAG, "Error partially updating widget $widgetId", e)
        }
    }
}
