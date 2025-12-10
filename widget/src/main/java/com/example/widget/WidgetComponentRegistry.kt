package com.example.widget

import android.content.Context
import android.util.Log
import com.example.widget.component.AnalogClockComponent
import com.example.widget.component.ButtonComponent
import com.example.widget.component.DigitalClockComponent
import com.example.widget.component.ImageComponent
import com.example.widget.component.devicecare.DeviceCareWidget
import com.example.widget.component.TextComponent
import com.example.widget.component.WidgetComponent
import com.example.widget.component.battery.BatteryWidget
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget
import com.example.widget.component.lifecycle.ComponentLifecycleManager
import com.example.widget.component.viewid.ViewIdAllocator

fun initializeWidgetComponents() {
    WidgetComponentRegistry.registerComponent(TextComponent())
    WidgetComponentRegistry.registerComponent(ImageComponent())
    WidgetComponentRegistry.registerComponent(ButtonComponent())

    WidgetComponentRegistry.registerComponent(AnalogClockComponent())
    WidgetComponentRegistry.registerComponent(DigitalClockComponent())

    WidgetComponentRegistry.registerComponent(BatteryWidget())
    WidgetComponentRegistry.registerComponent(BluetoothBatteryWidget())
    WidgetComponentRegistry.registerComponent(DeviceCareWidget())
}

/**
 * 위젯 컴포넌트를 등록하고 조회하는 레지스트리
 * componentId를 키로 WidgetScope extension function을 저장합니다.
 */
object WidgetComponentRegistry {
    private const val TAG = "WidgetComponentRegistry"
    private val registry = mutableMapOf<String, WidgetComponent>()
    private val viewIdAllocator = ViewIdAllocator()
    private var lifecycleInitialized = false

    /**
     * 위젯 컴포넌트를 등록합니다.
     * @param componentId 컴포넌트 고유 ID
     * @param component DSL 컴포넌트 함수
     */
    fun registerComponent(widget: WidgetComponent) {
        val widgetTag = widget.getWidgetTag()
        registry[widgetTag] = widget

        if (widget.getViewIdTypes().isNotEmpty()) {
            val allocation = viewIdAllocator.allocate(widget)
            Log.d(TAG, "Registered component with View IDs: $widgetTag -> $allocation")
        }
        
        Log.d(TAG, "Registered component: $widgetTag")
    }

    /**
     * 모든 컴포넌트의 lifecycle을 초기화합니다.
     * 앱 시작 시 한 번만 호출되어야 합니다.
     * 
     * @param context Context
     */
    fun initializeLifecycles(context: Context) {
        if (lifecycleInitialized) {
            Log.w(TAG, "Lifecycles already initialized, skipping")
            return
        }
        
        Log.i(TAG, "Initializing component lifecycles")
        ComponentLifecycleManager.initializeComponents(context, getAllComponents())
        lifecycleInitialized = true
    }

    /**
     * 모든 컴포넌트의 lifecycle을 종료합니다.
     * 앱 종료 시 호출되어야 합니다.
     * 
     * @param context Context
     */
    fun shutdownLifecycles(context: Context) {
        if (!lifecycleInitialized) {
            Log.w(TAG, "Lifecycles not initialized, nothing to shutdown")
            return
        }
        
        Log.i(TAG, "Shutting down component lifecycles")
        ComponentLifecycleManager.shutdownAll(context)
        lifecycleInitialized = false
    }

    /**
     * 등록된 컴포넌트를 조회합니다.
     * @param componentId 컴포넌트 고유 ID
     * @return DSL 컴포넌트 함수, 없으면 null
     */
    fun getComponent(tag: String): WidgetComponent? {
        return registry[tag]
    }

    fun getAllComponents(): List<WidgetComponent> {
        return registry.values.toList()
    }

    /**
     * 모든 등록된 컴포넌트 ID 목록을 반환합니다.
     */
    fun getAllComponentIds(): Set<String> {
        return registry.keys.toSet()
    }

    /**
     * 컴포넌트의 Base View ID를 조회합니다.
     * @param componentTag 컴포넌트 태그
     * @return Base View ID
     * @throws IllegalStateException 등록되지 않았거나 View ID가 할당되지 않은 경우
     */
    fun getBaseViewId(componentTag: String): Int {
        if (!registry.containsKey(componentTag)) {
            throw IllegalStateException("Component not registered: $componentTag")
        }
        return viewIdAllocator.getBaseId(componentTag)
    }

    /**
     * ViewIdAllocator 인스턴스를 조회합니다.
     * (디버깅 및 테스트 용도)
     * @return ViewIdAllocator 인스턴스
     */
    fun getViewIdAllocator(): ViewIdAllocator = viewIdAllocator
    
    /**
     * Lifecycle 초기화 상태를 반환합니다.
     * (디버깅 및 테스트 용도)
     * @return 초기화 여부
     */
    fun isLifecycleInitialized(): Boolean = lifecycleInitialized
}
