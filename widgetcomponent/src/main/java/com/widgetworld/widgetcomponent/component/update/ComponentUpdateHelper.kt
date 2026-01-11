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
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent
import com.widgetworld.widgetcomponent.proto.WidgetLayout
import com.widgetworld.widgetcomponent.provider.ExtraLargeWidgetProvider
import com.widgetworld.widgetcomponent.provider.LargeWidgetProvider
import com.widgetworld.widgetcomponent.provider.MediumWidgetProvider
import com.widgetworld.widgetcomponent.provider.common.ComponentContainerWidget.Companion.layoutKey

/**
 * 위젯 컴포넌트 업데이트를 위한 공통 유틸리티 클래스
 *
 * Manager 구현 시 재사용 가능한 코드를 제공합니다.
 */
object ComponentUpdateHelper {
    private const val TAG = "ComponentUpdateHelper"

    /**
     * 특정 컴포넌트 태그로 배치된 컴포넌트들을 조회합니다.
     * @param context Context
     * @param componentTag 컴포넌트 태그 (예: "TINY-Battery", "BluetoothBattery")
     * @return 배치된 컴포넌트 목록과 해당 위젯 ID의 쌍
     */
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

    /**
     * 컴포넌트 인스턴스를 조회합니다.
     * @param componentTag 컴포넌트 태그
     * @return WidgetComponent 인스턴스 또는 null
     */
    fun getComponentInstance(componentTag: String): WidgetComponent? {
        return WidgetComponentRegistry.getComponent(componentTag)
    }

    /**
     * RemoteViews를 생성합니다.
     * @param context Context
     * @return RemoteViews 인스턴스
     */
    fun createRemoteViews(context: Context): RemoteViews {
        return RemoteViews(context.packageName, R.layout.glance_root_layout)
    }

    /**
     * 위젯을 부분 업데이트합니다.
     * @param context Context
     * @param widgetId 위젯 ID
     * @param remoteViews 업데이트할 RemoteViews
     */
    fun partiallyUpdateWidget(context: Context, widgetId: Int, remoteViews: RemoteViews) {
        try {
            AppWidgetManager.getInstance(context)
                .partiallyUpdateAppWidget(widgetId, remoteViews)
        } catch (e: Exception) {
            Log.e(TAG, "Error partially updating widget $widgetId", e)
        }
    }
}
