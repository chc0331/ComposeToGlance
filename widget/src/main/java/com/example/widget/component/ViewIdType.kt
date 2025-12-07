package com.example.widget.component

/**
 * View ID 타입을 나타내는 Sealed Class
 * 각 컴포넌트는 자신이 필요로 하는 ViewIdType을 정의합니다.
 */
sealed class ViewIdType {
    abstract val typeName: String
}
