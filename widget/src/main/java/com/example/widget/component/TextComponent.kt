package com.example.widget.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.TextAlign
import com.example.widget.SizeType
import com.example.widget.WidgetCategory


class TextComponent : WidgetComponent() {

    override fun getName(): String {
        return "Text"
    }

    override fun getDescription(): String {
        return "Text"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.BASIC
    }

    override fun getSizeType(): SizeType {
        return SizeType.TINY
    }

    override fun getWidgetTag(): String {
        return "Text"
    }

    override fun WidgetScope.Content() {
        Box({
            viewProperty {
                Width { matchParent = true }
                Height { matchParent = true }
                BackgroundColor {
                    color {
                        argb = Color.White.toArgb()
                    }
                }
            }
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            Text({
                text = "Hello World"
                fontSize = 18f
                fontWeight = FontWeight.FONT_WEIGHT_BOLD
                fontColor {
                    color {
                        argb = Color.Black.toArgb()
                    }
                }
                textAlign = TextAlign.TEXT_ALIGN_CENTER
            })
        }
    }
}

