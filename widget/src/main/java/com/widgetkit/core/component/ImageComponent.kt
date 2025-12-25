package com.widgetkit.core.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalTheme

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
        val theme = getLocal(WidgetLocalTheme)
        val backgroundColor = (theme?.surfaceVariant as? Int) ?: Color.LightGray.toArgb()

        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(backgroundColor),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            // Note: 실제 이미지는 drawable 리소스나 URI를 사용해야 합니다.
            // 예시로는 빈 Box만 표시합니다.
            // Image(drawableResId = R.drawable.example_image) 형태로 사용 가능
        }
    }
    override fun getUpdateManager(): ComponentUpdateManager<*>? {
        return null
    }
}
