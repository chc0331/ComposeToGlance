package com.widgetworld.app.repository

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.core.Serializer
import androidx.glance.currentState
import com.google.protobuf.InvalidProtocolBufferException
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent
import com.widgetworld.widgetcomponent.proto.SizeType
import com.widgetworld.widgetcomponent.proto.WidgetCategory
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

    suspend fun updateSizeType(sizeType: SizeType) {
        dataStore.updateData { current ->
            current.toBuilder().setSizeType(sizeType).build()
        }
    }

    suspend fun updatePlacedWidgets(widgets: List<PlacedWidgetComponent>) {
        dataStore.updateData { current ->
            current.toBuilder().clearPlacedWidgetComponent().addAllPlacedWidgetComponent(widgets)
                .build()
        }
    }

    suspend fun addPlacedWidgets(widget: PlacedWidgetComponent) {
        dataStore.updateData { current ->
            current.toBuilder().addPlacedWidgetComponent(widget).build()
        }
    }

    suspend fun updatePlacedWidget(widget: PlacedWidgetComponent) {
        dataStore.updateData { current ->
            val index = current.placedWidgetComponentList.indexOfFirst {
                it.id == widget.id
            }
            current.toBuilder().setPlacedWidgetComponent(index, widget).build()
        }
    }

    suspend fun removePlacedWidget(widget: PlacedWidgetComponent) {
        dataStore.updateData { current ->
            val index = current.placedWidgetComponentList.indexOfFirst {
                it.id == widget.id
            }
            if (index != -1) {
                current.toBuilder().removePlacedWidgetComponent(index).build()
            } else current.toBuilder().build()
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