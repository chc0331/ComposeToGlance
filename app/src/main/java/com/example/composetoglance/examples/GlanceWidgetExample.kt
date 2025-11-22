package com.example.composetoglance.examples

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.toolkit.glance.GlanceRenderer
import com.example.toolkit.proto.WidgetLayoutDocument

/**
 * Glance 위젯 예제
 * 
 * 사용 방법:
 * ```kotlin
 * @Composable
 * fun MyGlanceWidget(context: Context) {
 *     val layout = createSimpleColumnLayoutDsl()
 *     val renderer = GlanceRenderer(context)
 *     renderer.render(layout)
 * }
 * ```
 */

/**
 * DSL로 생성한 레이아웃을 Glance 위젯으로 렌더링하는 예제
 */
@Composable
fun renderWidgetLayout(
    document: WidgetLayoutDocument,
    context: Context
) {
    val renderer = GlanceRenderer(context)
    renderer.render(document)
}

/**
 * Compose 예제: DSL을 활용해 실제 위젯을 Glance로 보여주는 데모
 */
@Composable
fun DslWidgetDemo() {
    val context = LocalContext.current
    val layout = createSimpleColumnLayoutDsl()
    renderWidgetLayout(layout, context)
}

