package com.example.widget.component.battery

import android.R.attr.textSize
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.dsl.WidgetScope
import com.example.dsl.component.Image
import com.example.dsl.component.Progress
import com.example.dsl.component.Text
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.TextAlign
import com.example.dsl.provider.DslLocalPreview
import com.example.dsl.provider.DslLocalSize
import com.example.dsl.provider.DslLocalState
import com.example.widget.R
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent


abstract class BatteryComponent : WidgetComponent() {

    companion object {
        internal val batteryValueKey = floatPreferencesKey("battery_value")
        internal val chargingStateKey = booleanPreferencesKey("charging_state")
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

    protected fun WidgetScope.CircularProgress() {
        fun WidgetScope.getProgressSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.55f
        }

        val batteryValue = getBatteryValue()
        Progress({
            ViewProperty {
                Width {
                    Dp {
                        value = getProgressSize()
                    }
                }
                Height {
                    Dp {
                        value = getProgressSize()
                    }
                }
            }
            progressType = ProgressType.PROGRESS_TYPE_CIRCULAR
            progressValue = batteryValue
            maxValue = 100f
            ProgressColor {
                Color {
                    resId = R.color.battery_gauge_sufficient_color
                }
            }
            BackgroundColor {
                Color {
                    argb = Color.LightGray.toArgb()
                }
            }
        })

    }

    protected fun WidgetScope.MobileDevice() {
        fun WidgetScope.getBatteryIconSize(): Float {
            val size = getLocal(DslLocalSize) as DpSize
            return size.height.value * 0.24f
        }
        Image {
            ViewProperty {
                Width { Dp { value = getBatteryIconSize() } }
                Height { Dp { value = getBatteryIconSize() } }
            }
            Provider {
                drawableResId = R.drawable.ic_mobile_device
            }

            animation = getChargingState()
            infiniteLoop = getChargingState()
        }
    }

    protected fun WidgetScope.BatteryText() {
        // 배터리 값에서 숫자 추출 (예: "50%" -> 50)
        val batteryValueText = "${getBatteryValue()}%"
        val size = getLocal(DslLocalSize) as DpSize
        val textSize = size.height.value * 0.2f
        Text({
            ViewProperty {
                viewId = R.id.batteryValue
                partiallyUpdate = true
            }
            TextContent {
                text = batteryValueText
            }
            fontSize = textSize
            fontWeight = FontWeight.FONT_WEIGHT_BOLD
            FontColor {
                Color {
                    argb = Color.Black.toArgb()
                }
            }
            textAlign = TextAlign.TEXT_ALIGN_CENTER
        })
    }

//    protected fun WidgetScope.getBatteryIcon(): Int {
//        val currentState = getLocal(DslLocalState) as Preferences? ?: null
//        val isPreview = getLocal(DslLocalPreview) as Boolean? ?: false
//        if (isPreview) {
//            return R.drawable.ic_mobile_device
//        }
//        return currentState?.let { state ->
//            val currentValue = state[batteryIconKey] as Int?
//            currentValue ?: 0
//        } ?: 0
//    }

    protected fun WidgetScope.getBatteryValue(): Float {
        val currentState = getLocal(DslLocalState)
        val isPreview = getLocal(DslLocalPreview) ?: false
        if (isPreview) {
            return 50f
        }
        val value = currentState?.let { state ->
            val currentValue = state[batteryValueKey]
            currentValue ?: 0f
        } ?: 0f

        return value
    }

    protected fun WidgetScope.getChargingState(): Boolean {
        val currentState = getLocal(DslLocalState)
        val isPreview = getLocal(DslLocalPreview) ?: false
        if (isPreview) {
            return false
        }
        return currentState?.let { state ->
            val currentValue = state[chargingStateKey]
            currentValue ?: false
        } ?: false
    }

//    protected fun WidgetScope.getChargingAnimation(): Boolean {
//        val currentState = getLocal(DslLocalState) as Preferences? ?: null
//        val isPreview = getLocal(DslLocalPreview) as Boolean? ?: false
//        if (isPreview) {
//            return false
//        }
//        return currentState?.let { state ->
//            val currentValue = state[chargingAnimationKey] as Boolean?
//            currentValue ?: false
//        } ?: false
//    }
}