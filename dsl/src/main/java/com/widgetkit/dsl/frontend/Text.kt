package com.widgetkit.dsl.frontend

import androidx.compose.ui.graphics.Color
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.TextAlign
import com.widgetkit.dsl.proto.WidgetMode
import com.widgetkit.dsl.proto.WidgetNode
import com.widgetkit.dsl.proto.component.TextDsl
import com.widgetkit.dsl.proto.modifier.WidgetModifier

fun WidgetScope.Text(
    modifier: WidgetModifier = WidgetModifier,
    mode: WidgetMode = WidgetMode.WIDGET_MODE_NORMAL,
    contentProperty: TextDsl.() -> Unit
) {
    val dsl = TextDsl(this, modifier)
    dsl.contentProperty()
    val textNode = WidgetNode.newBuilder()
        .setText(dsl.build())
        .setWidgetMode(mode)
        .build()
    addChild(textNode)
}

/**
 * 파라미터 기반 API (간편한 사용을 위한 오버로드)
 */
@JvmOverloads
fun WidgetScope.Text(
    modifier: WidgetModifier = WidgetModifier,
    mode: WidgetMode = WidgetMode.WIDGET_MODE_NORMAL,
    text: String? = null,
    textResId: Int = 0,
    fontSize: Float = 0f,
    fontColor: Color? = null,
    fontColorArgb: Int = 0,
    fontWeight: FontWeight = FontWeight.FONT_WEIGHT_UNSPECIFIED,
    textAlign: TextAlign = TextAlign.TEXT_ALIGN_UNSPECIFIED,
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
    
    if (maxLine > 0) {
        dsl.maxLine = maxLine
    }
    
    // contentProperty 블록이 있으면 실행 (파라미터를 덮어쓸 수 있음)
    contentProperty?.invoke(dsl)
    
    val textNode = WidgetNode.newBuilder()
        .setText(dsl.build())
        .setWidgetMode(mode)
        .build()
    addChild(textNode)
}