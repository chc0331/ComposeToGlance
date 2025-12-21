package com.widgetkit.core.repository

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.widgetkit.core.proto.WidgetLayout
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object WidgetLayoutProtoSerializer : Serializer<WidgetLayout> {

    override val defaultValue: WidgetLayout
        get() = WidgetLayout.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): WidgetLayout = try {
        WidgetLayout.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read proto.", exception)
    }

    override suspend fun writeTo(t: WidgetLayout, output: OutputStream) {
        t.writeTo(output)
    }
}
