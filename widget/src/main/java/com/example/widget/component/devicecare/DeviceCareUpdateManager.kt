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
                // GlanceAppWidgetState 업데이트
                updateWidgetState(context, widgetId, data)
                
                // 필요시 RemoteViews로 부분 업데이트 가능
                // val gridIndex = component.gridIndex
                // val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                // ... remoteViews 설정 ...
                // ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
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
            // DeviceCare의 preference keys를 사용하여 상태 업데이트
            // 필요시 PreferenceKey 정의 후 사용
        }
    }
}