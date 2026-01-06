package com.widgetkit.widgetcomponent.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.widgetkit.widgetcomponent.SizeType
import com.widgetkit.widgetcomponent.WidgetCategory
import com.widgetkit.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetkit.core.WidgetScope
import com.widgetkit.core.frontend.Text
import com.widgetkit.core.frontend.layout.Box
import com.widgetkit.core.proto.AlignmentType
import com.widgetkit.core.proto.FontWeight
import com.widgetkit.core.proto.TextAlign
import com.widgetkit.core.proto.modifier.WidgetModifier
import com.widgetkit.core.proto.modifier.backgroundColor
import com.widgetkit.core.proto.modifier.fillMaxHeight
import com.widgetkit.core.proto.modifier.fillMaxWidth
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DigitalClockComponent : WidgetComponent() {
    override fun getName(): String {
        return "DigitalClock"
    }

    override fun getDescription(): String {
        return "DigitalClock"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.CLOCK
    }

    override fun getSizeType(): SizeType {
        return SizeType.TINY
    }

    override fun getWidgetTag(): String {
        return "DigitalClock"
    }

    override fun WidgetScope.Content() {
        val theme = getLocal(WidgetLocalTheme)
        val backgroundColor = (theme?.surface as? Int) ?: Color.Black.toArgb()
        val textColor = (theme?.onSurface as? Int) ?: Color.Green.toArgb()

        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(backgroundColor),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            // 현재 시간을 포맷팅 (실제로는 동적으로 업데이트되어야 함)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val currentTime = timeFormat.format(Date())

            Text(
                contentProperty = {
                    TextContent {
                        text = currentTime
                    }
                    fontSize = 32f
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD
                    FontColor {
                        Color {
                            argb = textColor
                        }
                    }
                    textAlign = TextAlign.TEXT_ALIGN_CENTER
                }
            )
        }
    }
    override fun getUpdateManager(): ComponentUpdateManager<*>? {
        return null
    }
}
