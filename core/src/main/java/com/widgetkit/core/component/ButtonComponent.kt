package com.widgetkit.core.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.component.Box
import com.widgetkit.dsl.component.Button
import com.widgetkit.dsl.dsl.modifier.WidgetModifier
import com.widgetkit.dsl.dsl.modifier.backgroundColor
import com.widgetkit.dsl.dsl.modifier.cornerRadius
import com.widgetkit.dsl.dsl.modifier.fillMaxHeight
import com.widgetkit.dsl.dsl.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.update.ComponentUpdateManager

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
        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(Color.White.toArgb()),
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
                            argb = Color.White.toArgb()
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = Color.Blue.toArgb()
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
