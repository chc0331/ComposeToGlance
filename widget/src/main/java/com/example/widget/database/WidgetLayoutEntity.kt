package com.example.widget.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dsl.proto.WidgetLayoutDocument

/**
 * WidgetLayoutDocument를 저장하는 Room Entity
 */
@Entity(tableName = "widget_layouts")
data class WidgetLayoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /**
     * WidgetLayoutDocument를 바이트 배열로 변환하여 저장
     */
    val documentBytes: ByteArray?,

    /**
     * 레이아웃 이름 (선택사항)
     */
    val name: String? = null,

    /**
     * 생성 시간 (밀리초)
     */
    val createdAt: Long = System.currentTimeMillis(),

    /**
     * 수정 시간 (밀리초)
     */
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * ByteArray를 WidgetLayoutDocument로 변환
     */
    fun toDocument(): WidgetLayoutDocument? {
        return documentBytes?.let {
            try {
                WidgetLayoutDocument.parseFrom(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WidgetLayoutEntity

        if (id != other.id) return false
        if (documentBytes != null) {
            if (other.documentBytes == null) return false
            if (!documentBytes.contentEquals(other.documentBytes)) return false
        } else if (other.documentBytes != null) return false
        if (name != other.name) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (documentBytes?.contentHashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}
