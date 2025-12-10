package com.example.widget.component.devicecare

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.example.widget.component.update.ComponentUpdateHelper
import com.example.widget.component.update.ComponentUpdateManager

object DeviceCareUpdateManager : ComponentUpdateManager<DeviceState> {
    
    private const val TAG = "DeviceCareUpdateManager"
    
    override val widget: DeviceCareWidget
        get() = DeviceCareWidget()

    override suspend fun updateComponent(
        context: Context,
        data: DeviceState
    ) {
        Log.i(TAG, "updateComponent: $data")
        
        // DataStore에 저장
        DeviceCareComponentDataStore.saveData(context, data)
        
        // 배치된 컴포넌트 찾기 및 업데이트
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                // GlanceAppWidgetState 업데이트 (위젯 재렌더링 시 올바른 값 표시)
                updateWidgetState(context, widgetId, data)
                
                // RemoteViews로 부분 업데이트 (점수 텍스트만)
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                
                // 점수 계산
                val score = DeviceHealthCalculator.calculateScore(data)
                
                // 점수 텍스트만 부분 업데이트
                // Linear Progress는 ProgressBar가 아니므로 부분 업데이트 불가
                // updateWidgetState를 통해 전체 위젯이 재렌더링되므로 Progress는 자동으로 업데이트됨
                remoteViews.setTextViewText(
                    widget.getScoreTextId(gridIndex),
                    score.toString()
                )
                
                Log.i(TAG, "partially update widget: $widgetId score=$score")
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
    }
    
    override suspend fun syncComponentState(context: Context) {
        val deviceState = DeviceCareComponentDataStore.loadData(context)
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, _) ->
                Log.i(TAG, "Sync widget state: $widgetId $deviceState")
                updateWidgetState(context, widgetId, deviceState)
            }
    }
    
    private suspend fun updateWidgetState(
        context: Context,
        widgetId: Int,
        data: DeviceState
    ) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[DeviceCarePreferenceKey.MemoryUsageRatio] = data.memoryUsageRatio
            pref[DeviceCarePreferenceKey.StorageUsageRatio] = data.storageUsageRatio
            pref[DeviceCarePreferenceKey.CpuLoad] = data.cpuLoad
            pref[DeviceCarePreferenceKey.TemperatureCelsius] = data.temperatureCelsius
        }
    }
}