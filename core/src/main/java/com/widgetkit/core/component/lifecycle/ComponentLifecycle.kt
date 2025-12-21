package com.widgetkit.core.component.lifecycle

import android.content.Context

/**
 * 위젯 컴포넌트의 생명주기를 관리하는 인터페이스
 * 
 * BroadcastReceiver, WorkManager 등 외부 리소스를 사용하는 컴포넌트는
 * 이 인터페이스를 구현하여 생명주기를 관리합니다.
 * 
 * 예시:
 * - BroadcastReceiver 등록/해제
 * - WorkManager 작업 등록/취소
 * - 기타 리소스 할당/해제
 */
interface ComponentLifecycle {
    
    /**
     * 컴포넌트를 등록합니다.
     * 
     * 이 메서드는 다음 시점에 호출될 수 있습니다:
     * - 앱 시작 시 (requiresAutoLifecycle = true인 경우)
     * - 컴포넌트가 위젯에 처음 배치될 때
     * 
     * @param context Context
     */
    fun register(context: Context)
    
    /**
     * 컴포넌트를 등록 해제합니다.
     * 
     * 이 메서드는 다음 시점에 호출될 수 있습니다:
     * - 앱 종료 시
     * - 모든 위젯에서 컴포넌트가 제거될 때
     * 
     * @param context Context
     */
    fun unregister(context: Context)
    
    /**
     * 컴포넌트가 현재 등록되어 있는지 확인합니다.
     * 
     * @return 등록 상태
     */
    fun isRegistered(): Boolean
}


