package com.example.toolkit.component

import ProtoModifier
import com.example.composetoglance.proto.Color
import com.example.composetoglance.proto.ContentScale
import com.example.composetoglance.proto.ImageProvider
import com.example.toolkit.Emittable

class EmittableImage : Emittable {
    override var modifier: ProtoModifier = ProtoModifier
    var provider: ImageProvider? = null
    var tintColor: Color? = null
    var alpha: Float? = null // null retains the source image's alpha
    var contentScale: ContentScale = ContentScale.CONTENT_SCALE_FIT

    override fun copy(): Emittable =
        EmittableImage().also {
            it.modifier = modifier
            it.provider = provider
            it.tintColor = tintColor
            it.alpha = alpha
            it.contentScale = contentScale
        }

    override fun toString(): String =
        "EmittableImage(" +
                "modifier=$modifier, " +
                "provider=$provider, " +
                "tintColor=$tintColor, " +
                "alpha=$alpha, " +
                "contentScale=$contentScale" +
                ")"
}