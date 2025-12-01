package com.example.widget.component.battery

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.BatteryManager
import android.util.Log
import android.widget.RemoteViews
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceRemoteViews
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.widget.R
import com.example.widget.provider.LargeAppWidget
import com.example.widget.provider.LargeWidgetProvider
import kotlinx.coroutines.Dispatchers


object BatteryUpdateManager {

    suspend fun updateBatteryWidgetState(context: Context, data: BatteryData) {
        val isCharging = data.isCharging
        val batteryLevel = data.level
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryState =
            if (isCharging) BatteryState.CHARGING else BatteryState.getState(batteryLevel)

        with(Dispatchers.Default) {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(
                    context, LargeWidgetProvider::class.java
                )
            ).forEach { id ->
                val glanceId = glanceAppWidgetManager.getGlanceIdBy(id)
                updateAppWidgetState(context, glanceId) { pref ->
                    pref[BatteryComponent.batteryValueKey] = batteryLevel
                    pref[BatteryComponent.chargingStateKey] = false
                }

                val remoteViews = RemoteViews(context.packageName, R.layout.root_layout)
                remoteViews.setTextViewText(R.id.batteryValue, "${batteryLevel}%")
                Log.i("heec.choi","update : $id $batteryLevel")
                //todo : partiallyUpdate 확인
                AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(id, remoteViews)
            }
        }
    }
}

data class BatteryData(
    val level: Float,
    val status: String,
    val health: String,
    val isCharging: Boolean,
)

enum class BatteryState(
    val range: Pair<Float, Float>,
    val state: String,
    val animate: Boolean = false,
    val resId: Int = 0,
) {
    RISKY(Pair(0f, 10f), "위험", resId = R.color.battery_gauge_warning_color),
    WARNING(Pair(10f, 50f), "경고", resId = R.color.battery_gauge_insufficient_color),
    SUFFICIENT(Pair(50f, 75f), "보통", resId = R.color.battery_gauge_sufficient_color),
    ENOUGH(Pair(75f, 100f), "충분", resId = R.color.battery_gauge_sufficient_color),
    FULL(Pair(100f, 100f), "충전됨", resId = R.color.battery_gauge_sufficient_color),
    CHARGING(Pair(100f, 100f), "충전중", true, resId = R.color.battery_gauge_sufficient_color);

    companion object {
        fun getState(value: Float): BatteryState {
            return if (RISKY.include(value)) RISKY
            else if (WARNING.include(value)) WARNING
            else if (SUFFICIENT.include(value)) SUFFICIENT
            else if (ENOUGH.include(value)) ENOUGH
            else FULL
        }
    }

    private fun include(value: Float): Boolean {
        return (value >= range.first && value < range.second)
    }
}