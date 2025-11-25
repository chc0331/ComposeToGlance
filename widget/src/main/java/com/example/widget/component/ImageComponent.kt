package com.example.widget.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.WidgetScope
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.component.Box
import com.example.dsl.proto.AlignmentType

fun WidgetScope.ImageComponent() {
    Box(
        width = matchParentDimension,
        height = matchParentDimension,
        alignment = AlignmentType.ALIGNMENT_TYPE_CENTER,
        backgroundColor = colorProvider(color = color(Color.LightGray.toArgb()))
    ) {
        // Note: 실제 이미지는 drawable 리소스나 URI를 사용해야 합니다.
        // 예시로는 빈 Box만 표시합니다.
        // Image(drawableResId = R.drawable.example_image) 형태로 사용 가능
    }
}

