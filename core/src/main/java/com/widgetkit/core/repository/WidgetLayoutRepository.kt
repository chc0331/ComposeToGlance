package com.widgetkit.core.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.widgetkit.core.proto.PlacedWidgetComponent
import com.widgetkit.core.proto.SizeType
import com.widgetkit.core.proto.WidgetLayout
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first

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
