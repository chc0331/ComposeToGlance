package com.example.dsl.widget.remoteviews

import android.R
import android.content.Context
import android.view.View
import android.widget.RemoteViews
import com.example.dsl.widget.WidgetRenderer
import com.example.dsl.widget.glance.render.GlanceSpacer
import com.example.dsl.proto.WidgetNode

/**
 * SpacerRenderer의 RemoteViews 확장 함수
 */
internal fun GlanceSpacer.renderToRemoteViews(
    node: WidgetNode,
    context: Context,
    renderer: WidgetRenderer
): RemoteViews? {
    if (!node.hasSpacer()) {
        return null
    }

    val spacerProperty = node.spacer
    val viewProperty = spacerProperty.viewProperty

    // RemoteViews 생성 (빈 View)
    val remoteViews = RemoteViews(context.packageName, R.layout.simple_list_item_1)
    val spacerId = R.id.text1

    // View를 보이지 않게 설정 (Spacer는 공간만 차지)
    remoteViews.setViewVisibility(spacerId, View.GONE)

    // ViewProperty 속성 적용 (width, height 등)
    RemoteViewsBuilder.applyViewProperties(remoteViews, spacerId, viewProperty, context)

    return remoteViews
}

