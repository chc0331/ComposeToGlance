package com.widgetworld.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.widgetworld.app.repository.WidgetCanvasStateProtoSerializer
import com.widgetworld.widgetcomponent.proto.WidgetLayout
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private const val WIDGET_CANVAS_STATE_FILE_NAME = "widget_canvas_state.pb"

    @Provides
    @Singleton
    fun provideWidgetCanvasStateDataStore(
        @ApplicationContext context: Context
    ): DataStore<WidgetLayout> {
        return DataStoreFactory.create(
            serializer = WidgetCanvasStateProtoSerializer,
            produceFile = {
                context.dataStoreFile(WIDGET_CANVAS_STATE_FILE_NAME)
            }
        )
    }


}