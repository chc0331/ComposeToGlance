package com.example.widget.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.dimensionDp
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Progress
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_CENTER
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_CENTER
import com.example.dsl.provider.DslLocalSize
import com.example.widget.SizeType
import com.example.widget.WidgetCategory


class StorageComponent : WidgetComponent() {

    override fun getName(): String {
        return "Storage"
    }

    override fun getDescription(): String {
        return "Storage"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.DEVICE_INFO
    }

    override fun getSizeType(): SizeType {
        return SizeType.TINY
    }

    override fun getWidgetTag(): String {
        return "Storage"
    }

    override fun WidgetScope.Content() {
        Box(
            width = matchParentDimension,
            height = matchParentDimension,
            alignment = AlignmentType.ALIGNMENT_TYPE_CENTER,
            backgroundColor = colorProvider(color = color(Color.White.toArgb()))
        ) {
            val parentSize = this@Box.getLocal(DslLocalSize) as? DpSize
            val size = getLocal(DslLocalSize) ?: parentSize
            val progressWidth = size?.let { it.width.value * 0.8f } ?: 100f

            Column(
                horizontalAlignment = H_ALIGN_CENTER,
                verticalAlignment = V_ALIGN_CENTER
            ) {
                Text(
                    text = "Storage",
                    fontSize = 16f,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    textColor = Color.Black.toArgb(),
                    textAlign = TextAlign.TEXT_ALIGN_CENTER
                )

                Progress(
                    type = ProgressType.PROGRESS_TYPE_LINEAR,
                    progressValue = 65f,
                    maxValue = 100f,
                    width = dimensionDp(progressWidth),
                    height = dimensionDp(20f),
                    progressColor = Color.Blue.toArgb(),
                    backgroundColor = Color.LightGray.toArgb()
                )

                Text(
                    text = "65%",
                    fontSize = 14f,
                    fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                    textColor = Color.Black.toArgb(),
                    textAlign = TextAlign.TEXT_ALIGN_CENTER
                )
            }
        }
    }
}

