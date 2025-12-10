package com.example.dsl.widget.renderer.remoteviews

import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.RemoteViews
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.renderer.ColumnRenderer
import com.example.dsl.widget.renderer.RemoteViewsBuilder
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.WidgetNode

/**
 * ColumnRenderer의 RemoteViews 확장 함수
 */
fun ColumnRenderer.renderToRemoteViews(
    node: WidgetNode,
    context: Context,
    renderer: GlanceRenderer
): RemoteViews? {
    if (!node.hasColumn()) {
        return null
    }

    val columnProperty = node.column
    val viewProperty = columnProperty.viewProperty

    // RemoteViews 생성 (LinearLayout - vertical을 위한 간단한 레이아웃)
    val remoteViews = RemoteViews(context.packageName, android.R.layout.simple_list_item_1)
    val containerId = android.R.id.text1

    // LinearLayout 속성 설정
    remoteViews.setInt(containerId, "setOrientation", LinearLayout.VERTICAL)

    // Gravity 설정
    val gravity = when {
        columnProperty.horizontalAlignment == HorizontalAlignment.H_ALIGN_CENTER &&
                columnProperty.verticalAlignment == VerticalAlignment.V_ALIGN_CENTER -> Gravity.CENTER
        columnProperty.horizontalAlignment == HorizontalAlignment.H_ALIGN_CENTER -> Gravity.CENTER_HORIZONTAL
        columnProperty.verticalAlignment == VerticalAlignment.V_ALIGN_CENTER -> Gravity.CENTER_VERTICAL
        columnProperty.horizontalAlignment == HorizontalAlignment.H_ALIGN_END -> Gravity.END
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

