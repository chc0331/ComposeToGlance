package com.example.dsl.widget.renderer.strategy

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.widget.RemoteViewsCompat.setProgressBarIndeterminate
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.ContentScale
import com.example.dsl.R
import com.example.dsl.proto.ContentScale as ProtoContentScale
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceModifierBuilder
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.converter.ColorConverter
import com.example.dsl.widget.renderer.RemoteViewsBuilder

/**
 * Image 노드 렌더링 전략
 */
internal object ImageRenderStrategy {
    /**
     * Glance 전략
     */
    object Glance : GlanceRenderStrategy() {
        @Composable
        override fun renderGlance(
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

    /**
     * RemoteViews 전략 (일반 이미지)
     */
    object RemoteViews : RemoteViewsRenderStrategy() {
        override fun createRemoteViews(
            node: WidgetNode,
            context: RenderContext
        ): android.widget.RemoteViews? {
            if (!node.hasImage()) {
                return null
            }

            val imageProperty = node.image
            val viewProperty = imageProperty.viewProperty

            val viewId = viewProperty.viewId
            val remoteViews = android.widget.RemoteViews(context.context.packageName, R.layout.image_component, viewId)

            // ImageProvider 처리
            when {
                imageProperty.provider.hasDrawableResId() -> {
                    remoteViews.setImageViewResource(viewId, imageProperty.provider.drawableResId)
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
            RemoteViewsBuilder.applyViewProperties(remoteViews, viewId, viewProperty, context.context)

            return remoteViews
        }
    }

    /**
     * RemoteViews 전략 (애니메이션 이미지)
     */
    object AnimationRemoteViews : RemoteViewsRenderStrategy() {
        override fun createRemoteViews(
            node: WidgetNode,
            context: RenderContext
        ): android.widget.RemoteViews? {
            if (!node.hasImage()) {
                return null
            }
            
            val imageProperty = node.image
            val viewProperty = imageProperty.viewProperty
            
            val viewId = viewProperty.viewId
            val remoteViews = android.widget.RemoteViews(
                context.context.packageName,
                imageProperty.provider.drawableResId,
                viewId
            )
            remoteViews.setProgressBarIndeterminate(viewId, imageProperty.infiniteLoop)
            
            // ViewProperty 속성 적용
            RemoteViewsBuilder.applyViewProperties(remoteViews, viewId, viewProperty, context.context)
            return remoteViews
        }
    }
}

