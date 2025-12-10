package com.example.dsl.syntax

import com.example.dsl.WidgetScope
import com.example.dsl.modifier.DslModifier
import com.example.dsl.proto.ButtonProperty
import com.example.dsl.proto.ContentScale
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.ImageProperty
import com.example.dsl.proto.ProgressProperty
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.ProgressType.PROGRESS_TYPE_LINEAR
import com.example.dsl.proto.SpacerProperty
import com.example.dsl.proto.TextAlign
import com.example.dsl.proto.TextProperty
import com.example.dsl.syntax.ColorDsl

/**
 * 컴포넌트 DSL 클래스
 *
 * 이 파일은 각 컴포넌트의 DSL 클래스를 포함합니다.
 * - TextDsl: Text 컴포넌트의 DSL 클래스
 * - ImageDsl: Image 컴포넌트의 DSL 클래스
 * - ButtonDsl: Button 컴포넌트의 DSL 클래스
 * - ProgressDsl: Progress 컴포넌트의 DSL 클래스
 * - SpacerDsl: Spacer 컴포넌트의 DSL 클래스
 *
 * 최상위 컴포넌트 DSL 함수는 Components.kt를 참조하세요.
 */

/**
 * Text 컴포넌트 DSL
 */
class TextDsl(
    scope: WidgetScope,
    modifier: DslModifier = DslModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = TextProperty.newBuilder()
    private val propertyDsl = TextPropertyDsl(propertyBuilder)
    private var textSet = false
    private var fontColorSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 텍스트 내용 설정 블록
     */
    fun TextContent(block: TextContentDsl.() -> Unit) {
        textSet = true
        propertyDsl.Text(block)
    }

    /**
     * 최대 줄 수
     */
    var maxLine: Int
        get() = propertyDsl.maxLine
        set(value) {
            propertyDsl.maxLine = value
        }

    /**
     * 폰트 색상 설정 블록
     */
    fun FontColor(block: ColorProviderDsl.() -> Unit) {
        fontColorSet = true
        propertyDsl.FontColor(block)
    }

    /**
     * 폰트 크기
     */
    var fontSize: Float
        get() = propertyDsl.fontSize
        set(value) {
            propertyDsl.fontSize = value
        }

    /**
     * 폰트 두께
     */
    var fontWeight: FontWeight
        get() = propertyDsl.fontWeight
        set(value) {
            propertyDsl.fontWeight = value
        }

    /**
     * 텍스트 정렬
     */
    var textAlign: TextAlign
        get() = propertyDsl.textAlign
        set(value) {
            propertyDsl.textAlign = value
        }

    /**
     * TextProperty 빌드
     */
    internal fun build(): TextProperty {
        // ViewProperty 설정 (BaseComponentDsl에서 처리)
        // ViewProperty 블록이 있으면 propertyDsl.ViewProperty에서 처리하고,
        // 없으면 buildViewProperty()의 결과를 직접 설정
        if (hasViewProperty()) {
            val viewProperty = buildViewProperty()
            // ViewProperty 블록이 설정되었는지 확인
            // propertyDsl.ViewProperty는 블록을 받아서 처리하므로,
            // buildViewProperty()의 결과를 propertyBuilder에 직접 설정
            propertyBuilder.viewProperty = viewProperty
        } else {
            // ViewProperty가 설정되지 않았으면 기본값으로 설정
            propertyDsl.ViewProperty {
                viewId = scope.nextViewId()
            }
        }
        
        if (!textSet) {
            propertyDsl.Text {
                text = ""
            }
        }
        if (!fontColorSet) {
            propertyDsl.FontColor {
                Color {
                    argb = 0xFF000000.toInt()
                }
            }
        }
        return propertyBuilder.build()
    }
}

/**
 * Image 컴포넌트 DSL
 */
class ImageDsl(
    scope: WidgetScope,
    modifier: DslModifier = DslModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = ImageProperty.newBuilder()
    private val propertyDsl = ImagePropertyDsl(propertyBuilder)
    private var providerSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 이미지 제공자 설정 블록
     */
    fun Provider(block: ImageProviderDsl.() -> Unit) {
        providerSet = true
        propertyDsl.Provider(block)
    }

    /**
     * 틴트 색상 설정 블록
     */
    fun TintColor(block: ColorDsl.() -> Unit) {
        propertyDsl.TintColor(block)
    }

    /**
     * 투명도
     */
    var alpha: Float
        get() = propertyDsl.alpha
        set(value) {
            propertyDsl.alpha = value
        }

    /**
     * 콘텐츠 스케일
     */
    var contentScale: ContentScale
        get() = propertyDsl.contentScale
        set(value) {
            propertyDsl.contentScale = value
        }

    var animation: Boolean
        get() = propertyDsl.animation
        set(value) {
            propertyDsl.animation = value
        }

    var infiniteLoop: Boolean
        get() = propertyDsl.infiniteLoop
        set(value) {
            propertyDsl.infiniteLoop = value
        }

    /**
     * ImageProperty 빌드
     */
    internal fun build(): ImageProperty {
        // ViewProperty 설정 (BaseComponentDsl에서 처리)
        if (hasViewProperty()) {
            val viewProperty = buildViewProperty()
            propertyBuilder.viewProperty = viewProperty
        } else {
            propertyDsl.ViewProperty {
                viewId = scope.nextViewId()
            }
        }
        
        if (!providerSet) {
            throw IllegalArgumentException("Image provider must be set. Use provider { drawableResId = ... } or provider { uri = ... } or provider { bitmap(...) }")
        }
        return propertyBuilder.build()
    }
}

