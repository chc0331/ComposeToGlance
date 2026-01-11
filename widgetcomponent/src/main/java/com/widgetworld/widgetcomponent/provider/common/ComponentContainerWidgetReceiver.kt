package com.widgetworld.widgetcomponent.provider.common

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.battery.BatteryComponentDataStore
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

abstract class ComponentContainerWidgetReceiver : GlanceAppWidgetReceiver() {

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
        if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            initData(context, intent)
        }
    }

    protected fun initData(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.Default).launch {
            val repository = WidgetLayoutRepository(context)
            val widgetLayoutData = repository.fetchData()
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            val glanceManager = GlanceAppWidgetManager(context)
            appWidgetIds?.forEach {
                val glanceId = glanceManager.getGlanceIdBy(it)
                updateAppWidgetState(context, glanceId) {
                    it[ComponentContainerWidget.layoutKey] = widgetLayoutData.toByteArray()
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