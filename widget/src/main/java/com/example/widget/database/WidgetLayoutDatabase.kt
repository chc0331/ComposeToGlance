package com.example.widget.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * WidgetLayoutDocument를 저장하는 Room Database
 */
@Database(
    entities = [WidgetLayoutEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WidgetLayoutTypeConverter::class)
abstract class WidgetLayoutDatabase : RoomDatabase() {

    abstract fun widgetLayoutDao(): WidgetLayoutDao

    companion object {
        @Volatile
        private var INSTANCE: WidgetLayoutDatabase? = null

        private const val DATABASE_NAME = "widget_layout_database"

        /**
         * Database 인스턴스 가져오기 (싱글톤)
         */
        fun getDatabase(context: Context): WidgetLayoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WidgetLayoutDatabase::class.java,
                    DATABASE_NAME
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
