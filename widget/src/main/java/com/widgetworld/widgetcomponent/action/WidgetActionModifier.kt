package com.widgetworld.widgetcomponent.action

import android.content.Context
import com.widgetworld.core.proto.modifier.WidgetModifier
import com.widgetworld.core.proto.modifier.clickAction
import com.widgetworld.core.widget.action.RunWidgetCallbackAction
import com.widgetworld.core.widget.action.WidgetActionCallback
import com.widgetworld.core.widget.action.WidgetActionParameters
import com.widgetworld.core.widget.action.widgetActionParametersOf

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
