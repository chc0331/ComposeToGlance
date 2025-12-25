package com.widgetkit.core.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.Button
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.cornerRadius
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalTheme

class ButtonComponent : WidgetComponent() {
    override fun getName(): String {
        return "Button"
    }

    override fun getDescription(): String {
        return "Button"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.BASIC
    }

    override fun getSizeType(): SizeType {
        return SizeType.TINY
    }

    override fun getWidgetTag(): String {
        return "Button"
    }

    override fun WidgetScope.Content() {
        val theme = getLocal(WidgetLocalTheme)
        val backgroundColor = (theme?.surface as? Int) ?: Color.White.toArgb()
        val buttonBgColor = (theme?.primary as? Int) ?: Color.Blue.toArgb()
        val buttonTextColor = (theme?.onSurface as? Int) ?: Color.White.toArgb()

        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(backgroundColor),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Button(
                modifier = WidgetModifier
                    .cornerRadius(8f),
                contentProperty = {
                    Text {
                        text = "Click Me"
                    }
                    fontSize = 16f
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD
                    FontColor {
                        Color {
                            argb = buttonTextColor
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = buttonBgColor
                        }
                    }
                }
            )
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*>? {
        return null
    }
}
