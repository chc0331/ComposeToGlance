package com.example.widget.component.battery

import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Image
import com.example.dsl.provider.DslLocalSize
import com.example.widget.R
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent


abstract class BatteryComponent : WidgetComponent() {

    enum class BatteryState {
        LEVEL_2, LEVEL_25, LEVEL_50, LEVEL_75, LEVEL_100, CHARGING
    }

    fun getCellType(): String {
        return if (getSizeType() == SizeType.TINY) "1x1" else "2x1"
    }

    override fun getName(): String {
        return "${getSizeType()}-Battery"
    }

    override fun getDescription(): String {
        return "${getSizeType()}-Battery"
    }

    override fun getWidgetCategory(): WidgetCategory {
        return WidgetCategory.DEVICE_INFO
    }

    override fun getWidgetTag(): String {
        return "${getSizeType()}-Battery"
    }

    protected fun WidgetScope.getBatteryIconSize(): Float {
        val size = getLocal(DslLocalSize) as DpSize
        return size.height.value * 0.6f
    }

    protected fun WidgetScope.BatteryIcon(state: BatteryState, infinite: Boolean = false) {

        val resId = when (state) {
            BatteryState.CHARGING -> R.layout.battery_charging_avd
            BatteryState.LEVEL_2 -> R.drawable.ic_battery_2
            BatteryState.LEVEL_25 -> R.drawable.ic_battery_25
            BatteryState.LEVEL_50 -> R.drawable.ic_battery_50
            BatteryState.LEVEL_75 -> R.drawable.ic_battery_75
            else -> R.drawable.ic_battery_100
        }

        Image {
            ViewProperty {
                Width { Dp { value = getBatteryIconSize() } }
                Height { Dp { value = getBatteryIconSize() } }
            }
            Provider {
                drawableResId = resId
            }

            animation = state == BatteryState.CHARGING
            infiniteLoop = infinite
        }
    }
}