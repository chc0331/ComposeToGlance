package com.example.dsl.component

import com.example.dsl.WidgetScope
import com.example.dsl.proto.WidgetNode


// ==================== 컴포넌트 DSL (중첩 DSL 빌더 패턴) ====================

/**
 * Text 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시:
 * ```
 * Text({
 *     viewProperty {
 *         Width { wrapContent = true }
 *         Height { wrapContent = true }
 *     }
 *     text = "Hello World"
 *     fontSize = 18f
 *     fontWeight = FontWeight.FONT_WEIGHT_BOLD
 *     fontColor {
 *         color {
 *             argb = Color.Black.toArgb()
 *         }
 *     }
 *     textAlign = TextAlign.TEXT_ALIGN_CENTER
 * })
 * ```
 */
fun WidgetScope.Text(block: TextDsl.() -> Unit) {
    val dsl = TextDsl(this)
    dsl.block()
    val textNode = WidgetNode.newBuilder()
        .setText(dsl.build())
        .build()
    addChild(textNode)
}

/**
 * Image 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시:
 * ```
 * Image({
 *     viewProperty {
 *         Width { wrapContent = true }
 *         Height { wrapContent = true }
 *     }
 *     provider {
 *         drawableResId = R.drawable.example_image
 *     }
 *     contentScale = ContentScale.CONTENT_SCALE_FIT
 * })
 * ```
 */
fun WidgetScope.Image(block: ImageDsl.() -> Unit) {
    val dsl = ImageDsl(this)
    dsl.block()
    val imageNode = WidgetNode.newBuilder()
        .setImage(dsl.build())
        .build()
    addChild(imageNode)
}

/**
 * Button 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시:
 * ```
 * Button({
 *     viewProperty {
 *         Width { wrapContent = true }
 *         Height { wrapContent = true }
 *     }
 *     text = "Click Me"
 *     fontSize = 16f
 *     fontWeight = FontWeight.FONT_WEIGHT_BOLD
 *     fontColor {
 *         color {
 *             argb = Color.White.toArgb()
 *         }
 *     }
 *     backgroundColor {
 *         color {
 *             argb = Color.Blue.toArgb()
 *         }
 *     }
 * })
 * ```
 */
fun WidgetScope.Button(block: ButtonDsl.() -> Unit) {
    val dsl = ButtonDsl(this)
    dsl.block()
    val buttonNode = WidgetNode.newBuilder()
        .setButton(dsl.build())
        .build()
    addChild(buttonNode)
}

/**
 * Progress 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시:
 * ```
 * Progress({
 *     viewProperty {
 *         Width { matchParent = true }
 *         Height { matchParent = true }
 *     }
 *     progressType = ProgressType.PROGRESS_TYPE_LINEAR
 *     maxValue = 100f
 *     progressValue = 65f
 *     progressColor {
 *         color {
 *             argb = Color.Blue.toArgb()
 *         }
 *     }
 *     backgroundColor {
 *         color {
 *             argb = Color.LightGray.toArgb()
 *         }
 *     }
 * })
 * ```
 */
fun WidgetScope.Progress(block: ProgressDsl.() -> Unit) {
    val dsl = ProgressDsl(this)
    dsl.block()
    val progressNode = WidgetNode.newBuilder()
        .setProgress(dsl.build())
        .build()
    addChild(progressNode)
}

/**
 * Spacer 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시:
 * ```
 * Spacer({
 *     viewProperty {
 *         Width { wrapContent = true }
 *         Height { wrapContent = true }
 *     }
 * })
 * ```
 */
fun WidgetScope.Spacer(block: SpacerDsl.() -> Unit) {
    val dsl = SpacerDsl(this)
    dsl.block()
    val spacerNode = WidgetNode.newBuilder()
        .setSpacer(dsl.build())
        .build()
    addChild(spacerNode)
}