package com.example.dsl.widget.converter

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.glance.action.Action
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.action.actionStartService
import com.example.dsl.proto.Action as ProtoAction

/**
 * Proto Action을 Glance Action으로 변환
 */
object ActionConverter {
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
        }

        return when {
            protoAction.activity -> {
                actionStartActivity(intent)
            }

            protoAction.service -> {
                actionStartService(intent)
            }

            else -> {
                // 기본값은 Activity
                actionStartActivity(intent)
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

