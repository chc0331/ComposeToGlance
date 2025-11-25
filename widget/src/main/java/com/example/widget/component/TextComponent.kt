package com.example.widget.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.WidgetScope
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.component.Box
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.TextAlign

fun WidgetScope.TextComponent() {
    Box(
        width = matchParentDimension,
        height = matchParentDimension,
        alignment = AlignmentType.ALIGNMENT_TYPE_CENTER,
        backgroundColor = colorProvider(color = color(Color.White.toArgb()))
    ) {
        Text(
            text = "Hello World",
            fontSize = 18f,
            fontWeight = FontWeight.FONT_WEIGHT_BOLD,
            textColor = Color.Black.toArgb(),
            textAlign = TextAlign.TEXT_ALIGN_CENTER
        )
    }
}

