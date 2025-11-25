package com.example.dsl.glance.renderer

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.ContentScale
import com.example.dsl.proto.ContentScale as ProtoContentScale
import com.example.dsl.proto.WidgetNode
import com.example.dsl.glance.GlanceModifierBuilder
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.glance.RenderContext
import com.example.dsl.glance.converter.ColorConverter

/**
 * Image 노드 렌더러
 */
object ImageRenderer : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        if (!node.hasImage()) {
            androidx.glance.layout.Box {}
            return
        }

        val imageProperty = node.image
        val viewProperty = imageProperty.viewProperty

        // Modifier 생성
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
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
                // Bitmap은 Glance에서 직접 지원하지 않으므로 변환 필요
                // 여기서는 기본 이미지 제공자 반환
                val byteArray = imageProperty.provider.bitmap.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                ImageProvider(bitmap = bitmap)
            }

            else -> {
                // 기본 이미지
                ImageProvider(android.R.drawable.ic_menu_gallery)
            }
        }

        // ContentScale
        val contentScale = toGlanceContentScale(imageProperty.contentScale)

        // Alpha (Glance는 alpha를 modifier로 지원)
        val finalModifier = if (imageProperty.alpha != 1f && imageProperty.alpha > 0f) {
//            modifier.then(androidx.glance.GlanceModifier.alpha(imageProperty.alpha))
            modifier
        } else {
            modifier
        }

        // Tint Color (Glance는 colorFilter로 지원)
        val colorFilter = if (imageProperty.hasTintColor()) {
            androidx.glance.ColorFilter.tint(
                androidx.glance.unit.ColorProvider(
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

    private fun toGlanceContentScale(protoScale: ProtoContentScale): ContentScale {
        return when (protoScale) {
            ProtoContentScale.CONTENT_SCALE_FIT -> ContentScale.Fit
            ProtoContentScale.CONTENT_SCALE_CROP -> ContentScale.Crop
            ProtoContentScale.CONTENT_SCALE_FILL_BOUNDS -> ContentScale.FillBounds
            else -> ContentScale.Fit
        }
    }
}

