package com.example.widget.content

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.dimensionDp
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.Box
import com.example.dsl.provider.DslLocalSize
import com.example.dsl.Progress
import com.example.dsl.Text
import com.example.dsl.WidgetScope
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.ProgressType


fun WidgetScope.BatteryComponent() {
    Box(
        width = matchParentDimension,
        height = matchParentDimension,
        alignment = AlignmentType.ALIGNMENT_TYPE_CENTER,
        backgroundColor = colorProvider(color = color(Color.Black.toArgb()))
    ) {
        val parentSize = this@Box.getLocal(DslLocalSize) as? DpSize
        val size = getLocal(DslLocalSize) ?: parentSize
        val progressSize = size?.let { (it * 0.6f).height.value } ?: 60f
        Progress(
            type = ProgressType.PROGRESS_TYPE_CIRCULAR,
            progressValue = 50f,
            width = dimensionDp(progressSize),
            height = dimensionDp(progressSize),
            progressColor = Color.Green.toArgb()
        )
        Text(text = "50%", textColor = Color.White.toArgb())
    }
}
