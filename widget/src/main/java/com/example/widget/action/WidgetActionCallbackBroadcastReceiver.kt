package com.example.widget.action

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.dsl.action.WidgetActionCallbackBroadcastReceiver


internal class CustomWidgetActionCallbackBroadcastReceiver :
    WidgetActionCallbackBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i("heec.choi","onReceive : ${intent.action}")
        super.onReceive(context, intent)
    }
}