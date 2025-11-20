package com.example.toolkit.component

import ProtoModifier
import androidx.compose.ui.graphics.toArgb
import com.example.composetoglance.proto.Color
import com.example.composetoglance.proto.ColorProvider
import com.example.composetoglance.proto.ProgressType
import com.example.toolkit.Emittable

public class EmittableProgressIndicator : Emittable {
    override var modifier: ProtoModifier = ProtoModifier
    var progressType: ProgressType = ProgressType.PROGRESS_TYPE_UNSPECIFIED
    var progress: Float = 0.0f
    var indeterminate: Boolean = false
    var color: ColorProvider = DefaultIndicatorColor
    var backgroundColor: ColorProvider = DefaultBackgroundColor

    override fun copy(): Emittable =
        EmittableProgressIndicator().also {
            it.modifier = modifier
            it.progressType = progressType
            it.progress = progress
            it.indeterminate = indeterminate
            it.color = color
            it.backgroundColor = backgroundColor
        }

    override fun toString(): String =
        "EmittableLinearProgressIndicator(" +
                "modifier=$modifier, " +
                "progress=$progress, " +
                "indeterminate=$indeterminate, " +
                "color=$color, " +
                "backgroundColor=$backgroundColor" +
                ")"
}

private val DefaultIndicatorColor = ColorProvider.newBuilder()
    .setColor(Color.newBuilder().setArgb(androidx.compose.ui.graphics.Color.Black.toArgb())).build()

private val DefaultBackgroundColor = ColorProvider.newBuilder()
    .setColor(Color.newBuilder().setArgb(androidx.compose.ui.graphics.Color.Gray.toArgb())).build()