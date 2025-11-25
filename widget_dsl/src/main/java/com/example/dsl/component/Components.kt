package com.example.dsl.component

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.example.dsl.WidgetScope
import com.example.dsl.builder.buttonProperty
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.cornerRadius
import com.example.dsl.builder.imageProperty
import com.example.dsl.builder.imageProviderFromBitmap
import com.example.dsl.builder.imageProviderFromDrawable
import com.example.dsl.builder.imageProviderFromUri
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.builder.progressProperty
import com.example.dsl.builder.spacerProperty
import com.example.dsl.builder.textContent
import com.example.dsl.builder.textProperty
import com.example.dsl.builder.viewProperty
import com.example.dsl.builder.wrapContentDimension
import com.example.dsl.proto.ContentScale
import com.example.dsl.proto.ContentScale.CONTENT_SCALE_FIT
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.FontWeight.FONT_WEIGHT_NORMAL
import com.example.dsl.proto.Padding
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.TextAlign.TEXT_ALIGN_START
import com.example.dsl.proto.WidgetNode


// ==================== 컴포넌트 DSL ====================

/**
 * Spacer 컴포넌트
 */
fun WidgetScope.Spacer(
    viewId: Int = nextViewId(),
    width: Dimension = wrapContentDimension,
    height: Dimension = wrapContentDimension
) {
    val spacerNode = WidgetNode.newBuilder()
        .setSpacer(
            spacerProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height
                )
            )
        )
        .build()

    addChild(spacerNode)
}

/**
 * Progress 컴포넌트
 */
fun WidgetScope.Progress(
    type: ProgressType = ProgressType.PROGRESS_TYPE_LINEAR,
    maxValue: Float = 100f,
    progressValue: Float = 0f,
    viewId: Int = nextViewId(),
    width: Dimension = matchParentDimension,
    height: Dimension = matchParentDimension,
    padding: Padding? = null,
    progressColor: Int = 0xFFFFFFFF.toInt(),
    backgroundColor: Int = 0xFFE0E0E0.toInt()
) {
    val progressNode = WidgetNode.newBuilder()
        .setProgress(
            progressProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding
                ),
                type = type,
                maxValue = maxValue,
                progressValue = progressValue,
                progressColor = colorProvider(color = color(progressColor)),
                backgroundColor = colorProvider(color = color(backgroundColor))
            )
        )
        .build()

    addChild(progressNode)
}

/**
 * Button 컴포넌트
 */
fun WidgetScope.Button(
    text: String,
    viewId: Int = nextViewId(),
    width: Dimension = wrapContentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    fontSize: Float = 14f,
    fontWeight: FontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
    textColor: Int = 0xFFFFFFFF.toInt(),
    backgroundColor: Int = 0xFF2196F3.toInt(),
    cornerRadius: Float? = null,
    maxLine: Int = 1
) {
    val buttonNode = WidgetNode.newBuilder()
        .setButton(
            buttonProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding,
                    cornerRadius = cornerRadius?.let { cornerRadius(it) }
                ),
                text = textContent(text),
                fontColor = colorProvider(color = color(textColor)),
                fontSize = fontSize,
                fontWeight = fontWeight,
                backgroundColor = colorProvider(color = color(backgroundColor)),
                maxLine = maxLine
            )
        )
        .build()

    addChild(buttonNode)
}

/**
 * Image 컴포넌트
 */
fun WidgetScope.Image(
    @DrawableRes drawableResId: Int? = null,
    uri: String? = null,
    bitmap: Bitmap? = null,
    viewId: Int = nextViewId(),
    width: Dimension = wrapContentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    contentScale: ContentScale = CONTENT_SCALE_FIT,
    tintColor: Int? = null,
    alpha: Float = 1f
) {
    val provider = when {
        drawableResId != null -> imageProviderFromDrawable(drawableResId)
        uri != null -> imageProviderFromUri(uri)
        bitmap != null -> imageProviderFromBitmap(bitmap)
        else -> throw IllegalArgumentException("Either drawableResId, uri, or bitmap must be provided")
    }

    val imageNode = WidgetNode.newBuilder()
        .setImage(
            imageProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding
                ),
                provider = provider,
                tintColor = tintColor?.let { color(it) },
                alpha = alpha,
                contentScale = contentScale
            )
        )
        .build()

    addChild(imageNode)
}

/**
 * Text 컴포넌트
 */
fun WidgetScope.Text(
    text: String,
    viewId: Int = nextViewId(),
    width: Dimension = wrapContentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    fontSize: Float = 14f,
    fontWeight: FontWeight = FONT_WEIGHT_NORMAL,
    textColor: Int = 0xFF000000.toInt(),
    textAlign: TextAlign = TEXT_ALIGN_START,
    maxLine: Int = 1
) {
    val textNode = WidgetNode.newBuilder()
        .setText(
            textProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding
                ),
                text = textContent(text),
                fontColor = colorProvider(color = color(textColor)),
                fontSize = fontSize,
                fontWeight = fontWeight,
                textAlign = textAlign,
                maxLine = maxLine
            )
        )
        .build()

    addChild(textNode)
}