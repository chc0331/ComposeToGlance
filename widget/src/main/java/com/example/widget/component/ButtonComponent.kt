package com.example.widget.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Button
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.widget.SizeType
import com.example.widget.WidgetCategory

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
        Box({
            viewProperty {
                width { matchParent = true }
                height { matchParent = true }
                backgroundColor {
                    color {
                        argb = Color.White.toArgb()
                    }
                }
            }
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            Button({
                text = "Click Me"
                fontSize = 16f
                fontWeight = FontWeight.FONT_WEIGHT_BOLD
                fontColor {
                    color {
                        argb = Color.White.toArgb()
                    }
                }
                backgroundColor {
                    color {
                        argb = Color.Blue.toArgb()
                    }
                }
                viewProperty {
                    cornerRadius {
                        radius = 8f
                    }
                }
            })
        }
    }
}
