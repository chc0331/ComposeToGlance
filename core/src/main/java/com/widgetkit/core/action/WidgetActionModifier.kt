package com.widgetkit.core.action

import android.content.Context
import com.widgetkit.dsl.action.RunWidgetCallbackAction
import com.widgetkit.dsl.action.WidgetActionCallback
import com.widgetkit.dsl.action.WidgetActionParameters
import com.widgetkit.dsl.action.widgetActionParametersOf
import com.widgetkit.dsl.dsl.modifier.WidgetModifier
import com.widgetkit.dsl.dsl.modifier.clickAction

fun WidgetModifier.runCallbackBroadcastReceiver(
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