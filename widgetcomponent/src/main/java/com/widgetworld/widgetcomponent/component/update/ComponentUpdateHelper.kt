package com.widgetworld.widgetcomponent.component.update

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.widget.RemoteViews
import androidx.glance.appwidget.GlanceAppWidget
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
import com.widgetworld.widgetcomponent.provider.common.ComponentContainerWidgetReceiver

internal object ComponentUpdateHelper {
    private const val TAG = "ComponentUpdateHelper"

    suspend fun findPlacedComponents(
        context: Context,
        componentTag: String
    ): List<Triple<Int, PlacedWidgetComponent, GlanceAppWidget>> {
        val glanceManager = GlanceAppWidgetManager(context)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val providerClasses = listOf(
            MediumWidgetProvider::class.java,
            LargeWidgetProvider::class.java,
            ExtraLargeWidgetProvider::class.java
        )
        val results = mutableListOf<Triple<Int, PlacedWidgetComponent, GlanceAppWidget>>()

        providerClasses.forEach { providerClass ->
            try {
                val provider = providerClass.getDeclaredConstructor().newInstance() as ComponentContainerWidgetReceiver
                val glanceAppWidget = provider.glanceAppWidget
                val widgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, providerClass))

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
                            results.add(Triple(widgetId, component, glanceAppWidget))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error finding placed components for widget $widgetId", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating provider instance for ${providerClass.simpleName}", e)
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

    /**
     * widgetId에 해당하는 GlanceAppWidget 타입을 반환합니다.
     * 
     * @param context Context
     * @param widgetId 위젯 ID
     * @return 해당 widgetId의 GlanceAppWidget 인스턴스, 찾을 수 없으면 null
     */
    fun getGlanceAppWidgetForWidgetId(context: Context, widgetId: Int): GlanceAppWidget? {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val providerClasses = listOf(
            MediumWidgetProvider::class.java,
            LargeWidgetProvider::class.java,
            ExtraLargeWidgetProvider::class.java
        )

        return try {
            // AppWidgetProviderInfo를 통해 widgetId의 ComponentName 확인
            val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
            val componentName = appWidgetInfo?.provider ?: return null

            // ComponentName과 매칭되는 provider 찾기
            providerClasses.firstOrNull { providerClass ->
                ComponentName(context, providerClass) == componentName
            }?.let { providerClass ->
                try {
                    val provider = providerClass.getDeclaredConstructor().newInstance() as ComponentContainerWidgetReceiver
                    provider.glanceAppWidget
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating provider instance for ${providerClass.simpleName}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting GlanceAppWidget for widget $widgetId", e)
            null
        }
    }
}
