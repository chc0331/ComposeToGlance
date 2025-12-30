package com.widgetkit.dsl.widget.render.glance.render

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.TextAlign
import com.widgetkit.dsl.proto.TextDecoration
import com.widgetkit.dsl.proto.TextProperty
import com.widgetkit.dsl.widget.render.glance.converter.ColorConverter

internal object TextRenderUtils {

    private const val DEFAULT_FONT_SIZE_SP = 14f

    fun buildTextAndStyle(
        textProperty: TextProperty,
        context: Context
    ): Pair<String, TextStyle> {
        val textContent = when {
            textProperty.hasText() && textProperty.text.text.isNotEmpty() -> textProperty.text.text
            textProperty.hasText() && textProperty.text.resId != 0 -> {
                context.resources.getString(textProperty.text.resId)
            }
            else -> ""
        }

        val textColor = if (textProperty.hasFontColor()) {
            ColorConverter.toGlanceColor(
                textProperty.fontColor,
                context
            )
        } else {
            // 기본 텍스트 색상 (검정)
            Color.Black
        }

        val fontSizeSp = if (textProperty.fontSize > 0f) {
            textProperty.fontSize
        } else {
            DEFAULT_FONT_SIZE_SP
        }

        val textStyle = TextStyle(
            color = ColorProvider(textColor),
            fontSize = fontSizeSp.sp,
            fontWeight = textProperty.fontWeight.toGlanceFontWeight(),
            textAlign = textProperty.textAlign.toGlanceTextAlign(),
            textDecoration = textProperty.textDecoration.toGlanceTextDecoration()
        )

        return textContent to textStyle
    }

    private fun FontWeight.toGlanceFontWeight(): androidx.glance.text.FontWeight {
        return when (this) {
            FontWeight.FONT_WEIGHT_NORMAL -> androidx.glance.text.FontWeight.Normal
            FontWeight.FONT_WEIGHT_MEDIUM -> androidx.glance.text.FontWeight.Medium
            FontWeight.FONT_WEIGHT_BOLD -> androidx.glance.text.FontWeight.Bold
            else -> androidx.glance.text.FontWeight.Normal
        }
    }

    private fun TextAlign.toGlanceTextAlign(): androidx.glance.text.TextAlign {
        return when (this) {
            TextAlign.TEXT_ALIGN_START -> androidx.glance.text.TextAlign.Start
            TextAlign.TEXT_ALIGN_CENTER -> androidx.glance.text.TextAlign.Center
            TextAlign.TEXT_ALIGN_END -> androidx.glance.text.TextAlign.End
            else -> androidx.glance.text.TextAlign.Start
        }
    }

    private fun TextDecoration.toGlanceTextDecoration(): androidx.glance.text.TextDecoration {
        return when (this) {
            TextDecoration.TEXT_DECORATION_NONE -> androidx.glance.text.TextDecoration.None
            TextDecoration.TEXT_DECORATION_UNDERLINE -> androidx.glance.text.TextDecoration.Underline
            TextDecoration.TEXT_DECORATION_LINE_THROUGH -> androidx.glance.text.TextDecoration.LineThrough
            else -> androidx.glance.text.TextDecoration.None
        }
    }
}