/**
 * Button 컴포넌트 DSL
 */
class ButtonDsl(
    scope: WidgetScope,
    modifier: DslModifier = DslModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = ButtonProperty.newBuilder()
    private val propertyDsl = ButtonPropertyDsl(propertyBuilder)
    private var textSet = false
    private var fontColorSet = false

    init {
        this.modifier(modifier)
    }

    /**
     * 텍스트 내용 설정 블록
     */
    fun Text(block: TextContentDsl.() -> Unit) {
        textSet = true
        propertyDsl.Text(block)
    }

    /**
     * 텍스트 내용 직접 설정
     */
//    var text: String
//        get() = if (propertyBuilder.hasText()) propertyBuilder.text.text else ""
//        set(value) {
//            textSet = true
//            propertyDsl.text {
//                this.text = value
//            }
//        }

    /**
     * 최대 줄 수
     */
    var maxLine: Int
        get() = propertyDsl.maxLine
        set(value) {
            propertyDsl.maxLine = value
        }

    /**
     * 폰트 색상 설정 블록
     */
    fun FontColor(block: ColorProviderDsl.() -> Unit) {
        fontColorSet = true
        propertyDsl.FontColor(block)
    }

    /**
     * 폰트 크기
     */
    var fontSize: Float
        get() = propertyDsl.fontSize
        set(value) {
            propertyDsl.fontSize = value
        }

    /**
     * 폰트 두께
     */
    var fontWeight: FontWeight
        get() = propertyDsl.fontWeight
        set(value) {
            propertyDsl.fontWeight = value
        }

    /**
     * 배경 색상 설정 블록
     */
    fun BackgroundColor(block: ColorProviderDsl.() -> Unit) {
        propertyDsl.BackgroundColor(block)
    }

    /**
     * ButtonProperty 빌드
     */
    internal fun build(): ButtonProperty {
        // ViewProperty 설정 (BaseComponentDsl에서 처리)
        if (hasViewProperty()) {
            val viewProperty = buildViewProperty()
            propertyBuilder.viewProperty = viewProperty
        } else {
            propertyDsl.ViewProperty {
                viewId = scope.nextViewId()
            }
        }
        
        if (!textSet) {
            throw IllegalArgumentException("Button text must be set")
        }
        if (!fontColorSet) {
            propertyDsl.FontColor {
                Color {
                    argb = 0xFFFFFFFF.toInt()
                }
            }
        }
        return propertyBuilder.build()
    }
}

/**
 * Progress 컴포넌트 DSL
 */
class ProgressDsl(
    scope: WidgetScope,
    modifier: DslModifier = DslModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = ProgressProperty.newBuilder()
    private val propertyDsl = ProgressPropertyDsl(propertyBuilder)
    private var progressColorSet = false
    private var backgroundColorSet = false

    init {
        this.modifier(modifier)
        // 기본값 설정
        propertyDsl.progressType = PROGRESS_TYPE_LINEAR
        propertyDsl.maxValue = 100f
        propertyDsl.progressValue = 0f
    }

    /**
     * 진행률 타입
     */
    var progressType: ProgressType
        get() = propertyDsl.progressType
        set(value) {
            propertyDsl.progressType = value
        }

    /**
     * 최대값
     */
    var maxValue: Float
        get() = propertyDsl.maxValue
        set(value) {
            propertyDsl.maxValue = value
        }

    /**
     * 진행률 값
     */
    var progressValue: Float
        get() = propertyDsl.progressValue
        set(value) {
            propertyDsl.progressValue = value
        }

    /**
     * 진행률 색상 설정 블록
     */
    fun ProgressColor(block: ColorProviderDsl.() -> Unit) {
        progressColorSet = true
        propertyDsl.ProgressColor(block)
    }

    /**
     * 배경 색상 설정 블록
     */
    fun BackgroundColor(block: ColorProviderDsl.() -> Unit) {
        backgroundColorSet = true
        propertyDsl.BackgroundColor(block)
    }

    /**
     * ProgressProperty 빌드
     */
    internal fun build(): ProgressProperty {
        // ViewProperty 설정 (BaseComponentDsl에서 처리)
        if (hasViewProperty()) {
            val viewProperty = buildViewProperty()
            propertyBuilder.viewProperty = viewProperty
        } else {
            propertyDsl.ViewProperty {
                viewId = scope.nextViewId()
            }
        }
        
        if (!progressColorSet) {
            propertyDsl.ProgressColor {
                Color {
                    argb = 0xFFFFFFFF.toInt()
                }
            }
        }
        if (!backgroundColorSet) {
            propertyDsl.BackgroundColor {
                Color {
                    argb = 0xFFE0E0E0.toInt()
                }
            }
        }
        return propertyBuilder.build()
    }
}

/**
 * Spacer 컴포넌트 DSL
 */
class SpacerDsl(
    scope: WidgetScope,
    modifier: DslModifier = DslModifier
) : BaseComponentDsl(scope) {
    private val propertyBuilder = SpacerProperty.newBuilder()
    private val propertyDsl = SpacerPropertyDsl(propertyBuilder)

    init {
        this.modifier(modifier)
    }

    /**
     * SpacerProperty 빌드
     */
    internal fun build(): SpacerProperty {
        // ViewProperty 설정 (BaseComponentDsl에서 처리)
        if (hasViewProperty()) {
            val viewProperty = buildViewProperty()
            propertyBuilder.viewProperty = viewProperty
        } else {
            propertyDsl.ViewProperty {
                viewId = scope.nextViewId()
            }
        }
        return propertyBuilder.build()
    }
}

