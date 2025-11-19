package com.example.composetoglance.builder

import com.example.composetoglance.proto.ButtonProperty
import com.example.composetoglance.proto.Color
import com.example.composetoglance.proto.ColorProvider
import com.example.composetoglance.proto.ContentScale
import com.example.composetoglance.proto.FontWeight
import com.example.composetoglance.proto.ImageProperty
import com.example.composetoglance.proto.ImageProvider
import com.example.composetoglance.proto.ProgressProperty
import com.example.composetoglance.proto.ProgressType
import com.example.composetoglance.proto.SpacerProperty
import com.example.composetoglance.proto.TextAlign
import com.example.composetoglance.proto.TextContent
import com.example.composetoglance.proto.TextProperty
import com.example.composetoglance.proto.ViewProperty

object ComponentBuilder {

    fun textProperty(
        viewProperty: ViewProperty,
        text: TextContent,
        fontColor: ColorProvider,
        fontSize: Float,
        fontWeight: FontWeight = FontWeight.FONT_WEIGHT_NORMAL,
        textAlign: TextAlign = TextAlign.TEXT_ALIGN_START,
        maxLine: Int = 1
    ): TextProperty = TextProperty.newBuilder().setViewProperty(viewProperty).setText(text)
        .setFontColor(fontColor).setFontSize(fontSize).setFontWeight(fontWeight)
        .setTextAlign(textAlign).setMaxLine(maxLine).build()

    fun imageProperty(
        viewProperty: ViewProperty,
        provider: ImageProvider,
        tintColor: Color? = null,
        alpha: Float = 1f,
        contentScale: ContentScale = ContentScale.CONTENT_SCALE_FIT
    ): ImageProperty =
        ImageProperty.newBuilder().setViewProperty(viewProperty).setProvider(provider)
            .setAlpha(alpha).setContentScale(contentScale)
            .apply { tintColor?.let { setTintColor(it) } }.build()

    fun buttonProperty(
        viewProperty: ViewProperty,
        text: TextContent,
        fontColor: ColorProvider,
        fontSize: Float,
        fontWeight: FontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
        backgroundColor: ColorProvider? = null,
        maxLine: Int = 1
    ): ButtonProperty = ButtonProperty.newBuilder().setViewProperty(viewProperty).setText(text)
        .setFontColor(fontColor).setFontSize(fontSize).setFontWeight(fontWeight).setMaxLine(maxLine)
        .apply { backgroundColor?.let { setBackgroundColor(it) } }.build()

    fun progressProperty(
        viewProperty: ViewProperty,
        type: ProgressType,
        maxValue: Float,
        progressValue: Float,
        progressColor: ColorProvider,
        backgroundColor: ColorProvider
    ): ProgressProperty =
        ProgressProperty.newBuilder().setViewProperty(viewProperty).setProgressType(type)
            .setMaxValue(maxValue).setProgressValue(progressValue).setProgressColor(progressColor)
            .setBackgroundColor(backgroundColor).build()


    fun spacerProperty(
        viewProperty: ViewProperty
    ): SpacerProperty = SpacerProperty.newBuilder().setViewProperty(viewProperty).build()

}
