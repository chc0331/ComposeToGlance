package com.widgetworld.app.repository

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.widgetworld.widgetcomponent.proto.WidgetLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetCanvasStateRepository @Inject constructor(private val dataStore: DataStore<WidgetLayout>) {

    val dataStoreFlow: Flow<WidgetLayout> =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(WidgetLayout.getDefaultInstance())
            } else {
                throw exception
            }
        }
}

object WidgetCanvasStateProtoSerializer : Serializer<WidgetLayout> {

    override val defaultValue: WidgetLayout
        get() = WidgetLayout.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): WidgetLayout = try {
        WidgetLayout.parseFrom(input)
    } catch (e: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read proto.", e)
    }

    override suspend fun writeTo(t: WidgetLayout, output: OutputStream) {
        t.writeTo(output)
    }
}