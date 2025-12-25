package com.widgetkit.dsl.widget.render.glance.render

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.widget.WidgetRenderer
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.node.RenderNode
import com.widgetkit.dsl.widget.render.glance.GlanceModifierBuilder
import com.widgetkit.dsl.widget.render.glance.converter.ActionConverter
import com.widgetkit.dsl.widget.render.glance.converter.ColorConverter

/**
 * Button 노드 렌더러
 */
internal object GlanceButton : RenderNode {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: WidgetRenderer
    ) {
        if (!node.hasButton()) {
            Box {}
            return
        }

        val buttonProperty = node.button
        val viewProperty = buttonProperty.viewProperty

        // Modifier 생성
        var modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
            .then(context.modifier)

        // 텍스트 내용
        val textContent = when {
            buttonProperty.text.text.isNotEmpty() -> buttonProperty.text.text
            buttonProperty.text.resId != 0 -> {
                context.context.resources.getString(buttonProperty.text.resId)
            }

            else -> ""
        }

        // 텍스트 색상
        val textColor = ColorConverter.toGlanceColor(
            buttonProperty.fontColor,
            context.context
        )

        // 배경 색상
        val backgroundColor = if (buttonProperty.hasBackgroundColor()) {
            ColorConverter.toGlanceColor(
                buttonProperty.backgroundColor,
                context.context
            )
        } else {
            null
        }

        // 텍스트 스타일
        val textStyle = TextStyle(
            color = ColorProvider(textColor),
            fontSize = buttonProperty.fontSize.sp,
            fontWeight = toGlanceFontWeight(buttonProperty.fontWeight)
        )

        // Click Action
        val clickAction = if (viewProperty.hasClickAction()) {
            ActionConverter.toGlanceAction(viewProperty.clickAction, context.context)
        } else {
            null
        }

        // Glance Button은 backgroundColor를 직접 지원하지 않으므로
        // Box + Text + clickable로 구현
        var finalModifier = modifier
        if (backgroundColor != null) {
            finalModifier = finalModifier.background(backgroundColor)
        }
        if (clickAction != null) {
            finalModifier = finalModifier.clickable(clickAction)
        }

        Box(
            modifier = finalModifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = textContent,
                style = textStyle
            )
        }
    }

    private fun toGlanceFontWeight(protoWeight: com.widgetkit.dsl.proto.FontWeight): FontWeight {
        return when (protoWeight) {
            com.widgetkit.dsl.proto.FontWeight.FONT_WEIGHT_NORMAL -> FontWeight.Normal
            com.widgetkit.dsl.proto.FontWeight.FONT_WEIGHT_MEDIUM -> FontWeight.Medium
            com.widgetkit.dsl.proto.FontWeight.FONT_WEIGHT_BOLD -> FontWeight.Bold
            else -> FontWeight.Medium
        }
    }
}

