package com.example.dsl.dsl.builder

import com.example.dsl.proto.Color
import com.example.dsl.proto.ColorProvider

/**
 * Content 관련 간단한 빌더 함수
 *
 * 이 파일은 파라미터를 직접 받는 간단한 빌더 함수를 포함합니다.
 * - Color(argb: Int)
 * - ColorProvider(resId, color, darkColor)
 * - TextContent(text, resId)
 * - ImageProviderFromDrawable(resId)
 * - ImageProviderFromUri(uri)
 * - ImageProviderFromBitmap(bitmap)
 *
 * DSL 클래스 및 block을 받는 DSL 빌더 함수는 ContentDsl.kt를 참조하세요.
 */
internal fun Color(argb: Int): Color = Color.newBuilder().setArgb(argb).build()

internal fun ColorProvider(
    resId: Int = 0, color: Color? = null, darkColor: Color? = null
): ColorProvider = ColorProvider.newBuilder().apply { if (resId != 0) setResId(resId) }
    .apply { color?.let { setColor(it) } }.apply { darkColor?.let { setDarkColor(it) } }.build()
