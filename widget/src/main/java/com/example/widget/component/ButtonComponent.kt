package com.example.widget.component

import android.R.attr.radius
import android.R.attr.text
import android.graphics.Color.argb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Button
import com.example.dsl.modifier.*
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.update.ComponentUpdateManager

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
            modifier = Modifier
                .width(matchParent)
                .height(matchParent)
                .backgroundColor(Color.White.toArgb()),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Button(
                modifier = Modifier
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
