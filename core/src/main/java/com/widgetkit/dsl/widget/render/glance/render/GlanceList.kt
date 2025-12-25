package com.widgetkit.dsl.widget.render.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import com.widgetkit.dsl.proto.WidgetMode
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.GlanceModifierBuilder
import com.widgetkit.dsl.widget.render.glance.converter.AlignmentConverter

internal object GlanceList : RenderNode {

    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasList()) {
            Box { }
            return
        }

        val listProperty = node.list
        val viewProperty = listProperty.viewProperty
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
            .then(context.modifier)
        val horizontalAlignment = AlignmentConverter.toGlanceHorizontalAlignment(
            listProperty.horizontalAlignment
        )
        val children = node.childrenList

        if (node.widgetMode == WidgetMode.WIDGET_MODE_PREVIEW) {
            Column(
                modifier = modifier,
                horizontalAlignment = horizontalAlignment
            ) {
                children.forEach { child ->
                    renderer.renderNode(child, context)
                }
            }
        } else {
            LazyColumn(
                modifier = modifier,
                horizontalAlignment = horizontalAlignment
            ) {
                children.forEach { child ->
                    item {
                        renderer.renderNode(child, context)
                    }
                }
            }
        }
    }
}