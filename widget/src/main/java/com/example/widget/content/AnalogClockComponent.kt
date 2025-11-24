package com.example.widget.content

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.dimensionDp
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.component.Box
import com.example.dsl.component.Progress
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.ProgressType
import com.example.dsl.provider.DslLocalSize
import java.util.Calendar

fun WidgetScope.AnalogClockComponent() {
    Box(
        width = matchParentDimension,
        height = matchParentDimension,
        alignment = AlignmentType.ALIGNMENT_TYPE_CENTER,
        backgroundColor = colorProvider(color = color(Color.Black.toArgb()))
    ) {
        val parentSize = this@Box.getLocal(DslLocalSize) as? DpSize
        val size = getLocal(DslLocalSize) ?: parentSize
        val clockSize = size?.let { kotlin.math.min(it.width.value, it.height.value) * 0.8f } ?: 80f
        
        // 시계 원형 배경 (Progress를 원형으로 사용)
        Progress(
            type = ProgressType.PROGRESS_TYPE_CIRCULAR,
            progressValue = 100f,
            width = dimensionDp(clockSize),
            height = dimensionDp(clockSize),
            progressColor = Color.White.toArgb(),
            backgroundColor = Color.DarkGray.toArgb()
        )
        
        // 시계 중심점 (간단한 원)
        val centerSize = clockSize * 0.1f
        Progress(
            type = ProgressType.PROGRESS_TYPE_CIRCULAR,
            progressValue = 100f,
            width = dimensionDp(centerSize),
            height = dimensionDp(centerSize),
            progressColor = Color.White.toArgb()
        )
    }
}

