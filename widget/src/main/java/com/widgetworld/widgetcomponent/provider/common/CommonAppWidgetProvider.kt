package com.widgetworld.widgetcomponent.provider.common

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.battery.BatteryComponentDataStore
import com.widgetworld.widgetcomponent.component.battery.BatteryData
import com.widgetworld.widgetcomponent.component.battery.BatteryStatusReceiver
import com.widgetworld.widgetcomponent.component.battery.BatteryUpdateManager
import com.widgetworld.widgetcomponent.component.battery.bluetooth.checkBluetoothBatteryComponentExist
import com.widgetworld.widgetcomponent.component.battery.bluetooth.earbuds.EarbudsBatteryDataStore
import com.widgetworld.widgetcomponent.component.battery.bluetooth.earbuds.EarbudsBatteryUpdateManager
import com.widgetworld.widgetcomponent.component.battery.bluetooth.watch.WatchBatteryDataStore
import com.widgetworld.widgetcomponent.component.battery.bluetooth.watch.WatchBatteryUpdateManager
import com.widgetworld.widgetcomponent.component.battery.checkBatteryComponentExist
import com.widgetworld.widgetcomponent.component.devicecare.DeviceStateCollector
import com.widgetworld.widgetcomponent.component.devicecare.ram.RamData
import com.widgetworld.widgetcomponent.component.devicecare.ram.RamUpdateManager
import com.widgetworld.widgetcomponent.component.devicecare.ram.checkRamWidgetExist
import com.widgetworld.widgetcomponent.repository.WidgetLayoutRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

abstract class CommonWidgetProvider : GlanceAppWidgetReceiver() {

    /**
     * 컴포넌트 이름을 반환하는 추상 메서드
     */
    abstract fun getComponentName(context: Context): ComponentName

    /**
     * 로그 태그를 반환하는 추상 메서드
     */
    abstract fun getTag(): String


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.i(getTag(), "onReceive / ${intent.action}")
        if (intent.action == "com.example.widget.test") {
            handleTestAction(context)
        } else if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            initData(context, intent)
        }
    }

    /**
     * 테스트 액션을 처리하는 공통 메서드
     */
    private fun handleTestAction(context: Context) {
        BatteryStatusReceiver().onReceive(
            context,
            Intent().apply {
                action = Intent.ACTION_BATTERY_CHANGED
                putExtra(BatteryManager.EXTRA_LEVEL, Random.nextInt(10))
                putExtra(BatteryManager.EXTRA_SCALE, 10)
                putExtra(BatteryManager.EXTRA_STATUS, 2)
            }
        )
        AppWidgetManager.getInstance(context).getAppWidgetIds(
            getComponentName(context)
        ).forEach {
            val randomInt = Random.nextInt(100)
            val tempData = BatteryData(randomInt.toFloat(), false)
            BatteryUpdateManager.updateAppWidget(context, it, tempData)
        }
    }

    /**
     * 위젯 데이터를 초기화하는 공통 메서드
     */
    protected fun initData(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.Default).launch {
            val repository = WidgetLayoutRepository(context)
            val widgetLayoutData = repository.fetchData()
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            val glanceManager = GlanceAppWidgetManager(context)
            appWidgetIds?.forEach {
                val glanceId = glanceManager.getGlanceIdBy(it)
                updateAppWidgetState(context, glanceId) {
                    it[CommonAppWidget.layoutKey] = widgetLayoutData.toByteArray()
                }
                if (widgetLayoutData.checkBluetoothBatteryComponentExist()) {
                    val earbudsBatteryData = EarbudsBatteryDataStore.loadData(context)
                    EarbudsBatteryUpdateManager.syncState(context, earbudsBatteryData)
                    val watchBatteryData = WatchBatteryDataStore.loadData(context)
                    WatchBatteryUpdateManager.syncState(context, watchBatteryData)
                }
                if (widgetLayoutData.checkBatteryComponentExist()) {
                    val batteryData = BatteryComponentDataStore.loadData(context)
                    BatteryUpdateManager.syncState(context, batteryData)
                }
                if (widgetLayoutData.checkRamWidgetExist()) {
                    val syncData = DeviceStateCollector.collect(context)
                    val ramUsage = (syncData.memoryUsage * 100f) / syncData.totalMemory
                    val ramData = RamData(ramUsage)
                    RamUpdateManager.syncState(context, data = ramData)
                }
//                if (widgetLayoutData.checkStorageWidgetExist()) {
//                    StorageUpdateManager.syncComponentState(context)
//                }
            }
        }
    }
}

