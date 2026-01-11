package com.widgetworld.widgetcomponent.component

import com.widgetworld.widgetcomponent.SizeType
import com.widgetworld.widgetcomponent.WidgetCategory
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.component.datastore.ComponentDataStore
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.component.viewid.ViewIdProvider
import com.widgetworld.widgetcomponent.component.viewid.ViewIdType
import com.widgetworld.core.WidgetScope
import com.widgetworld.widgetcomponent.proto.PlacedWidgetComponent

abstract class WidgetComponent : ViewIdProvider {

    abstract fun getName(): String
    abstract fun getDescription(): String
    abstract fun getWidgetCategory(): WidgetCategory
    abstract fun getSizeType(): SizeType
    abstract fun getWidgetTag(): String
    abstract fun WidgetScope.Content()

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

    abstract fun getUpdateManager(): ComponentUpdateManager<*>?

    fun checkIfComponentExist(placedWidgetComponentList: List<PlacedWidgetComponent>): Boolean =
        placedWidgetComponentList.find { it.widgetTag.contains(this.getWidgetTag()) } != null

    open fun getDataStore(): ComponentDataStore<*>? = null
}
