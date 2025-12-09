package com.example.widget.component.update

import android.content.Context
import com.example.widget.component.WidgetComponent

/**
 * 위젯 컴포넌트의 업데이트 관리를 위한 인터페이스
 *
 * Manager가 필요한 컴포넌트만 이 인터페이스를 구현합니다.
 * partiallyUpdateAppWidget을 사용하여 위젯을 업데이트합니다.
 */
interface ComponentUpdateManager<T> {

    val widget: WidgetComponent

    /**
     * 컴포넌트를 업데이트합니다.
     * @param context Context
     * @param data 업데이트할 데이터
     */
    suspend fun updateComponent(context: Context, data: T)

    /**
     * 컴포넌트의 상태를 동기화합니다.
     * 선택적 메서드 - 모든 Manager가 구현할 필요는 없습니다.
     * @param context Context
     */
    suspend fun syncComponentState(context: Context) {
        // 기본 구현은 비어있음 - 필요시 오버라이드
    }
}

