package com.widgetkit.dsl.widget.remoteviews

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.AndroidRemoteViews
import com.widgetkit.dsl.R
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.rendernode.NodeRenderer
import com.widgetkit.dsl.widget.rendernode.RenderContext
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.glance.converter.ColorConverter

internal object RvImage : NodeRenderer {

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
            context.context
        )

        AndroidRemoteViews(remoteViews = remoteViews, modifier = GlanceModifier)
    }
}