package com.example.toolkit.component

import ProtoModifier
import android.R.attr.maxLines
import androidx.compose.ui.graphics.toArgb
import com.example.composetoglance.proto.Color
import com.example.composetoglance.proto.ColorProvider
import com.example.composetoglance.proto.FontWeight
import com.example.composetoglance.proto.TextAlign
import com.example.composetoglance.proto.TextContent
import com.example.toolkit.Emittable

private val DefaultColor = Color.newBuilder()
    .setArgb(androidx.compose.ui.graphics.Color.DarkGray.toArgb()).build()

data class TextStyle(
    val maxLine: Int = 0,
    val fontColor: ColorProvider = ColorProvider.newBuilder().setColor(DefaultColor).build(),
    val fontSize: Float = 0f,
    val fontWeight: FontWeight = FontWeight.FONT_WEIGHT_NORMAL,
    val textAlign: TextAlign = TextAlign.TEXT_ALIGN_START
)

abstract class EmittableWithText : Emittable {
    var text: TextContent = TextContent.newBuilder().setText("").build()
    var style: TextStyle? = null
}

class EmittableText : EmittableWithText() {

    override var modifier: ProtoModifier = ProtoModifier

    override fun copy(): Emittable =
        EmittableText().also {
            it.modifier = modifier
            it.text = text
            it.style = style
        }

    override fun toString(): String =
        "EmittableText($text, style=$style, modifier=$modifier, maxLines=$maxLines)"
}