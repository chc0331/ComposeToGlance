package com.example.dsl.widget.renderer

import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import com.example.dsl.widget.converter.ColorConverter
import com.example.dsl.widget.converter.PaddingConverter
import com.example.dsl.proto.ViewProperty

/**
 * RemoteViews를 생성하고 속성을 적용하는 유틸리티 클래스
 */
internal object RemoteViewsBuilder {
    /**
     * ViewProperty의 속성들을 RemoteViews에 적용
     */
    fun applyViewProperties(
        remoteViews: RemoteViews,
        viewId: Int,
        viewProperty: ViewProperty,
        context: Context
    ) {
        if (viewProperty.width.dp.value != 0f) {
            remoteViews.setViewLayoutWidth(
                viewId,
                viewProperty.width.dp.value,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else if (viewProperty.width.matchParent) {
            Log.i("heec.choi","View id : $viewId")
            remoteViews.setViewLayoutWidth(
                viewId,
                ViewGroup.LayoutParams.MATCH_PARENT.toFloat(), // -1.0f
                TypedValue.COMPLEX_UNIT_PX
            )
        }
        if (viewProperty.height.dp.value != 0f) {
            remoteViews.setViewLayoutHeight(
                viewId,
                viewProperty.height.dp.value,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else if (viewProperty.height.matchParent) {
            remoteViews.setViewLayoutHeight(
                viewId,
                ViewGroup.LayoutParams.MATCH_PARENT.toFloat(), // -1.0f
                TypedValue.COMPLEX_UNIT_PX
            )
        }

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

        if (viewProperty.hide) remoteViews.setViewVisibility(viewId, View.GONE)
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

