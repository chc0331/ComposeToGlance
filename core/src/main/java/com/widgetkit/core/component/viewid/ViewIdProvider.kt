package com.widgetkit.core.component.viewid

/**
 * View ID 타입을 나타내는 Sealed Class
 * 각 컴포넌트는 자신이 필요로 하는 ViewIdType을 정의합니다.
 */
abstract class ViewIdType {
    abstract val typeName: String
}

/**
 * View ID를 제공하는 인터페이스
 * partiallyUpdateAppWidget을 위한 유니크한 View ID를 생성합니다.
 */
interface ViewIdProvider {
    /**
     * 컴포넌트가 필요로 하는 View ID 타입들을 정의
     * @return View ID 타입 리스트
     */
    fun getViewIdTypes(): List<ViewIdType>

    /**
     * 특정 View ID 타입과 Grid Index로 실제 View ID 생성
     * @param viewIdType View ID 타입
     * @param gridIndex 그리드 인덱스 (1-based, 1~8 for 4x2 grid)
     * @return 고유한 View ID
     */
    fun generateViewId(viewIdType: ViewIdType, gridIndex: Int): Int

    /**
     * 컴포넌트가 사용하는 최대 Grid Index 수
     * 기본값: 8 for 4x2 grid
     * @return 최대 그리드 수
     */
    fun getMaxGridCount(): Int = 8
}
