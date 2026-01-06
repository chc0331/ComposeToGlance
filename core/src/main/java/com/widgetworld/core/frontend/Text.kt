package com.widgetworld.core.frontend

import androidx.compose.ui.graphics.Color
import com.widgetworld.core.WidgetScope
import com.widgetworld.core.proto.FontWeight
import com.widgetworld.core.proto.TextAlign
import com.widgetworld.core.proto.TextDecoration
import com.widgetworld.core.proto.WidgetNode
import com.widgetworld.core.proto.component.TextDsl
import com.widgetworld.core.proto.modifier.WidgetModifier

fun WidgetScope.Text(
    modifier: WidgetModifier = WidgetModifier,
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
 * 파라미터 기반 API (간편한 사용을 위한 오버로드)
 */
@JvmOverloads
fun WidgetScope.Text(
    modifier: WidgetModifier = WidgetModifier,
    text: String? = null,
    textResId: Int = 0,
    fontSize: Float = 0f,
    fontColor: Color? = null,
    fontColorArgb: Int = 0,
    fontWeight: FontWeight = FontWeight.FONT_WEIGHT_UNSPECIFIED,
    textAlign: TextAlign = TextAlign.TEXT_ALIGN_UNSPECIFIED,
    textDecoration: TextDecoration = TextDecoration.TEXT_DECORATION_UNSPECIFIED,
    maxLine: Int = 0,
    contentProperty: (TextDsl.() -> Unit)? = null
) {
    val dsl = TextDsl(this, modifier)
    
    // 파라미터로 설정된 값들을 먼저 적용
    if (text != null) {
        dsl.setText(text)
    } else if (textResId != 0) {
        dsl.setTextResId(textResId)
    }
    
    if (fontSize > 0f) {
        dsl.fontSize = fontSize
    }
    
    if (fontColor != null) {
        dsl.setFontColor(fontColor)
    } else if (fontColorArgb != 0) {
        dsl.setFontColorArgb(fontColorArgb)
    }
    
    if (fontWeight != FontWeight.FONT_WEIGHT_UNSPECIFIED) {
        dsl.fontWeight = fontWeight
    }
    
    if (textAlign != TextAlign.TEXT_ALIGN_UNSPECIFIED) {
        dsl.textAlign = textAlign
    }
    
    if (textDecoration != TextDecoration.TEXT_DECORATION_UNSPECIFIED) {
        dsl.textDecoration = textDecoration
    }
    
    if (maxLine > 0) {
        dsl.maxLine = maxLine
    }
    
    // contentProperty 블록이 있으면 실행 (파라미터를 덮어쓸 수 있음)
    contentProperty?.invoke(dsl)
    
    val textNode = WidgetNode.newBuilder()
        .setText(dsl.build())
        .build()
    addChild(textNode)
}