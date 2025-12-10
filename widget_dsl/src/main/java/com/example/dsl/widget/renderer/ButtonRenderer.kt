package com.example.dsl.widget.renderer

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.text.FontWeight
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.GlanceModifierBuilder
import com.example.dsl.widget.GlanceRenderer
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.converter.ActionConverter
import com.example.dsl.widget.converter.ColorConverter

/**
 * Button 노드 렌더러
 */
object ButtonRenderer : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        if (!node.hasButton()) {
            androidx.glance.layout.Box {}
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

        androidx.glance.layout.Box(
            modifier = finalModifier,
            contentAlignment = androidx.glance.layout.Alignment.Center
        ) {
            androidx.glance.text.Text(
                text = textContent,
                style = textStyle
            )
        }
    }

    private fun toGlanceFontWeight(protoWeight: com.example.dsl.proto.FontWeight): FontWeight {
        return when (protoWeight) {
            com.example.dsl.proto.FontWeight.FONT_WEIGHT_NORMAL -> FontWeight.Normal
            com.example.dsl.proto.FontWeight.FONT_WEIGHT_MEDIUM -> FontWeight.Medium
            com.example.dsl.proto.FontWeight.FONT_WEIGHT_BOLD -> FontWeight.Bold
            else -> FontWeight.Medium
        }
    }
}

