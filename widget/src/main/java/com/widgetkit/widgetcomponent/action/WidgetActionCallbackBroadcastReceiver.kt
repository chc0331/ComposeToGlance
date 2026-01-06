package com.widgetkit.widgetcomponent.action

import android.content.Context
import android.content.Intent
import android.util.Log
import com.widgetkit.core.widget.action.WidgetActionCallbackBroadcastReceiver

internal class CustomWidgetActionCallbackBroadcastReceiver :
    WidgetActionCallbackBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive : ${intent.action}")
        super.onReceive(context, intent)
    }

    companion object {
        private const val TAG = "CustomWidgetActionCallbackBroadcastReceiver"
    }
}
