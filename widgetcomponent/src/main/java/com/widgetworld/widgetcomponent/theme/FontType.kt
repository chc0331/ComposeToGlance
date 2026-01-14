package com.widgetworld.widgetcomponent.theme

import android.util.Log

/**
 * 위젯 컴포넌트에서 사용할 폰트 타입 정의
 * Material Design 3 Typography를 기반으로 하되, 위젯의 작은 공간에 맞게 조정된 크기
 * 
 * 각 FontType은 고정된 sp 크기를 가지며, 위젯 크기와 무관하게 동일한 크기를 유지합니다.
 * fontScaleFactor를 통해 전체 폰트 크기를 조정할 수 있습니다.
 */
enum class FontType(val sp: Float) {
    // Display - 큰 숫자/시간 표시용
    DisplayLarge(32f),
    DisplayMedium(28f),
    DisplaySmall(24f),
    
    // Headline - 위젯 제목
    HeadlineLarge(20f),
    HeadlineMedium(18f),
    HeadlineSmall(16f),
    
    // Title - 섹션 제목
    TitleLarge(16f),
    TitleMedium(14f),
    TitleSmall(12f),
    
    // Body - 본문 텍스트
    BodyLarge(15f),
    BodyMedium(13f),
    BodySmall(12f),
    
    // Label - 라벨 텍스트
    LabelLarge(11f),
    LabelMedium(10f),
    LabelSmall(8f),
    
    // Caption - 작은 설명 텍스트
    Caption(8f);
    
    companion object {
        private const val TAG = "FontType"
        
        /**
         * 폰트 크기 스케일 팩터 (0.7f ~ 1.3f 범위)
         * 기본값은 1.0f입니다.
         */
        @Volatile
        private var _fontScaleFactor: Float = 1.0f
        
        /**
         * 애노테이션에서 읽은 범위 값을 캐싱
         */
        private val range: Pair<Float, Float> by lazy {
            val annotation = FontType::class.java
                .getDeclaredMethod("setFontScaleFactor", Float::class.java)
                .getAnnotation(FontScaleRange::class.java)
            
            val min = annotation?.min ?: 0.7f
            val max = annotation?.max ?: 1.3f
            min to max
        }
        
        /**
         * 현재 설정된 폰트 스케일 팩터를 반환합니다.
         */
        val fontScaleFactor: Float
            get() = _fontScaleFactor
        
        /**
         * 폰트 스케일 팩터를 설정합니다.
         * 범위를 벗어난 경우 경고를 출력하고 값을 클램핑합니다.
         * 
         * @param scale 0.7f ~ 1.3f 범위의 스케일 값
         */
        @FontScaleRange(min = 0.7f, max = 1.3f)
        fun setFontScaleFactor(scale: Float) {
            val (min, max) = range
            
            when {
                scale < min -> {
                    Log.w(TAG, "Font scale factor $scale is below minimum $min. Clamping to $min.")
                    _fontScaleFactor = min
                }
                scale > max -> {
                    Log.w(TAG, "Font scale factor $scale is above maximum $max. Clamping to $max.")
                    _fontScaleFactor = max
                }
                else -> {
                    _fontScaleFactor = scale
                }
            }
        }
        
        /**
         * 폰트 스케일 팩터를 기본값(1.0f)으로 리셋합니다.
         */
        fun resetFontScaleFactor() {
            _fontScaleFactor = 1.0f
        }
    }
}

/**
 * FontType의 sp 크기를 반환하는 extension property
 * fontScaleFactor가 적용된 값을 반환합니다.
 * 위젯 컴포넌트에서 `FontType.LabelLarge.size` 형태로 사용 가능
 */
val FontType.value: Float
    get() = this.sp * FontType.fontScaleFactor

/**
 * 폰트 스케일 팩터의 유효 범위를 지정하는 애노테이션
 *
 * @property min 최소값 (기본값: 0.7f)
 * @property max 최대값 (기본값: 1.3f)
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class FontScaleRange(
    val min: Float = 0.7f,
    val max: Float = 1.3f
)
