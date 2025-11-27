package com.example.widget.repository

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.widget.proto.PlacedWidgetComponent
import com.example.widget.proto.SizeType
import com.example.widget.proto.WidgetLayout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private const val WIDGET_LAYOUT = "widget_layout"
private const val DATA_STORE_FILE_NAME = "widget_layout.pb"
private val Context.layoutDataStore: DataStore<WidgetLayout> by dataStore(
    fileName = DATA_STORE_FILE_NAME,
    serializer = WidgetLayoutProtoSerializer
)

class WidgetLayoutRepository(private val context: Context) {

    private val TAG = "WidgetLayoutRepo"

    val dataStoreFlow: Flow<WidgetLayout> = context.layoutDataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(WidgetLayout.getDefaultInstance())
        } else {
            throw exception
        }
    }

    suspend fun updateData(sizeType: SizeType, positionedWidgets: List<PlacedWidgetComponent>) {
        with(context.layoutDataStore) {
            updateData { it.toBuilder().clear().build() }
            updateData {
                val builder = it.toBuilder()
                builder.setSizeType(sizeType)
                builder.addAllPlacedWidgetComponent(positionedWidgets)
                builder.build()
            }
        }
    }

    suspend fun fetchData() = context.layoutDataStore.data.first()
}