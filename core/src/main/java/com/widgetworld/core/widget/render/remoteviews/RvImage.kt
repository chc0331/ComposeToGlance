package com.widgetworld.core.widget.render.remoteviews

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.AndroidRemoteViews
import com.widgetworld.core.R
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.converter.ColorConverter

internal object RvImage : RenderNode {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasImage()) {
            return
        }

        val imageProperty = node.image
        val viewProperty = imageProperty.viewProperty

        val viewId = viewProperty.viewId
        val remoteViews = android.widget.RemoteViews(
            context.context.packageName,
            R.layout.image_component,
            viewId
        )

        // ImageProvider 처리
        when {
            imageProperty.provider.hasDrawableResId() -> {
                remoteViews.setImageViewResource(
                    viewId,
                    imageProperty.provider.drawableResId
                )
            }

            imageProperty.provider.hasUri() -> {
                remoteViews.setImageViewUri(
                    viewId,
                    Uri.parse(imageProperty.provider.uri)
                )
            }

            imageProperty.provider.hasBitmap() -> {
                val byteArray = imageProperty.provider.bitmap.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                remoteViews.setImageViewBitmap(viewId, bitmap)
            }

            else -> {
                remoteViews.setImageViewResource(viewId, android.R.drawable.ic_menu_gallery)
            }
        }

        // Tint Color
        if (imageProperty.hasTintColor()) {
            val tintColor = ColorConverter.toGlanceColor(imageProperty.tintColor)
            remoteViews.setInt(viewId, "setColorFilter", tintColor.toArgb())
        }

        // Alpha
        if (imageProperty.alpha != 1f && imageProperty.alpha > 0f) {
            remoteViews.setFloat(viewId, "setAlpha", imageProperty.alpha)
        }

        // ViewProperty 속성 적용
        RemoteViewsBuilder.applyViewProperties(
            remoteViews,
            viewId,
            viewProperty,
            context.context,
            context.document.widgetMode
        )

        AndroidRemoteViews(remoteViews = remoteViews, modifier = GlanceModifier)
    }
}