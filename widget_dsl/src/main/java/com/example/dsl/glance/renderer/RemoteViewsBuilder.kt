package com.example.dsl.glance.renderer

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.widget.RemoteViews
import com.example.dsl.glance.converter.ColorConverter
import com.example.dsl.glance.converter.PaddingConverter
import com.example.dsl.proto.ViewProperty

/**
 * RemoteViews를 생성하고 속성을 적용하는 유틸리티 클래스
 */
object RemoteViewsBuilder {
    /**
     * ViewProperty의 속성들을 RemoteViews에 적용
     */
    fun applyViewProperties(
        remoteViews: RemoteViews,
        viewId: Int,
        viewProperty: ViewProperty,
        context: Context
    ) {
        // Padding
        if (viewProperty.hasPadding() && !PaddingConverter.isEmpty(viewProperty.padding)) {
            val padding = PaddingConverter.toGlancePaddingValues(viewProperty.padding)
            val paddingStart = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                padding.start.value,
                context.resources.displayMetrics
            ).toInt()
            val paddingTop = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                padding.top.value,
                context.resources.displayMetrics
            ).toInt()
            val paddingEnd = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                padding.end.value,
                context.resources.displayMetrics
            ).toInt()
            val paddingBottom = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                padding.bottom.value,
                context.resources.displayMetrics
            ).toInt()
            remoteViews.setViewPadding(viewId, paddingStart, paddingTop, paddingEnd, paddingBottom)
        }

        // Background color
        if (viewProperty.hasBackgroundColor()) {
            val backgroundColor = ColorConverter.toGlanceColor(
                viewProperty.backgroundColor,
                context
            )
            remoteViews.setInt(viewId, "setBackgroundColor", backgroundColor.value.toInt())
        }

        // Click Action
        if (viewProperty.hasClickAction()) {
            val protoAction = viewProperty.clickAction
            if (protoAction.hasComponent()) {
                val component = protoAction.component
                val intent = Intent().apply {
                    setClassName(component.packageName, component.className)
                }
                val flags =
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
                val pendingIntent = if (protoAction.activity) {
                    android.app.PendingIntent.getActivity(context, 0, intent, flags)
                } else if (protoAction.service) {
                    android.app.PendingIntent.getService(context, 0, intent, flags)
                } else {
                    android.app.PendingIntent.getBroadcast(context, 0, intent, flags)
                }
                remoteViews.setOnClickPendingIntent(viewId, pendingIntent)
            }
        }
    }

    /**
     * DP 값을 픽셀로 변환
     */
    fun dpToPixels(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}

