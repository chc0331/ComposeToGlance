package com.example.composetoglance.editor.widget

import com.example.dsl.WidgetScope
import com.example.widget.content.AnalogClockComponent
import com.example.widget.content.BatteryComponent
import com.example.widget.content.ButtonComponent
import com.example.widget.content.DigitalClockComponent
import com.example.widget.content.ImageComponent
import com.example.widget.content.StorageComponent
import com.example.widget.content.TextComponent

/**
 * 위젯 컴포넌트를 WidgetComponentRegistry에 등록하는 초기화 함수
 * 앱 시작 시 또는 ViewModel 초기화 시 호출해야 합니다.
 */
fun initializeWidgetComponents() {
    // 기본 카테고리 컴포넌트
    WidgetComponentRegistry.registerComponent("text") { TextComponent() }
    WidgetComponentRegistry.registerComponent("image") { ImageComponent() }
    WidgetComponentRegistry.registerComponent("button") { ButtonComponent() }
    
    // 시계 카테고리 컴포넌트
    WidgetComponentRegistry.registerComponent("analog_clock") { AnalogClockComponent() }
    WidgetComponentRegistry.registerComponent("digital_clock") { DigitalClockComponent() }
    
    // 디바이스 정보 카테고리 컴포넌트
    WidgetComponentRegistry.registerComponent("battery") { BatteryComponent() }
    WidgetComponentRegistry.registerComponent("storage") { StorageComponent() }
}

