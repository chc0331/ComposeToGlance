package com.example.widget.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.glance.state.GlanceState
import com.example.toolkit.builder.color
import com.example.toolkit.builder.colorProvider
import com.example.toolkit.builder.dimensionDp
import com.example.toolkit.builder.matchParentDimension
import com.example.toolkit.dsl.Box
import com.example.toolkit.dsl.DslLocalSize
import com.example.toolkit.dsl.Progress
import com.example.toolkit.dsl.Text
import com.example.toolkit.dsl.WidgetScope
import com.example.toolkit.proto.AlignmentType
import com.example.toolkit.proto.ProgressType


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
