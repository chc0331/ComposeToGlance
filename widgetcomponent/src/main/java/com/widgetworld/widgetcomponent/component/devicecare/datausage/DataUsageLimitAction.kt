package com.widgetworld.widgetcomponent.component.devicecare.datausage

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.GlanceId
import com.widgetworld.widgetcomponent.action.WidgetActionVisibleActivity
import com.widgetworld.core.widget.action.WidgetActionCallback
import com.widgetworld.core.widget.action.WidgetActionParameters

/**
 * 데이터 사용량 Limit 변경 액션 처리
 */
class DataUsageLimitAction : WidgetActionCallback {

    companion object {
        const val TAG = "DataUsageLimitAction"
        const val PARAM_LIMIT_TYPE = "limitType" // "wifi" or "mobile"
        const val PARAM_WIDGET_ID = "widgetId"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: WidgetActionParameters
    ) {
        Log.d(TAG, "Data usage limit button clicked")
        
        val limitType = parameters[WidgetActionParameters.Key<String>(PARAM_LIMIT_TYPE)]
        val widgetId = parameters[WidgetActionParameters.Key<Int>(PARAM_WIDGET_ID)]
        
        if (limitType == null) {
            Log.e(TAG, "Limit type not found in parameters")
            return
        }
        
        if (widgetId == null) {
            Log.e(TAG, "Widget ID not found in parameters")
            return
        }
        
        Log.d(TAG, "Opening limit dialog for type: $limitType, widget: $widgetId")
        
        // Limit 변경 다이얼로그 Activity 시작
        val intent = Intent(context, DataUsageLimitDialogActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(PARAM_LIMIT_TYPE, limitType)
            putExtra(PARAM_WIDGET_ID, widgetId)
        }
        context.startActivity(intent)
    }
}

