package com.example.widget.content

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun WidgetScope.DigitalClockComponent() {
    Box(
        width = matchParentDimension,
        height = matchParentDimension,
        alignment = AlignmentType.ALIGNMENT_TYPE_CENTER,
        backgroundColor = colorProvider(color = color(Color.Black.toArgb()))
    ) {
        // 현재 시간을 포맷팅 (실제로는 동적으로 업데이트되어야 함)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTime = timeFormat.format(Date())
        
        Text(
            text = currentTime,
            fontSize = 32f,
            fontWeight = FontWeight.FONT_WEIGHT_BOLD,
            textColor = Color.Green.toArgb(),
            textAlign = TextAlign.TEXT_ALIGN_CENTER
        )
    }
}

