package com.example.widget.component

import android.R.attr.text
import android.graphics.Color.argb
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Text
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.BoxLayoutProperty
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.TextAlign
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
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
        Box({
            ViewProperty {
                Width { matchParent = true }
                Height { matchParent = true }
                BackgroundColor {
                    Color {
                        argb = Color.Black.toArgb()
                    }
                }
            }
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            // 현재 시간을 포맷팅 (실제로는 동적으로 업데이트되어야 함)
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val currentTime = timeFormat.format(Date())

            Text({
                Text {
                    text = currentTime
                }
                fontSize = 32f
                fontWeight = FontWeight.FONT_WEIGHT_BOLD
                FontColor {
                    Color {
                        argb = Color.Green.toArgb()
                    }
                }
                textAlign = TextAlign.TEXT_ALIGN_CENTER
            })
        }
    }
}

