package com.example.dsl.widget.remoteviews

import android.R
import android.content.Context
import android.view.Gravity
import android.widget.RemoteViews
import com.example.dsl.widget.WidgetRenderer
import com.example.dsl.widget.glance.render.GlanceBox
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.WidgetNode

/**
 * BoxRenderer의 RemoteViews 확장 함수
 */
internal fun GlanceBox.renderToRemoteViews(
    node: WidgetNode,
    context: Context,
    renderer: WidgetRenderer
): RemoteViews? {
    if (!node.hasBox()) {
        return null
    }

    val boxProperty = node.box
    val viewProperty = boxProperty.viewProperty

    // RemoteViews 생성 (FrameLayout을 위한 간단한 레이아웃)
    // android.R.layout.simple_list_item_1을 사용하고 content를 container로 사용
    val remoteViews = RemoteViews(context.packageName, R.layout.simple_list_item_1)
    val containerId = R.id.text1

    // Alignment를 Gravity로 변환
    val gravity = when (boxProperty.contentAlignment) {
        AlignmentType.ALIGNMENT_TYPE_TOP_START -> Gravity.TOP or Gravity.START
        AlignmentType.ALIGNMENT_TYPE_TOP_CENTER -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
        AlignmentType.ALIGNMENT_TYPE_TOP_END -> Gravity.TOP or Gravity.END
        AlignmentType.ALIGNMENT_TYPE_CENTER_START -> Gravity.CENTER_VERTICAL or Gravity.START
        AlignmentType.ALIGNMENT_TYPE_CENTER -> Gravity.CENTER
        AlignmentType.ALIGNMENT_TYPE_CENTER_END -> Gravity.CENTER_VERTICAL or Gravity.END
        AlignmentType.ALIGNMENT_TYPE_BOTTOM_START -> Gravity.BOTTOM or Gravity.START
        AlignmentType.ALIGNMENT_TYPE_BOTTOM_CENTER -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        AlignmentType.ALIGNMENT_TYPE_BOTTOM_END -> Gravity.BOTTOM or Gravity.END
        AlignmentType.ALIGNMENT_TYPE_TOP -> Gravity.TOP
        AlignmentType.ALIGNMENT_TYPE_CENTER_VERTICAL -> Gravity.CENTER_VERTICAL
        AlignmentType.ALIGNMENT_TYPE_BOTTOM -> Gravity.BOTTOM
        AlignmentType.ALIGNMENT_TYPE_START -> Gravity.START
        AlignmentType.ALIGNMENT_TYPE_CENTER_HORIZONTAL -> Gravity.CENTER_HORIZONTAL
        AlignmentType.ALIGNMENT_TYPE_END -> Gravity.END
        else -> Gravity.START
    }

    // 자식 노드들을 렌더링하고 추가
//    val children = node.childrenList
//    children.forEachIndexed { index, child ->
//        val childRemoteViews = renderer.renderNodeToRemoteViews(child, context)
//        if (childRemoteViews != null) {
//            // 자식 RemoteViews를 추가 (FrameLayout에 추가)
//            remoteViews.addView(containerId, childRemoteViews)
//        }
//    }

    // ViewProperty 속성 적용
    RemoteViewsBuilder.applyViewProperties(remoteViews, containerId, viewProperty, context)

    return remoteViews
}

