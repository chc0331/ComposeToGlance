package com.widgetworld.core.widget.render.glance.render

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.glance.ColorFilter
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.unit.ColorProvider
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.node.RenderContext
import com.widgetworld.core.widget.node.RenderNode
import com.widgetworld.core.widget.render.glance.GlanceModifierBuilder
import com.widgetworld.core.widget.render.glance.converter.ColorConverter

/**
 * Image 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
internal object GlanceImage : RenderNode {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasImage()) {
            Box {}
            return
        }

        val imageProperty = node.image
        val viewProperty = imageProperty.viewProperty

        // Modifier 생성
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context)
            .then(context.modifier)

        // ImageProvider 생성
        val imageProvider = when {
            imageProperty.provider.hasDrawableResId() -> {
                ImageProvider(imageProperty.provider.drawableResId)
            }

            imageProperty.provider.hasUri() -> {
                androidx.glance.appwidget.ImageProvider(Uri.parse(imageProperty.provider.uri))
            }

            imageProperty.provider.hasBitmap() -> {
                val byteArray = imageProperty.provider.bitmap.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                ImageProvider(bitmap = bitmap)
            }

            else -> {
                ImageProvider(android.R.drawable.ic_menu_gallery)
            }
        }

        // ContentScale
        val contentScale = toGlanceContentScale(imageProperty.contentScale)

        // Alpha (Glance는 alpha를 modifier로 지원)
        val finalModifier = if (imageProperty.alpha != 1f && imageProperty.alpha > 0f) {
            modifier
        } else {
            modifier
        }

        // Tint Color (Glance는 colorFilter로 지원)
        val colorFilter = if (imageProperty.hasTintColor()) {
            ColorFilter.tint(
                ColorProvider(
                    ColorConverter.toGlanceColor(imageProperty.tintColor)
                )
            )
        } else {
            null
        }

        Image(
            provider = imageProvider,
            contentDescription = viewProperty.semantics?.contentDescription,
            modifier = finalModifier,
            contentScale = contentScale,
            colorFilter = colorFilter
        )
    }

    private fun toGlanceContentScale(protoScale: com.widgetworld.core.proto.ContentScale): ContentScale {
        return when (protoScale) {
            com.widgetworld.core.proto.ContentScale.CONTENT_SCALE_FIT -> ContentScale.Fit
            com.widgetworld.core.proto.ContentScale.CONTENT_SCALE_CROP -> ContentScale.Crop
            com.widgetworld.core.proto.ContentScale.CONTENT_SCALE_FILL_BOUNDS -> ContentScale.FillBounds
            else -> ContentScale.Fit
        }
    }
}