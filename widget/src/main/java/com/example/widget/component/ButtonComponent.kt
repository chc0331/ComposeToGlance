package com.example.widget.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.WidgetScope
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.component.Box
import com.example.dsl.component.Button
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.widget.SizeType
import com.example.widget.WidgetCategory

class ButtonComponent : WidgetComponent() {
    override fun getName(): String {
        return "AnalogClock"
    }

    override fun getDescription(): String {
        return "AnalogClock"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.CLOCK
    }

    override fun getSizeType(): SizeType {
        return SizeType.TINY
    }

    override fun getWidgetTag(): String {
        return "AnalogClock"
    }

    override fun WidgetScope.Content() {
        Box(
            width = matchParentDimension,
            height = matchParentDimension,
            alignment = AlignmentType.ALIGNMENT_TYPE_CENTER,
            backgroundColor = colorProvider(color = color(Color.White.toArgb()))
        ) {
            Button(
                text = "Click Me",
                fontSize = 16f,
                fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                textColor = Color.White.toArgb(),
                backgroundColor = Color.Blue.toArgb(),
                cornerRadius = 8f
            )
        }
    }
}
