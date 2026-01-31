package com.widgetworld.app.editor

import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PlacedWidgetComponent를 위한 고유 ID 생성기
 * AtomicInteger를 사용하여 thread-safe한 ID 생성
 */
@Singleton
class PlacedWidgetIdGenerator @Inject constructor() {
    private val idCounter = AtomicInteger(1)

    /**
     * 고유한 ID를 생성하여 반환
     * @return 새로운 고유 ID
     */
    fun generateId(): Int {
        return idCounter.getAndIncrement()
    }

    /**
     * 현재 ID 카운터 값 조회 (디버깅용)
     */
    fun getCurrentId(): Int {
        return idCounter.get()
    }
}

