package com.example.dsl.component

import com.example.dsl.WidgetScope
import com.example.dsl.modifier.DslModifier
import com.example.dsl.syntax.ButtonDsl
import com.example.dsl.syntax.ImageDsl
import com.example.dsl.syntax.ProgressDsl
import com.example.dsl.syntax.SpacerDsl
import com.example.dsl.syntax.TextDsl
import com.example.dsl.proto.WidgetNode

/**
 * 컴포넌트 최상위 DSL 함수
 * 
 * 이 파일은 WidgetScope에 대한 확장 함수로, 최상위 컴포넌트 DSL 함수를 포함합니다.
 * - Text(block): Text 컴포넌트 생성
 * - Image(block): Image 컴포넌트 생성
 * - Button(block): Button 컴포넌트 생성
 * - Progress(block): Progress 컴포넌트 생성
 * - Spacer(block): Spacer 컴포넌트 생성
 * 
 * 각 컴포넌트의 DSL 클래스는 ComponentDsl.kt를 참조하세요.
 */

// ==================== 컴포넌트 DSL (중첩 DSL 빌더 패턴) ====================

/**
 * Text 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시 (기존 방식):
 * ```
 * Text({
 *     ViewProperty {
 *         Width { wrapContent = true }
 *         Height { wrapContent = true }
 *     }
 *     TextContent { text = "Hello World" }
 *     fontSize = 18f
 *     fontWeight = FontWeight.FONT_WEIGHT_BOLD
 * })
 * ```
 * 
 * 사용 예시 (Modifier 사용):
 * ```
 * Text(
 *     modifier = DslModifier
 *         .width(wrapContent)
 *         .height(wrapContent)
 *         .padding(16.dp)
 * ) {
 *     TextContent { text = "Hello World" }
 *     fontSize = 18f
 *     fontWeight = FontWeight.FONT_WEIGHT_BOLD
 * }
 * ```
 */
fun WidgetScope.Text(
    modifier: DslModifier = DslModifier,
    contentProperty: TextDsl.() -> Unit
) {
    val dsl = TextDsl(this, modifier)
    dsl.contentProperty()
    val textNode = WidgetNode.newBuilder()
        .setText(dsl.build())
        .build()
    addChild(textNode)
}

/**
 * Image 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시 (기존 방식):
 * ```
 * Image({
 *     ViewProperty {
 *         Width { wrapContent = true }
 *         Height { wrapContent = true }
 *     }
 *     Provider { drawableResId = R.drawable.example_image }
 *     contentScale = ContentScale.CONTENT_SCALE_FIT
 * })
 * ```
 * 
 * 사용 예시 (Modifier 사용):
 * ```
 * Image(
 *     modifier = DslModifier
 *         .width(wrapContent)
 *         .height(wrapContent)
 *         .padding(16.dp)
 * ) {
 *     Provider { drawableResId = R.drawable.example_image }
 *     contentScale = ContentScale.CONTENT_SCALE_FIT
 * }
 * ```
 */
fun WidgetScope.Image(
    modifier: DslModifier = DslModifier,
    contentProperty: ImageDsl.() -> Unit
) {
    val dsl = ImageDsl(this, modifier)
    dsl.contentProperty()
    val imageNode = WidgetNode.newBuilder()
        .setImage(dsl.build())
        .build()
    addChild(imageNode)
}

/**
 * Button 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시 (기존 방식):
 * ```
 * Button({
 *     ViewProperty {
 *         Width { wrapContent = true }
 *         Height { wrapContent = true }
 *     }
 *     Text { text = "Click Me" }
 *     fontSize = 16f
 *     fontWeight = FontWeight.FONT_WEIGHT_BOLD
 * })
 * ```
 * 
 * 사용 예시 (Modifier 사용):
 * ```
 * Button(
 *     modifier = DslModifier
 *         .width(wrapContent)
 *         .height(wrapContent)
 *         .padding(16.dp)
 * ) {
 *     Text { text = "Click Me" }
 *     fontSize = 16f
 *     fontWeight = FontWeight.FONT_WEIGHT_BOLD
 * }
 * ```
 */
fun WidgetScope.Button(
    modifier: DslModifier = DslModifier,
    contentProperty: ButtonDsl.() -> Unit
) {
    val dsl = ButtonDsl(this, modifier)
    dsl.contentProperty()
    val buttonNode = WidgetNode.newBuilder()
        .setButton(dsl.build())
        .build()
    addChild(buttonNode)
}

/**
 * Progress 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시 (기존 방식):
 * ```
 * Progress({
 *     ViewProperty {
 *         Width { matchParent = true }
 *         Height { matchParent = true }
 *     }
 *     progressType = ProgressType.PROGRESS_TYPE_LINEAR
 *     maxValue = 100f
 *     progressValue = 65f
 * })
 * ```
 * 
 * 사용 예시 (Modifier 사용):
 * ```
 * Progress(
 *     modifier = DslModifier
 *         .width(matchParent)
 *         .height(matchParent)
 *         .padding(16.dp)
 * ) {
 *     progressType = ProgressType.PROGRESS_TYPE_LINEAR
 *     maxValue = 100f
 *     progressValue = 65f
 * }
 * ```
 */
fun WidgetScope.Progress(
    modifier: DslModifier = DslModifier,
    contentProperty: ProgressDsl.() -> Unit
) {
    val dsl = ProgressDsl(this, modifier)
    dsl.contentProperty()
    val progressNode = WidgetNode.newBuilder()
        .setProgress(dsl.build())
        .build()
    addChild(progressNode)
}

/**
 * Spacer 컴포넌트 (중첩 DSL 빌더 패턴)
 * 
 * 사용 예시 (기존 방식):
 * ```
 * Spacer({
 *     ViewProperty {
 *         Width { wrapContent = true }
 *         Height { wrapContent = true }
 *     }
 * })
 * ```
 * 
 * 사용 예시 (Modifier 사용):
 * ```
 * Spacer(
 *     modifier = DslModifier
 *         .width(wrapContent)
 *         .height(wrapContent)
 *         .padding(16.dp)
 * )
 * ```
 */
fun WidgetScope.Spacer(
    modifier: DslModifier = DslModifier,
    contentProperty: SpacerDsl.() -> Unit = {}
) {
    val dsl = SpacerDsl(this, modifier)
    dsl.contentProperty()
    val spacerNode = WidgetNode.newBuilder()
        .setSpacer(dsl.build())
        .build()
    addChild(spacerNode)
}