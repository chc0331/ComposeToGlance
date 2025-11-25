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

fun WidgetScope.ButtonComponent() {
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

