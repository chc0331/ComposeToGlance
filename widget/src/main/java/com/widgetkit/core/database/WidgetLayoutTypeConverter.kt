package com.widgetkit.core.database

import androidx.room.TypeConverter
import com.widgetkit.dsl.proto.WidgetLayoutDocument
import com.google.protobuf.InvalidProtocolBufferException

/**
 * WidgetLayoutDocument와 ByteArray 간의 변환을 처리하는 TypeConverter
 */
class WidgetLayoutTypeConverter {

    @TypeConverter
    fun fromByteArray(bytes: ByteArray?): WidgetLayoutDocument? {
        return if (bytes == null || bytes.isEmpty()) {
            null
        } else {
            try {
                WidgetLayoutDocument.parseFrom(bytes)
            } catch (e: InvalidProtocolBufferException) {
                null
            }
        }
    }

    @TypeConverter
    fun toByteArray(document: WidgetLayoutDocument?): ByteArray? {
        return document?.toByteArray()
    }
}

