package com.example.widget.action

import android.content.Context
import com.example.dsl.action.RunWidgetCallbackAction
import com.example.dsl.action.WidgetActionCallback
import com.example.dsl.action.WidgetActionParameters
import com.example.dsl.action.widgetActionParametersOf
import com.example.dsl.modifier.WidgetModifier
import com.example.dsl.modifier.clickAction

fun WidgetModifier.runCallbackAction(
    context: Context,
    widgetId: Int,
    action: WidgetActionCallback
) = this.clickAction(
    context, RunWidgetCallbackAction(
        CustomWidgetActionCallbackBroadcastReceiver::class.java,
        action::class.java, widgetActionParametersOf(
            WidgetActionParameters.Key<String>("actionClass") to action::class.java.canonicalName,
            WidgetActionParameters.Key<Int>("widgetId") to widgetId
        )
    )
)