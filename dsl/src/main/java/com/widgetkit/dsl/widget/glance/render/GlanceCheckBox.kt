package com.widgetkit.dsl.widget.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.layout.Box
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.rendernode.NodeRenderer
import com.widgetkit.dsl.widget.rendernode.RenderContext
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.glance.GlanceModifierBuilder

/**
 * Checkbox 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
internal object GlanceCheckBox : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasCheckbox()) {
            Box {}
            return
        }

        val checkboxProperty = node.checkbox
        val viewProperty = checkboxProperty.viewProperty

        // Modifier 생성
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
            .then(context.modifier)
    }
}

