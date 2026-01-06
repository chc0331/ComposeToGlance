package com.widgetworld.widgetcomponent.component

import com.widgetworld.widgetcomponent.SizeType
import com.widgetworld.widgetcomponent.WidgetCategory
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.component.datastore.ComponentDataStore
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.component.viewid.ViewIdProvider
import com.widgetworld.widgetcomponent.component.viewid.ViewIdType
import com.widgetworld.core.WidgetScope

abstract class WidgetComponent : ViewIdProvider {

    abstract fun getName(): String
    abstract fun getDescription(): String
    abstract fun getWidgetCategory(): WidgetCategory
    abstract fun getSizeType(): SizeType
    abstract fun getWidgetTag(): String
    abstract fun WidgetScope.Content()

    /**
     * Content를 주어진 scope에서 실행합니다.
     * 이 메서드는 WidgetLocalProvider 내에서 호출되어야 하며,
     * 생성된 children은 WidgetLocalProvider가 자동으로 수집합니다.
     */
    fun renderContent(scope: WidgetScope) {
        scope.Content()
    }

    override fun getViewIdTypes(): List<ViewIdType> = emptyList()

    override fun generateViewId(viewIdType: ViewIdType, gridIndex: Int): Int {
        val baseId = getBaseViewIdInternal()
        val typeIndex = getViewIdTypes().indexOf(viewIdType)

        if (typeIndex == -1) {
            throw IllegalArgumentException(
                "ViewIdType $viewIdType not found in ${getWidgetTag()}'s viewIdTypes"
            )
        }
        return baseId + typeIndex * getMaxGridCount() + (gridIndex - 1)
    }

    override fun getMaxGridCount(): Int {
        return 8
    }

    /**
     * Registry로부터 Base View ID를 가져옵니다.
     * 외부에서 주입받도록 하기 위한 내부 메서드
     */
    private fun getBaseViewIdInternal(): Int {
        // WidgetComponentRegistry를 통해 조회
        // 순환 참조를 피하기 위해 lazy하게 조회
        return try {
            WidgetComponentRegistry.getBaseViewId(getWidgetTag())
        } catch (e: Exception) {
            throw IllegalStateException(
                "Component ${getWidgetTag()} not registered in WidgetComponentRegistry",
                e
            )
        }
    }

    /**
     * 컴포넌트의 업데이트 관리자를 반환합니다.
     * 업데이트가 필요한 컴포넌트만 오버라이드하여 구현합니다.
     * @return ComponentUpdateManager 또는 null
     */
    abstract fun getUpdateManager(): ComponentUpdateManager<*>?

    /**
     * 컴포넌트의 DataStore를 반환합니다.
     * 상태 저장이 필요한 컴포넌트만 오버라이드하여 구현합니다.
     * * @return ComponentDataStore 또는 null
     */
    open fun getDataStore(): ComponentDataStore<*>? = null
}
