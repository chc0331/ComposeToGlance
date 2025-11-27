package com.example.widget.database

import android.content.Context
import com.example.dsl.proto.WidgetLayoutDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * WidgetLayoutDocument를 Room DB에 저장하고 관리하는 Repository
 * 
 * 사용 예시:
 * ```
 * val repository = WidgetLayoutRepository(context)
 * 
 * // 문서 저장
 * val document = WidgetLayout { ... }
 * val id = repository.insertDocument(document, "My Layout")
 * 
 * // 문서 조회
 * val savedDocument = repository.getDocumentById(id)
 * 
 * // 모든 문서 Flow로 조회
 * repository.getAllDocuments().collect { documents ->
 *     // 처리
 * }
 * ```
 */
class WidgetLayoutRepository(context: Context) {
    private val dao = WidgetLayoutDatabase.getDatabase(context).widgetLayoutDao()

    /**
     * WidgetLayoutDocument를 저장하고 생성된 ID를 반환
     */
    suspend fun insertDocument(document: WidgetLayoutDocument, name: String? = null): Long {
        return dao.insertDocument(document, name)
    }

    /**
     * ID로 WidgetLayoutDocument 조회
     */
    suspend fun getDocumentById(id: Long): WidgetLayoutDocument? {
        return dao.getLayoutById(id)?.toDocument()
    }

    /**
     * ID로 WidgetLayoutDocument를 Flow로 조회
     */
    fun getDocumentByIdFlow(id: Long): Flow<WidgetLayoutDocument?> {
        return dao.getLayoutByIdFlow(id).map { it?.toDocument() }
    }

    /**
     * 이름으로 WidgetLayoutDocument 조회
     */
    suspend fun getDocumentByName(name: String): WidgetLayoutDocument? {
        return dao.getLayoutByName(name)?.toDocument()
    }

    /**
     * 모든 WidgetLayoutDocument를 Flow로 조회
     */
    fun getAllDocuments(): Flow<List<WidgetLayoutDocument>> {
        return dao.getAllLayouts().map { entities ->
            entities.mapNotNull { it.toDocument() }
        }
    }

    /**
     * 모든 Entity를 Flow로 조회
     */
    fun getAllEntities(): Flow<List<WidgetLayoutEntity>> {
        return dao.getAllLayouts()
    }

    /**
     * WidgetLayoutDocument 업데이트
     */
    suspend fun updateDocument(id: Long, document: WidgetLayoutDocument) {
        dao.updateDocument(id, document)
    }

    /**
     * Entity 업데이트
     */
    suspend fun updateEntity(entity: WidgetLayoutEntity) {
        dao.updateLayout(entity)
    }

    /**
     * ID로 레이아웃 삭제
     */
    suspend fun deleteById(id: Long) {
        dao.deleteLayoutById(id)
    }

    /**
     * Entity로 레이아웃 삭제
     */
    suspend fun delete(entity: WidgetLayoutEntity) {
        dao.deleteLayout(entity)
    }

    /**
     * 모든 레이아웃 삭제
     */
    suspend fun deleteAll() {
        dao.deleteAllLayouts()
    }

    /**
     * 레이아웃 개수 조회
     */
    suspend fun getCount(): Int {
        return dao.getLayoutCount()
    }

    /**
     * 레이아웃 개수를 Flow로 조회
     */
    fun getCountFlow(): Flow<Int> {
        return dao.getLayoutCountFlow()
    }
}

