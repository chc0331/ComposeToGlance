package com.example.widget.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.WidgetScope
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.matchParentDimension
import com.example.dsl.component.Box
import com.example.dsl.proto.AlignmentType
import com.example.widget.SizeType
import com.example.widget.WidgetCategory

class ImageComponent : WidgetComponent() {
    override fun getName(): String {
        return "Image"
    }

    override fun getDescription(): String {
        return "Image"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.BASIC
    }

    override fun getSizeType(): SizeType {
        return SizeType.TINY
    }

    override fun getWidgetTag(): String {
        return "Image"
    }

    override fun WidgetScope.Content() {
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
}

