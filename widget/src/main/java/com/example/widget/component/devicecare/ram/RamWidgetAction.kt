package com.example.widget.component.devicecare.ram

import android.content.Context
import android.util.Log
import androidx.glance.GlanceId
import com.example.dsl.action.RunWidgetCallbackAction
import com.example.dsl.action.WidgetActionCallback
import com.example.dsl.action.WidgetActionParameters

class RamWidgetAction : WidgetActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: WidgetActionParameters
    ) {
        Log.i("heec.choi","onAction $glanceId")
    }
}