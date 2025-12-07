import com.example.widget.component.AnalogClockComponent
import com.example.widget.component.ButtonComponent
import com.example.widget.component.DigitalClockComponent
import com.example.widget.component.ImageComponent
import com.example.widget.component.StorageComponent
import com.example.widget.component.TextComponent
import com.example.widget.component.WidgetComponent
import com.example.widget.component.battery.ui.Battery1x1
import com.example.widget.component.battery.ui.Battery2x1

fun initializeWidgetComponents() {
    WidgetComponentRegistry.registerComponent(TextComponent())
    WidgetComponentRegistry.registerComponent(ImageComponent())
    WidgetComponentRegistry.registerComponent(ButtonComponent())

    WidgetComponentRegistry.registerComponent(AnalogClockComponent())
    WidgetComponentRegistry.registerComponent(DigitalClockComponent())

    WidgetComponentRegistry.registerComponent(Battery1x1())
    WidgetComponentRegistry.registerComponent(Battery2x1())
    WidgetComponentRegistry.registerComponent(StorageComponent())
}

/**
 * 위젯 컴포넌트를 등록하고 조회하는 레지스트리
 * componentId를 키로 WidgetScope extension function을 저장합니다.
 */
object WidgetComponentRegistry {
    private val registry = mutableMapOf<String, WidgetComponent>()

    /**
     * 위젯 컴포넌트를 등록합니다.
     * @param componentId 컴포넌트 고유 ID
     * @param component DSL 컴포넌트 함수
     */
    fun registerComponent(widget: WidgetComponent) {
        registry[widget.getWidgetTag()] = widget
    }

    /**
     * 등록된 컴포넌트를 조회합니다.
     * @param componentId 컴포넌트 고유 ID
     * @return DSL 컴포넌트 함수, 없으면 null
     */
    fun getComponent(tag: String): WidgetComponent? {
        return registry[tag]
    }

    fun getAllComponents(): List<WidgetComponent> {
        return registry.values.toList()
    }

    /**
     * 모든 등록된 컴포넌트 ID 목록을 반환합니다.
     */
    fun getAllComponentIds(): Set<String> {
        return registry.keys.toSet()
    }
}