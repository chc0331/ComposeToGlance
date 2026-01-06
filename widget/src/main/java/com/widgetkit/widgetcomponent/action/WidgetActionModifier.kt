package com.widgetkit.widgetcomponent.action

import android.content.Context
import com.widgetkit.core.proto.modifier.WidgetModifier
import com.widgetkit.core.proto.modifier.clickAction
import com.widgetkit.core.widget.action.RunWidgetCallbackAction
import com.widgetkit.core.widget.action.WidgetActionCallback
import com.widgetkit.core.widget.action.WidgetActionParameters
import com.widgetkit.core.widget.action.widgetActionParametersOf

fun WidgetModifier.runCallbackBroadcastReceiver(
    context: Context,
    widgetId: Int,
    action: WidgetActionCallback
) = this.clickAction(
    context,
    RunWidgetCallbackAction(
        CustomWidgetActionCallbackBroadcastReceiver::class.java,
        action::class.java,
        widgetActionParametersOf(
            WidgetActionParameters.Key<String>("actionClass") to action::class.java.canonicalName,
            WidgetActionParameters.Key<Int>("widgetId") to widgetId
        )
    )
)
