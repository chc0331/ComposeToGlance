package com.widgetworld.widgetcomponent.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * WidgetLayoutEntity에 대한 데이터 접근 객체
 */
@Dao
interface WidgetLayoutDao {

    /**
     * 모든 레이아웃을 Flow로 반환
     */
    @Query("SELECT * FROM widget_layouts ORDER BY updatedAt DESC")
    fun getAllLayouts(): Flow<List<WidgetLayoutEntity>>

    /**
     * ID로 레이아웃 조회
     */
    @Query("SELECT * FROM widget_layouts WHERE id = :id")
    suspend fun getLayoutById(id: Long): WidgetLayoutEntity?

    /**
     * ID로 레이아웃을 Flow로 조회
     */
    @Query("SELECT * FROM widget_layouts WHERE id = :id")
    fun getLayoutByIdFlow(id: Long): Flow<WidgetLayoutEntity?>

    /**
     * 이름으로 레이아웃 조회
     */
    @Query("SELECT * FROM widget_layouts WHERE name = :name LIMIT 1")
    suspend fun getLayoutByName(name: String): WidgetLayoutEntity?

    /**
     * 레이아웃 삽입
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLayout(layout: WidgetLayoutEntity): Long

    /**
     * WidgetLayoutDocument를 직접 삽입
     */
    suspend fun insertDocument(
        document: com.widgetworld.core.proto.WidgetLayoutDocument,
        name: String? = null
    ): Long {
        val entity = WidgetLayoutEntity(
            documentBytes = document.toByteArray(),
            name = name,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        return insertLayout(entity)
    }

    /**
     * 레이아웃 업데이트
     */
    @Update
    suspend fun updateLayout(layout: WidgetLayoutEntity)

    /**
     * WidgetLayoutDocument를 직접 업데이트
     */
    suspend fun updateDocument(id: Long, document: com.widgetworld.core.proto.WidgetLayoutDocument) {
        val existing = getLayoutById(id) ?: return
        val updated = existing.copy(
            documentBytes = document.toByteArray(),
            updatedAt = System.currentTimeMillis()
        )
        updateLayout(updated)
    }

    /**
     * 레이아웃 삭제
     */
    @Delete
    suspend fun deleteLayout(layout: WidgetLayoutEntity)

    /**
     * ID로 레이아웃 삭제
     */
    @Query("DELETE FROM widget_layouts WHERE id = :id")
    suspend fun deleteLayoutById(id: Long)

    /**
     * 모든 레이아웃 삭제
     */
    @Query("DELETE FROM widget_layouts")
    suspend fun deleteAllLayouts()

    /**
     * 레이아웃 개수 조회
     */
    @Query("SELECT COUNT(*) FROM widget_layouts")
    suspend fun getLayoutCount(): Int

    /**
     * 레이아웃 개수를 Flow로 조회
     */
    @Query("SELECT COUNT(*) FROM widget_layouts")
    fun getLayoutCountFlow(): Flow<Int>
}
