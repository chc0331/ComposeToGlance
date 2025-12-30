package com.widgetkit.dsl.proto.property

import com.widgetkit.dsl.proto.ColorProvider
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.TextAlign
import com.widgetkit.dsl.proto.TextContent
import com.widgetkit.dsl.proto.TextDecoration
import com.widgetkit.dsl.proto.TextProperty
import com.widgetkit.dsl.proto.ViewProperty

class TextPropertyDsl(private val builder: TextProperty.Builder) {
    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
        val viewPropertyBuilder = ViewProperty.newBuilder()
        ViewPropertyDsl(viewPropertyBuilder).block()
        builder.setViewProperty(viewPropertyBuilder.build())
    }

    /**
     * 실제 텍스트 콘텐츠 설정 블록
     * - WidgetScope.Text와 혼동을 피하기 위해 TextContent라는 이름을 사용
     */
    fun TextContent(block: TextContentDsl.() -> Unit) {
        val textContentBuilder = TextContent.newBuilder()
        TextContentDsl(textContentBuilder).block()
        builder.setText(textContentBuilder.build())
    }

    var maxLine: Int
        get() = builder.maxLine
        set(value) {
            builder.setMaxLine(value)
        }

    fun FontColor(block: ColorProviderDsl.() -> Unit) {
        val colorProviderBuilder = ColorProvider.newBuilder()
        ColorProviderDsl(colorProviderBuilder).block()
        builder.setFontColor(colorProviderBuilder.build())
    }

    var fontSize: Float
        get() = builder.fontSize
        set(value) {
            builder.setFontSize(value)
        }

    var fontWeight: FontWeight
        get() = builder.fontWeight
        set(value) {
            builder.setFontWeight(value)
        }

    var textAlign: TextAlign
        get() = builder.textAlign
        set(value) {
            builder.setTextAlign(value)
        }

    var textDecoration: TextDecoration
        get() = builder.textDecoration
        set(value) {
            builder.setTextDecoration(value)
        }
}


