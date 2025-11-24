package com.example.dsl

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.example.dsl.builder.boxLayoutProperty
import com.example.dsl.builder.buttonProperty
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.columnLayoutProperty
import com.example.dsl.builder.cornerRadius
import com.example.dsl.builder.imageProperty
import com.example.dsl.builder.imageProviderFromBitmap
import com.example.dsl.builder.imageProviderFromDrawable
import com.example.dsl.builder.imageProviderFromUri
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.builder.progressProperty
import com.example.dsl.builder.rowLayoutProperty
import com.example.dsl.builder.spacerProperty
import com.example.dsl.builder.textContent
import com.example.dsl.builder.textProperty
import com.example.dsl.builder.viewProperty
import com.example.dsl.builder.wrapContentDimension
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.ContentScale
import com.example.dsl.proto.ContentScale.CONTENT_SCALE_FIT
import com.example.dsl.proto.Dimension
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.FontWeight.FONT_WEIGHT_NORMAL
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_START
import com.example.dsl.proto.Padding
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.TextAlign.TEXT_ALIGN_START
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_TOP
import com.example.dsl.proto.WidgetLayoutDocument
import com.example.dsl.proto.WidgetNode

/**
 * Widget DSL - Compose 스타일의 선언적 API
 *
 * 사용 예시:
 * ```
 * val layout = widgetLayout {
 *     column(horizontalAlignment = H_ALIGN_CENTER) {
 *         text("Hello", fontSize = 18f, fontWeight = FONT_WEIGHT_BOLD)
 *         text("World", fontSize = 14f)
 *     }
 * }
 * ```
 */

// ==================== 최상위 함수 ====================

/**
 * WidgetLayoutDocument를 생성하는 최상위 DSL 함수
 */
fun WidgetLayout(block: WidgetScope.() -> Unit): WidgetLayoutDocument {
    val scope = WidgetScope()
    scope.block()
    return WidgetLayoutDocument.newBuilder()
        .setRoot(scope.build())
        .build()
}

// ==================== Scope 클래스 ====================

/**
 * Widget DSL의 스코프
 * 자식 노드를 관리하고 최종 WidgetNode를 생성
 */
class WidgetScope {
    private var viewIdCounter = 0
    internal val children = mutableListOf<WidgetNode>()
    private val locals = mutableMapOf<WidgetLocal<out Any?>, Any?>()

    /**
     * 다음 viewId를 생성
     */
    fun nextViewId(): Int = viewIdCounter++

    /**
     * 자식 노드 추가
     */
    fun addChild(node: WidgetNode) {
        children.add(node)
    }

    /**
     * CompositionLocal 값을 설정.
     * */
    fun <T> setLocal(key: WidgetLocal<T>, value: T) {
        locals[key] = value
    }

    /**
     * CompositionLocal 값을 가져옴.
     * */
    fun <T> getLocal(key: WidgetLocal<T>): T? {
        return locals[key] as? T ?: key.getDefaultValue()
    }

    /**
     * 부모 스코프의 locals를 복사.
     * */
    internal fun copyLocalsFrom(parent: WidgetScope) {
        parent.locals.forEach { (key, value) ->
            locals[key as WidgetLocal<out Any?>] = value
        }
    }

    /**
     * 최종 WidgetNode 빌드 (단일 루트 노드인 경우)
     */
    fun build(): WidgetNode {
        require(children.size == 1) { "WidgetScope must have exactly one root node" }
        return children[0]
    }

    /**
     * 여러 자식 노드를 가진 컨테이너 빌드
     */
    fun buildContainer(
        setter: (WidgetNode.Builder, List<WidgetNode>) -> Unit
    ): WidgetNode {
        val builder = WidgetNode.newBuilder()
        setter(builder, children)
        return builder.build()
    }
}

// ==================== 레이아웃 DSL ====================

/**
 * Column 레이아웃
 */
fun WidgetScope.Column(
    viewId: Int = nextViewId(),
    width: Dimension = matchParentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    horizontalAlignment: HorizontalAlignment = H_ALIGN_START,
    verticalAlignment: VerticalAlignment = V_ALIGN_TOP,
    backgroundColor: ColorProvider? = null,
    block: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.block()

    val columnNode = WidgetNode.newBuilder()
        .setColumn(
            columnLayoutProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding,
                    backgroundColor = backgroundColor
                ),
                horizontalAlignment = horizontalAlignment,
                verticalAlignment = verticalAlignment
            )
        )
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(columnNode)
}

/**
 * Row 레이아웃
 */
fun WidgetScope.Row(
    viewId: Int = nextViewId(),
    width: Dimension = matchParentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    horizontalAlignment: HorizontalAlignment = H_ALIGN_START,
    verticalAlignment: VerticalAlignment = V_ALIGN_TOP,
    backgroundColor: ColorProvider? = null,
    block: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.block()

    val rowNode = WidgetNode.newBuilder()
        .setRow(
            rowLayoutProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding,
                    backgroundColor = backgroundColor
                ),
                horizontalAlignment = horizontalAlignment,
                verticalAlignment = verticalAlignment
            )
        )
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(rowNode)
}

/**
 * Box 레이아웃
 */
fun WidgetScope.Box(
    viewId: Int = nextViewId(),
    width: Dimension = matchParentDimension,
    height: Dimension = wrapContentDimension,
    padding: Padding? = null,
    alignment: AlignmentType = AlignmentType.ALIGNMENT_TYPE_TOP_START,
    backgroundColor: ColorProvider? = null,
    block: WidgetScope.() -> Unit
) {
    val childScope = WidgetScope()
    childScope.copyLocalsFrom(this)
    childScope.block()
    val boxNode = WidgetNode.newBuilder()
        .setBox(
            boxLayoutProperty(
                viewProperty = viewProperty(
                    viewId = viewId,
                    width = width,
                    height = height,
                    padding = padding,
                    backgroundColor = backgroundColor
                ),
                alignment = alignment
            )
        )
        .apply {
            childScope.children.forEach { addChildren(it) }
        }
        .build()

    addChild(boxNode)
}

// ==================== 컴포넌트 DSL ====================

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

// ==================== 편의 확장 함수 ====================

/**
 * Padding을 간단하게 생성하는 확장 함수
 * WidgetScope에서 사용할 수 있는 편의 함수
 */
fun WidgetScope.padding(
    all: Float? = null,
    horizontal: Float? = null,
    vertical: Float? = null,
    start: Float = 0f,
    top: Float = 0f,
    end: Float = 0f,
    bottom: Float = 0f
): Padding {
    val finalStart = all ?: horizontal ?: start
    val finalTop = all ?: vertical ?: top
    val finalEnd = all ?: horizontal ?: end
    val finalBottom = all ?: vertical ?: bottom

    return padding(
        start = finalStart,
        top = finalTop,
        end = finalEnd,
        bottom = finalBottom
    )
}
