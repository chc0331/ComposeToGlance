package com.example.widget.component.lifecycle

import android.content.Context
import android.util.Log
import com.example.widget.component.WidgetComponent

/**
 * 위젯 컴포넌트들의 생명주기를 중앙에서 관리하는 매니저
 * 
 * 이 클래스는 다음 역할을 수행합니다:
 * 1. 앱 시작 시 자동 등록이 필요한 컴포넌트들을 등록
 * 2. 앱 종료 시 모든 등록된 컴포넌트를 해제
 * 3. 특정 컴포넌트의 동적 등록/해제 관리
 */
object ComponentLifecycleManager {
    
    private const val TAG = "ComponentLifecycleMgr"
    
    // 현재 등록된 컴포넌트들의 lifecycle을 추적
    private val registeredLifecycles = mutableMapOf<String, ComponentLifecycle>()
    
    // 각 컴포넌트가 몇 개의 위젯에서 사용되는지 추적 (참조 카운트)
    private val referenceCount = mutableMapOf<String, Int>()
    
    /**
     * 컴포넌트들을 초기화하고 자동 등록이 필요한 것들을 등록합니다.
     * 
     * @param context Context
     * @param components 초기화할 컴포넌트 리스트
     */
    fun initializeComponents(context: Context, components: List<WidgetComponent>) {
        Log.i(TAG, "Initializing ${components.size} components")
        
        components.forEach { component ->
            val lifecycle = component.getLifecycle()
            val tag = component.getWidgetTag()
            
            if (lifecycle != null && component.requiresAutoLifecycle()) {
                try {
                    registerComponentLifecycle(context, tag, lifecycle)
                    Log.d(TAG, "Auto-registered lifecycle for: $tag")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to auto-register lifecycle for: $tag", e)
                }
            }
        }
    }
    
    /**
     * 특정 컴포넌트의 lifecycle을 등록합니다.
     * 참조 카운트를 사용하여 중복 등록을 방지합니다.
     * 
     * @param context Context
     * @param componentTag 컴포넌트 태그
     * @param lifecycle ComponentLifecycle 인스턴스
     */
    fun registerComponentLifecycle(
        context: Context,
        componentTag: String,
        lifecycle: ComponentLifecycle
    ) {
        val currentCount = referenceCount.getOrDefault(componentTag, 0)
        
        if (currentCount == 0) {
            // 처음 등록하는 경우
            try {
                lifecycle.register(context)
                registeredLifecycles[componentTag] = lifecycle
                referenceCount[componentTag] = 1
                Log.i(TAG, "Registered lifecycle: $componentTag")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register lifecycle: $componentTag", e)
                throw e
            }
        } else {
            // 이미 등록된 경우 참조 카운트만 증가
            referenceCount[componentTag] = currentCount + 1
            Log.d(TAG, "Increased reference count for $componentTag: ${currentCount + 1}")
        }
    }
    
    /**
     * 특정 컴포넌트의 lifecycle 등록을 해제합니다.
     * 참조 카운트를 감소시키고, 0이 되면 실제로 해제합니다.
     * 
     * @param context Context
     * @param componentTag 컴포넌트 태그
     */
    fun unregisterComponentLifecycle(context: Context, componentTag: String) {
        val currentCount = referenceCount.getOrDefault(componentTag, 0)
        
        if (currentCount <= 0) {
            Log.w(TAG, "Attempted to unregister non-registered component: $componentTag")
            return
        }
        
        if (currentCount == 1) {
            // 마지막 참조인 경우 실제로 해제
            val lifecycle = registeredLifecycles[componentTag]
            if (lifecycle != null) {
                try {
                    lifecycle.unregister(context)
                    registeredLifecycles.remove(componentTag)
                    referenceCount.remove(componentTag)
                    Log.i(TAG, "Unregistered lifecycle: $componentTag")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to unregister lifecycle: $componentTag", e)
                }
            }
        } else {
            // 참조 카운트만 감소
            referenceCount[componentTag] = currentCount - 1
            Log.d(TAG, "Decreased reference count for $componentTag: ${currentCount - 1}")
        }
    }
    
    /**
     * 컴포넌트가 위젯에 배치될 때 호출합니다.
     * 필요시 lifecycle을 등록합니다.
     * 
     * @param context Context
     * @param component 배치된 컴포넌트
     */
    fun onComponentPlaced(context: Context, component: WidgetComponent) {
        val lifecycle = component.getLifecycle()
        if (lifecycle != null && !component.requiresAutoLifecycle()) {
            // 자동 등록이 아닌 경우에만 여기서 등록
            val tag = component.getWidgetTag()
            registerComponentLifecycle(context, tag, lifecycle)
        }
    }
    
    /**
     * 컴포넌트가 위젯에서 제거될 때 호출합니다.
     * 필요시 lifecycle을 해제합니다.
     * 
     * @param context Context
     * @param component 제거된 컴포넌트
     */
    fun onComponentRemoved(context: Context, component: WidgetComponent) {
        val lifecycle = component.getLifecycle()
        if (lifecycle != null && !component.requiresAutoLifecycle()) {
            // 자동 등록이 아닌 경우에만 여기서 해제
            val tag = component.getWidgetTag()
            unregisterComponentLifecycle(context, tag)
        }
    }
    
    /**
     * 모든 등록된 컴포넌트의 lifecycle을 해제합니다.
     * 앱 종료 시 호출되어야 합니다.
     * 
     * @param context Context
     */
    fun shutdownAll(context: Context) {
        Log.i(TAG, "Shutting down all component lifecycles")
        
        registeredLifecycles.forEach { (tag, lifecycle) ->
            try {
                lifecycle.unregister(context)
                Log.d(TAG, "Shutdown lifecycle: $tag")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to shutdown lifecycle: $tag", e)
            }
        }
        
        registeredLifecycles.clear()
        referenceCount.clear()
    }
    
    /**
     * 특정 컴포넌트가 등록되어 있는지 확인합니다.
     * 
     * @param componentTag 컴포넌트 태그
     * @return 등록 여부
     */
    fun isRegistered(componentTag: String): Boolean {
        return registeredLifecycles.containsKey(componentTag)
    }
    
    /**
     * 현재 등록된 모든 컴포넌트 태그 목록을 반환합니다.
     * (디버깅 용도)
     * 
     * @return 등록된 컴포넌트 태그 Set
     */
    fun getRegisteredComponents(): Set<String> {
        return registeredLifecycles.keys.toSet()
    }
    
    /**
     * 특정 컴포넌트의 참조 카운트를 반환합니다.
     * (디버깅 용도)
     * 
     * @param componentTag 컴포넌트 태그
     * @return 참조 카운트
     */
    fun getReferenceCount(componentTag: String): Int {
        return referenceCount.getOrDefault(componentTag, 0)
    }
}


