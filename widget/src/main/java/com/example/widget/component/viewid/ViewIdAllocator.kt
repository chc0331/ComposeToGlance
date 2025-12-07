package com.example.widget.component.viewid

import android.util.Log
import com.example.widget.component.WidgetComponent

/**
 * View ID 범위를 자동으로 할당하고 관리하는 클래스
 * 컴포넌트 등록 시 충돌 없이 View ID 범위를 자동 할당합니다.
 */
class ViewIdAllocator {

    companion object {
        private const val TAG = "ViewIdAllocator"
        private const val INITIAL_BASE_ID = 1000  // 시작 ID (기존 시스템과 충돌 방지)
    }

    private var currentBaseId = INITIAL_BASE_ID
    private val allocations = mutableMapOf<String, ViewIdAllocation>()

    /**
     * View ID 할당 정보를 담는 데이터 클래스
     */
    data class ViewIdAllocation(
        val componentTag: String,
        val baseId: Int,
        val viewTypeCount: Int,
        val maxGridCount: Int
    ) {
        /**
         * 이 컴포넌트가 사용하는 총 ID 개수
         */
        val totalIdCount: Int
            get() = viewTypeCount * maxGridCount

        /**
         * 이 컴포넌트의 마지막 ID
         */
        val endId: Int
            get() = baseId + totalIdCount - 1

        override fun toString(): String {
            return "ViewIdAllocation(tag=$componentTag, baseId=$baseId, " +
                    "viewTypes=$viewTypeCount, gridCount=$maxGridCount, " +
                    "range=$baseId..$endId)"
        }
    }

    /**
     * 컴포넌트에 View ID 범위 할당
     * @param component View ID가 필요한 컴포넌트
     * @return 할당된 View ID 정보
     */
    fun allocate(component: WidgetComponent): ViewIdAllocation {
        val tag = component.getWidgetTag()

        // 이미 할당된 경우 기존 할당 정보 반환
        if (allocations.containsKey(tag)) {
            Log.d(TAG, "Component already allocated: ${allocations[tag]}")
            return allocations[tag]!!
        }

        val baseId = currentBaseId
        val viewTypeCount = component.getViewIdTypes().size
        val maxGridCount = component.getMaxGridCount()

        val allocation = ViewIdAllocation(
            componentTag = tag,
            baseId = baseId,
            viewTypeCount = viewTypeCount,
            maxGridCount = maxGridCount
        )

        allocations[tag] = allocation
        currentBaseId = allocation.endId + 1  // 다음 컴포넌트를 위한 ID

        Log.i(TAG, "Allocated: $allocation")

        return allocation
    }

    /**
     * 컴포넌트의 Base ID 조회
     * @param componentTag 컴포넌트 태그
     * @return Base View ID
     * @throws IllegalStateException 할당되지 않은 컴포넌트인 경우
     */
    fun getBaseId(componentTag: String): Int {
        return allocations[componentTag]?.baseId
            ?: throw IllegalStateException("Component not allocated: $componentTag")
    }

    /**
     * 특정 컴포넌트의 할당 정보 조회
     * @param componentTag 컴포넌트 태그
     * @return 할당 정보, 없으면 null
     */
    fun getAllocation(componentTag: String): ViewIdAllocation? {
        return allocations[componentTag]
    }

    /**
     * 모든 할당 정보 조회
     * @return 모든 할당 정보 맵
     */
    fun getAllAllocations(): Map<String, ViewIdAllocation> {
        return allocations.toMap()
    }

    /**
     * 할당된 컴포넌트 개수
     */
    fun getAllocatedComponentCount(): Int = allocations.size

    /**
     * 다음 할당될 Base ID
     */
    fun getNextBaseId(): Int = currentBaseId
}