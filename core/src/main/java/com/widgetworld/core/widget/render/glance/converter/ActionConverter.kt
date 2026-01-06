package com.widgetworld.core.widget.render.glance.converter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.glance.action.Action
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.action.actionStartService
import com.widgetworld.core.widget.action.bundleFromBytes
import com.widgetworld.core.proto.Action as ProtoAction

/**
 * Proto Action을 Glance Action으로 변환
 */
internal object ActionConverter {
    /**
     * Proto Action을 Glance Action으로 변환
     * @param protoAction Proto Action
     * @param context Context
     * @return Glance Action 또는 null
     */
    fun toGlanceAction(protoAction: ProtoAction, context: Context): Action? {
        if (!protoAction.hasComponent()) {
            return null
        }

        val component = protoAction.component
        val componentName = ComponentName(
            component.packageName,
            component.className
        )

        val intent = Intent().apply {
            this.component = componentName
            // Add intent extras from proto action
            protoAction.intentExtrasMap.forEach { (key, value) ->
                when (value.lowercase()) {
                    "true" -> putExtra(key, true)
                    "false" -> putExtra(key, false)
                    else -> {
                        // Try to parse as int, long, or keep as string
                        value.toLongOrNull()?.let { putExtra(key, it) }
                            ?: value.toIntOrNull()?.let { putExtra(key, it) }
                            ?: putExtra(key, value)
                    }
                }
            }
        }

        return when {
            protoAction.activity -> {
                actionStartActivity(intent)

            }

            protoAction.service -> {
                actionStartService(intent)
            }

            else -> {
                val paramByteArray = protoAction.actionParameters.toByteArray()
                val paramBundle = bundleFromBytes(paramByteArray)
                intent.putExtra("ParamExtras", paramBundle)
                intent.setAction("android.intent.action.test_widget")
                actionSendBroadcast(intent)
            }
        }
    }

    /**
     * Proto Action이 유효한지 확인
     */
    fun isValid(protoAction: ProtoAction): Boolean {
        return protoAction.hasComponent() &&
                (protoAction.activity || protoAction.service || protoAction.broadcastReceiver)
    }
}

