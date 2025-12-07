import com.example.widget.component.AnalogClockComponent
import com.example.widget.component.ButtonComponent
import com.example.widget.component.DigitalClockComponent
import com.example.widget.component.ImageComponent
import com.example.widget.component.StorageComponent
import com.example.widget.component.TextComponent
import com.example.widget.component.WidgetComponent
import com.example.widget.component.ViewIdAllocator
import com.example.widget.component.battery.BatteryWidget
import com.example.widget.component.battery.bluetooth.BluetoothBatteryWidget
import android.util.Log

object WidgetComponentRegistry {
    private const val TAG = "WidgetComponentRegistry"
    
    private val registry = mutableMapOf<String, WidgetComponent>()
    private val viewIdAllocator = ViewIdAllocator()

    init {
        registerComponent(TextComponent())
        registerComponent(ImageComponent())
        registerComponent(ButtonComponent())

        registerComponent(AnalogClockComponent())
        registerComponent(DigitalClockComponent())

        registerComponent(BatteryWidget())
        registerComponent(BluetoothBatteryWidget())
        registerComponent(StorageComponent())
    }

    /**
     * 위젯 컴포넌트를 등록합니다.
     * View ID가 필요한 컴포넌트는 자동으로 ID 범위를 할당받습니다.
     * @param widget 등록할 컴포넌트
     */
    fun registerComponent(widget: WidgetComponent) {
        val tag = widget.getWidgetTag()
        registry[tag] = widget
        
        // View ID가 필요한 컴포넌트만 할당
        if (widget.getViewIdTypes().isNotEmpty()) {
            val allocation = viewIdAllocator.allocate(widget)
            Log.d(TAG, "Registered component with View IDs: $tag -> $allocation")
        } else {
            Log.d(TAG, "Registered component without View IDs: $tag")
        }
    }

    /**
     * 등록된 컴포넌트를 조회합니다.
     * @param tag 컴포넌트 태그
     * @return 컴포넌트, 없으면 null
     */
    fun getComponent(tag: String): WidgetComponent? {
        return registry[tag]
    }

    /**
     * 모든 등록된 컴포넌트를 반환합니다.
     * @return 컴포넌트 리스트
     */
    fun getAllComponents(): List<WidgetComponent> {
        return registry.values.toList()
    }

    /**
     * 모든 등록된 컴포넌트 ID 목록을 반환합니다.
     * @return 컴포넌트 태그 Set
     */
    fun getAllComponentIds(): Set<String> {
        return registry.keys.toSet()
    }
    
    /**
     * 컴포넌트의 Base View ID를 조회합니다.
     * @param componentTag 컴포넌트 태그
     * @return Base View ID
     * @throws IllegalStateException 등록되지 않았거나 View ID가 할당되지 않은 경우
     */
    fun getBaseViewId(componentTag: String): Int {
        if (!registry.containsKey(componentTag)) {
            throw IllegalStateException("Component not registered: $componentTag")
        }
        return viewIdAllocator.getBaseId(componentTag)
    }
    
    /**
     * ViewIdAllocator 인스턴스를 조회합니다.
     * (디버깅 및 테스트 용도)
     * @return ViewIdAllocator 인스턴스
     */
    fun getViewIdAllocator(): ViewIdAllocator = viewIdAllocator
}

