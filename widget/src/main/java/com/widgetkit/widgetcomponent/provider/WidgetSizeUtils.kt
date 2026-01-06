package com.widgetkit.widgetcomponent.provider

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.SizeF
import kotlin.math.abs

fun getExactWidgetSizeInDp(context: Context, appWidgetOptions: Bundle): SizeF {
    // 1. 기존 방식(Min/Max + Orientation)으로 '예상 타겟 크기' 계산
    val minWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
    val maxWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
    val minHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
    val maxHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

    val orientation = context.resources.configuration.orientation

    // 현재 방향에 따른 대략적인 타겟 사이즈
    val targetWidth: Float
    val targetHeight: Float

    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        targetWidth = maxWidth.toFloat()
        targetHeight = minHeight.toFloat()
    } else {
        targetWidth = minWidth.toFloat()
        targetHeight = maxHeight.toFloat()
    }

    // 2. API 31+ (Android 12) 이상인 경우 OPTION_APPWIDGET_SIZES 확인
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val possibleSizes = appWidgetOptions.getParcelableArrayList<SizeF>(
            AppWidgetManager.OPTION_APPWIDGET_SIZES
        )

        // 리스트가 유효하다면, 타겟 사이즈와 가장 가까운 사이즈를 찾음
        if (!possibleSizes.isNullOrEmpty()) {
            return findClosestSize(targetWidth, targetHeight, possibleSizes)
        }
    }

    // 3. API 31 미만이거나 리스트가 없으면 그냥 타겟 사이즈 반환
    return SizeF(targetWidth, targetHeight)
}

// 타겟(targetW, targetH)과 리스트 내의 사이즈들 간의 거리를 비교해 가장 가까운 것 반환
private fun findClosestSize(targetW: Float, targetH: Float, sizes: List<SizeF>): SizeF {
    var bestMatch: SizeF = sizes[0]
    var minDiff = Float.MAX_VALUE

    for (size in sizes) {
        // 유클리드 거리 공식 응용 (너비 차이^2 + 높이 차이^2)
        // 성능을 위해 sqrt는 생략해도 비교 가능
        val diffW = abs(size.width - targetW)
        val diffH = abs(size.height - targetH)

        // 단순히 차이의 합으로 비교하거나, 제곱의 합으로 비교
        val currentDiff = (diffW * diffW) + (diffH * diffH)

        if (currentDiff < minDiff) {
            minDiff = currentDiff
            bestMatch = size
        }
    }

    return bestMatch
}
