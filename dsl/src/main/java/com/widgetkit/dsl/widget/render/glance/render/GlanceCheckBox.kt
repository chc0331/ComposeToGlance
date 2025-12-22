package com.widgetkit.dsl.widget.render.glance.render

import androidx.compose.runtime.Composable
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.CheckboxDefaults
import androidx.glance.layout.Box
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.GlanceModifierBuilder
import com.widgetkit.dsl.widget.render.glance.converter.ColorConverter

/**
 * Checkbox 노드 렌더러
 * Strategy 패턴을 사용하여 Glance와 RemoteViews 렌더링을 분리
 */
internal object GlanceCheckBox : RenderNode {
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

        val checkBoxProperty = node.checkbox
        val viewProperty = checkBoxProperty.viewProperty

        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
            .then(context.modifier)

        val checked = checkBoxProperty.checked

        // TextProperty 기반 라벨 텍스트
        val textProperty = checkBoxProperty.textProperty

        val (textContent, textStyle) = TextRenderUtils.buildTextAndStyle(
            textProperty = textProperty,
            context = context.context
        )

        CheckBox(
            checked = checked,
            onCheckedChange = null,
            modifier = modifier,
            text = textContent,
            style = textStyle,
            colors = CheckboxDefaults.colors(
                checkedColor = ColorConverter.toGlanceColorProvider(checkBoxProperty.checkedColor),
                uncheckedColor = ColorConverter.toGlanceColorProvider(checkBoxProperty.uncheckedColor)
            ),
        )
    }
}

