package com.example.dsl.builder

import com.example.dsl.proto.ButtonProperty
import com.example.dsl.proto.Color
import com.example.dsl.proto.ColorProvider
import com.example.dsl.proto.ContentScale
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.ImageProperty
import com.example.dsl.proto.ImageProvider
import com.example.dsl.proto.ProgressProperty
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.SpacerProperty
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.TextContent
import com.example.dsl.proto.TextProperty
import com.example.dsl.proto.ViewProperty

/**
 * ComponentProperty 관련 간단한 빌더 함수
 *
 * 이 파일은 파라미터를 직접 받는 간단한 빌더 함수를 포함합니다.
 * - TextProperty(viewProperty, text, fontColor, fontSize, fontWeight, textAlign, maxLine)
 * - ImageProperty(viewProperty, provider, tintColor, alpha, contentScale)
 * - ButtonProperty(viewProperty, text, fontColor, fontSize, fontWeight, backgroundColor, maxLine)
 * - ProgressProperty(viewProperty, type, maxValue, progressValue, progressColor, backgroundColor)
 * - SpacerProperty(viewProperty)
 *
 * DSL 클래스 및 block을 받는 DSL 빌더 함수는 ComponentPropertyDsl.kt를 참조하세요.
 */

internal fun TextProperty(
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

internal fun ImageProperty(
    viewProperty: ViewProperty,
    provider: ImageProvider,
    tintColor: Color? = null,
    alpha: Float = 1f,
    contentScale: ContentScale = ContentScale.CONTENT_SCALE_FIT
): ImageProperty =
    ImageProperty.newBuilder().setViewProperty(viewProperty).setProvider(provider)
        .setAlpha(alpha).setContentScale(contentScale)
        .apply { tintColor?.let { setTintColor(it) } }.build()

fun ButtonProperty(
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

fun ProgressProperty(
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


fun SpacerProperty(
    viewProperty: ViewProperty
): SpacerProperty = SpacerProperty.newBuilder().setViewProperty(viewProperty).build()

