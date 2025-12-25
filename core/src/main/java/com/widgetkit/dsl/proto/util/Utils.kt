package com.widgetkit.dsl.proto.util

import com.widgetkit.dsl.proto.Color
import com.widgetkit.dsl.proto.ColorProvider
import com.widgetkit.dsl.proto.Dp

internal fun Color(argb: Int): Color = Color.newBuilder().setArgb(argb).build()

internal fun ColorProvider(
    resId: Int = 0, color: Color? = null, darkColor: Color? = null
): ColorProvider = ColorProvider.newBuilder().apply { if (resId != 0) setResId(resId) }
    .apply { color?.let { setColor(it) } }.apply { darkColor?.let { setDarkColor(it) } }.build()

internal fun Dp(value: Float): Dp = Dp.newBuilder().setValue(value).build()