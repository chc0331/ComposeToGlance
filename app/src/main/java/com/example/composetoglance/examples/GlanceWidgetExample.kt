package com.example.composetoglance.examples

import android.content.Context
import androidx.compose.runtime.Composable
import com.example.composetoglance.proto.WidgetLayoutDocument
import com.example.toolkit.glance.GlanceRenderer

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

