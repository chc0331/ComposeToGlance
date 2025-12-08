package com.example.dsl.glance.renderer.remoteviews

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.R
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.glance.converter.ColorConverter
import com.example.dsl.glance.renderer.ImageRenderer
import com.example.dsl.glance.renderer.RemoteViewsBuilder
import com.example.dsl.proto.ContentScale
import com.example.dsl.proto.WidgetNode

/**
 * ImageRenderer의 RemoteViews 확장 함수
 */
fun ImageRenderer.renderToRemoteViews(
    node: WidgetNode,
    context: Context,
): RemoteViews? {
    if (!node.hasImage()) {
        return null
    }

    val imageProperty = node.image
    val viewProperty = imageProperty.viewProperty

    if (imageProperty.animation) {
        return renderToAnimationRemoteViews(node, context)
    }

    val viewId = viewProperty.viewId
    val remoteViews = RemoteViews(context.packageName, R.layout.image_component, viewId)

    // ImageProvider 처리
    when {
        imageProperty.provider.hasDrawableResId() -> {
            remoteViews.setImageViewResource(viewId, imageProperty.provider.drawableResId)
        }

        imageProperty.provider.hasUri() -> {
            remoteViews.setImageViewUri(
                viewId,
                android.net.Uri.parse(imageProperty.provider.uri)
            )
        }

        imageProperty.provider.hasBitmap() -> {
            val byteArray = imageProperty.provider.bitmap.toByteArray()
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            remoteViews.setImageViewBitmap(viewId, bitmap)
        }

        else -> {
            // 기본 이미지
            remoteViews.setImageViewResource(viewId, android.R.drawable.ic_menu_gallery)
        }
    }

    // Tint Color
    if (imageProperty.hasTintColor()) {
        // tintColor는 Color 타입이므로 직접 변환
        val tintColor = ColorConverter.toGlanceColor(imageProperty.tintColor)
        remoteViews.setInt(viewId, "setColorFilter", tintColor.toArgb())
    }

    // Alpha
    if (imageProperty.alpha != 1f && imageProperty.alpha > 0f) {
        remoteViews.setFloat(viewId, "setAlpha", imageProperty.alpha)
    }

    // ViewProperty 속성 적용
    RemoteViewsBuilder.applyViewProperties(remoteViews, viewId, viewProperty, context)

    return remoteViews
}

