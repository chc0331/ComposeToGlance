package com.example.dsl.dsl

import com.example.dsl.WidgetScope
import com.example.dsl.dsl.modifier.WidgetModifier
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.ColumnLayoutProperty
import com.example.dsl.proto.RowLayoutProperty
import com.example.dsl.proto.BoxLayoutProperty
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_START
import com.example.dsl.proto.VerticalAlignment
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_TOP

/**
 * 레이아웃 DSL 클래스
 * 
 * 이 파일은 각 레이아웃의 DSL 클래스를 포함합니다.
 * - ColumnLayoutDsl: Column 레이아웃의 DSL 클래스
 * - RowLayoutDsl: Row 레이아웃의 DSL 클래스
 * - BoxLayoutDsl: Box 레이아웃의 DSL 클래스
 * 
 * 최상위 레이아웃 DSL 함수는 Layouts.kt를 참조하세요.
 */

/**
 * Column 레이아웃 DSL
 */
class ColumnLayoutDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = ColumnLayoutProperty.newBuilder()
    private val propertyDsl = ColumnLayoutPropertyDsl(propertyBuilder)
    private var horizontalAlignmentSet = false
    private var verticalAlignmentSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 수평 정렬
     */
    var horizontalAlignment: HorizontalAlignment
        get() = propertyDsl.horizontalAlignment
        set(value) {
            horizontalAlignmentSet = true
            propertyDsl.horizontalAlignment = value
        }

    /**
     * 수직 정렬
     */
    var verticalAlignment: VerticalAlignment
        get() = propertyDsl.verticalAlignment
        set(value) {
            verticalAlignmentSet = true
            propertyDsl.verticalAlignment = value
        }

    /**
     * ColumnLayoutProperty 빌드
     */
    internal fun build(): ColumnLayoutProperty {
        // ViewProperty 설정 (BaseComponentDsl에서 처리)
        if (hasViewProperty()) {
            val viewProperty = buildViewProperty()
            propertyBuilder.viewProperty = viewProperty
        } else {
            propertyDsl.ViewProperty {
                viewId = scope.nextViewId()
            }
        }
        // alignment 기본값 설정
        if (!horizontalAlignmentSet) {
            propertyDsl.horizontalAlignment = H_ALIGN_START
        }
        if (!verticalAlignmentSet) {
            propertyDsl.verticalAlignment = V_ALIGN_TOP
        }
        return propertyBuilder.build()
    }
}

/**
 * Row 레이아웃 DSL
 */
class RowLayoutDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = RowLayoutProperty.newBuilder()
    private val propertyDsl = RowLayoutPropertyDsl(propertyBuilder)
    private var horizontalAlignmentSet = false
    private var verticalAlignmentSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 수평 정렬
     */
    var horizontalAlignment: HorizontalAlignment
        get() = propertyDsl.horizontalAlignment
        set(value) {
            horizontalAlignmentSet = true
            propertyDsl.horizontalAlignment = value
        }

    /**
     * 수직 정렬
     */
    var verticalAlignment: VerticalAlignment
        get() = propertyDsl.verticalAlignment
        set(value) {
            verticalAlignmentSet = true
            propertyDsl.verticalAlignment = value
        }

    /**
     * RowLayoutProperty 빌드
     */
    internal fun build(): RowLayoutProperty {
        // ViewProperty 설정 (BaseComponentDsl에서 처리)
        if (hasViewProperty()) {
            val viewProperty = buildViewProperty()
            propertyBuilder.viewProperty = viewProperty
        } else {
            propertyDsl.ViewProperty {
                viewId = scope.nextViewId()
            }
        }
        // alignment 기본값 설정
        if (!horizontalAlignmentSet) {
            propertyDsl.horizontalAlignment = H_ALIGN_START
        }
        if (!verticalAlignmentSet) {
            propertyDsl.verticalAlignment = V_ALIGN_TOP
        }
        return propertyBuilder.build()
    }
}

/**
 * Box 레이아웃 DSL
 */
class BoxLayoutDsl(
    scope: WidgetScope,
    modifier: WidgetModifier = WidgetModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = BoxLayoutProperty.newBuilder()
    private val propertyDsl = BoxLayoutPropertyDsl(propertyBuilder)
    private var contentAlignmentSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 콘텐츠 정렬
     */
    var contentAlignment: AlignmentType
        get() = propertyDsl.contentAlignment
        set(value) {
            contentAlignmentSet = true
            propertyDsl.contentAlignment = value
        }

    /**
     * BoxLayoutProperty 빌드
     */
    internal fun build(): BoxLayoutProperty {
        // ViewProperty 설정 (BaseComponentDsl에서 처리)
        if (hasViewProperty()) {
            val viewProperty = buildViewProperty()
            propertyBuilder.viewProperty = viewProperty
        } else {
            propertyDsl.ViewProperty {
                viewId = scope.nextViewId()
            }
        }
        // alignment 기본값 설정
        if (!contentAlignmentSet) {
            propertyDsl.contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_START
        }
        return propertyBuilder.build()
    }
}

