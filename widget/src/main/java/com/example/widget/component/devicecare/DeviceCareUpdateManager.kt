package com.example.widget.component.devicecare

import android.content.Context
import com.example.widget.component.WidgetComponent
import com.example.widget.component.update.ComponentUpdateHelper
import com.example.widget.component.update.ComponentUpdateManager

object DeviceCareUpdateManager : ComponentUpdateManager<DeviceState> {
    override val widget: WidgetComponent
        get() = DeviceCareWidget()

    override suspend fun updateComponent(
        context: Context,
        data: DeviceState
    ) {
        val componentTag = DeviceCareWidget().getWidgetTag()
        val deviceCareComponents = ComponentUpdateHelper.findPlacedComponents(
            context,
            DeviceCareWidget().getWidgetTag()
        )
        deviceCareComponents.forEach { (widgetId, component) ->
            val deviceCareWidget =
                ComponentUpdateHelper.getComponentInstance(componentTag) as? DeviceCareWidget
                    ?: return@forEach

        }
    }
}