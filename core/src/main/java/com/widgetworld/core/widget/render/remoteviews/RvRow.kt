package com.widgetworld.core.widget.render.remoteviews

import android.R
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RemoteViews
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.proto.HorizontalAlignment
import com.widgetworld.core.proto.VerticalAlignment
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.render.glance.render.GlanceRow

/**
 * RowRenderer의 RemoteViews 확장 함수
 */
internal fun GlanceRow.renderToRemoteViews(
    node: WidgetNode,
    context: Context,
    renderer: WidgetRenderer
): RemoteViews? {
    if (!node.hasRow()) {
        return null
    }

    val rowProperty = node.row
    val viewProperty = rowProperty.viewProperty

    // RemoteViews 생성 (LinearLayout - horizontal을 위한 간단한 레이아웃)
    val remoteViews = RemoteViews(context.packageName, R.layout.simple_list_item_1)
    val containerId = R.id.text1

    // LinearLayout 속성 설정
    remoteViews.setInt(containerId, "setOrientation", LinearLayout.HORIZONTAL)

    // Gravity 설정
    val gravity = when {
        rowProperty.horizontalAlignment == HorizontalAlignment.H_ALIGN_CENTER &&
                rowProperty.verticalAlignment == VerticalAlignment.V_ALIGN_CENTER -> Gravity.CENTER
        rowProperty.horizontalAlignment == HorizontalAlignment.H_ALIGN_CENTER -> Gravity.CENTER_HORIZONTAL
        rowProperty.verticalAlignment == VerticalAlignment.V_ALIGN_CENTER -> Gravity.CENTER_VERTICAL
        rowProperty.verticalAlignment == VerticalAlignment.V_ALIGN_BOTTOM -> Gravity.BOTTOM
        else -> Gravity.START or Gravity.TOP
    }
    remoteViews.setInt(containerId, "setGravity", gravity)

    // 자식 노드들을 렌더링하고 추가
    val children = node.childrenList
//    children.forEachIndexed { index, child ->
//        val childRemoteViews = renderer.renderNodeToRemoteViews(child, context)
//        if (childRemoteViews != null) {
//            remoteViews.addView(containerId, childRemoteViews)
//        }
//    }

    // ViewProperty 속성 적용
    RemoteViewsBuilder.applyViewProperties(remoteViews, containerId, viewProperty, context)

    return remoteViews
}

