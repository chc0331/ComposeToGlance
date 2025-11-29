package com.example.dsl.glance.renderer.remoteviews

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.RemoteViews
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
    renderer: GlanceRenderer
): RemoteViews? {
    if (!node.hasImage()) {
        return null
    }

    val imageProperty = node.image
    val viewProperty = imageProperty.viewProperty

    // RemoteViews 생성 (ImageView를 위한 간단한 레이아웃)
    // RemoteViews는 단일 뷰를 직접 생성할 수 없으므로 레이아웃이 필요
    // simple_list_item_1을 사용하고 text1을 ImageView로 사용
    val remoteViews = RemoteViews(context.packageName, android.R.layout.simple_list_item_1)
    val imageViewId = android.R.id.text1

    // ImageProvider 처리
    when {
        imageProperty.provider.hasDrawableResId() -> {
            remoteViews.setImageViewResource(imageViewId, imageProperty.provider.drawableResId)
        }
        imageProperty.provider.hasUri() -> {
            remoteViews.setImageViewUri(imageViewId, android.net.Uri.parse(imageProperty.provider.uri))
        }
        imageProperty.provider.hasBitmap() -> {
            val byteArray = imageProperty.provider.bitmap.toByteArray()
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            remoteViews.setImageViewBitmap(imageViewId, bitmap)
        }
        else -> {
            // 기본 이미지
            remoteViews.setImageViewResource(imageViewId, android.R.drawable.ic_menu_gallery)
        }
    }

    // Tint Color
    if (imageProperty.hasTintColor()) {
        // tintColor는 Color 타입이므로 직접 변환
        val tintColor = ColorConverter.toGlanceColor(imageProperty.tintColor)
        remoteViews.setInt(imageViewId, "setColorFilter", tintColor.value.toInt())
    }

    // Alpha
    if (imageProperty.alpha != 1f && imageProperty.alpha > 0f) {
        remoteViews.setFloat(imageViewId, "setAlpha", imageProperty.alpha)
    }

    // ContentScale (ScaleType)
    val scaleType = when (imageProperty.contentScale) {
        ContentScale.CONTENT_SCALE_FIT -> android.widget.ImageView.ScaleType.FIT_CENTER
        ContentScale.CONTENT_SCALE_CROP -> android.widget.ImageView.ScaleType.CENTER_CROP
        ContentScale.CONTENT_SCALE_FILL_BOUNDS -> android.widget.ImageView.ScaleType.FIT_XY
        else -> android.widget.ImageView.ScaleType.FIT_CENTER
    }
    remoteViews.setInt(imageViewId, "setScaleType", scaleType.ordinal)

    // ViewProperty 속성 적용
    RemoteViewsBuilder.applyViewProperties(remoteViews, imageViewId, viewProperty, context)

    return remoteViews
}

