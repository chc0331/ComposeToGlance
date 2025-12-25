package com.widgetkit.core.provider

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalCellHeight
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalCellWidth
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContentPadding
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContentRadius
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalProvider
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalRootPadding
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalState
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalTheme
import com.widgetkit.dsl.theme.GlanceThemeConverter
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.cornerRadius
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.padding
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.core.WidgetComponentRegistry
import com.widgetkit.core.component.battery.BatteryData
import com.widgetkit.core.component.battery.BatteryStatusReceiver
import com.widgetkit.core.component.battery.BatteryUpdateManager
import com.widgetkit.core.component.battery.bluetooth.BluetoothBatteryUpdateManager
import com.widgetkit.core.component.battery.bluetooth.checkBluetoothBatteryComponentExist
import com.widgetkit.core.component.battery.checkBatteryComponentExist
import com.widgetkit.core.component.devicecare.ram.RamUpdateManager
import com.widgetkit.core.component.devicecare.ram.checkRamWidgetExist
import com.widgetkit.core.component.devicecare.storage.StorageUpdateManager
import com.widgetkit.core.component.devicecare.storage.checkStorageWidgetExist
import com.widgetkit.core.proto.PlacedWidgetComponent
import com.widgetkit.core.proto.WidgetLayout
import com.widgetkit.core.repository.WidgetLayoutRepository
import com.widgetkit.dsl.frontend.layout.Box
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

val layoutKey = byteArrayPreferencesKey("layout_key")

class LargeAppWidget : DslAppWidget() {

    companion object {
        private const val ROOT_PADDING = 8.0f
        private const val CONTENT_PADDING = 2.0f
    }

    override val sizeMode: SizeMode
        get() = SizeMode.Exact

    override fun WidgetScope.DslContent() {
        val currentState = getLocal(WidgetLocalState) as Preferences?
        val currentLayout = WidgetLayout.parseFrom(currentState?.get(layoutKey))
        val widgetSize = getLocal(WidgetLocalSize) as DpSize
        val context = getLocal(WidgetLocalContext) as Context
        val glanceTheme = GlanceThemeConverter.createDefaultTheme(context)

        val cellWidth = (widgetSize.width - ROOT_PADDING.dp * 2) / 4
        val cellHeight = (widgetSize.height - ROOT_PADDING.dp * 2) / 2

        WidgetLocalProvider(
            WidgetLocalRootPadding provides ROOT_PADDING.dp,
            WidgetLocalContentPadding provides CONTENT_PADDING.dp,
            WidgetLocalCellWidth provides cellWidth,
            WidgetLocalCellHeight provides cellHeight,
            WidgetLocalTheme provides glanceTheme
        ) {
            Box(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentProperty = {
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_START
                }
            ) {
                currentLayout.placedWidgetComponentList.forEach {
                    GridItem(it)
                }
            }
        }
    }

    private fun WidgetScope.GridItem(widget: PlacedWidgetComponent) {
        val rootPadding = getLocal(WidgetLocalRootPadding) as Dp
        val contentPadding = getLocal(WidgetLocalContentPadding) as Dp
        val cellWidth = getLocal(WidgetLocalCellWidth)
        val cellHeight = getLocal(WidgetLocalCellHeight)
        val gridIndex = widget.gridIndex

        val topMargin = rootPadding + (cellHeight?.times((gridIndex - 1) / 4) ?: 0.dp)
        val leftMargin = rootPadding + (cellWidth?.times((gridIndex - 1) % 4) ?: 0.dp)
        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = leftMargin.value, top = topMargin.value)
                .backgroundColor(Color.Transparent.toArgb())
        ) {
            val componentWidth = cellWidth?.times(widget.colSpan) ?: 0.dp
            val componentHeight = cellHeight?.times(widget.rowSpan) ?: 0.dp
            Box(
                modifier = WidgetModifier
                    .width(componentWidth.value)
                    .height(componentHeight.value)
                    .padding(
                        start = contentPadding.value,
                        top = contentPadding.value,
                        bottom = contentPadding.value,
                        end = contentPadding.value
                    )
            ) {
                val context = getLocal(WidgetLocalContext) as Context
                val glanceTheme = GlanceThemeConverter.createDefaultTheme(context)
                
                WidgetLocalProvider(
                    WidgetLocalSize provides DpSize(
                        componentWidth - contentPadding * 2,
                        componentHeight - contentPadding * 2
                    ),
                    WidgetLocalGridIndex provides gridIndex,
                    WidgetLocalTheme provides glanceTheme
                ) {
                    val contentRadius = getLocal(WidgetLocalContentRadius) ?: 0.dp
                    WidgetComponentRegistry.getComponent(widget.widgetTag)?.let {
                        Box(
                            modifier = WidgetModifier
                                .cornerRadius(contentRadius.value)
                        ) {
                            it.renderContent(this)
                        }
                    }
                }
            }
        }
    }
}

class LargeWidgetProvider : GlanceAppWidgetReceiver() {

    companion object {
        private const val TAG = "LargeWidgetProvider"
    }

    override val glanceAppWidget: GlanceAppWidget
        get() = LargeAppWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.i(TAG, "onReceive / ${intent.action}")
        if (intent.action == "com.example.widget.test") {
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
                ComponentName(
                    context,
                    LargeWidgetProvider::class.java.name
                )
            ).forEach {
                val randomInt = Random.nextInt(100)
                val tempData = BatteryData(randomInt.toFloat(), false)
                BatteryUpdateManager.updateAppWidget(context, it, tempData)
            }
        } else if (intent.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            initData(context, intent)
        }
    }

    private fun initData(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.Default).launch {
            val repository = WidgetLayoutRepository(context)
            val widgetLayoutData = repository.fetchData()
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            val glanceManager = GlanceAppWidgetManager(context)
            appWidgetIds?.forEach {
                val glanceId = glanceManager.getGlanceIdBy(it)
                updateAppWidgetState(context, glanceId) {
                    it[layoutKey] = widgetLayoutData.toByteArray()
                }
                if (widgetLayoutData.checkBluetoothBatteryComponentExist()) {
                    BluetoothBatteryUpdateManager.syncComponentState(context)
                }
                if (widgetLayoutData.checkBatteryComponentExist()) {
                    BatteryUpdateManager.syncComponentState(context)
                }
                if (widgetLayoutData.checkRamWidgetExist()) {
                    RamUpdateManager.syncComponentState(context)
                }
                if (widgetLayoutData.checkStorageWidgetExist()) {
                    StorageUpdateManager.syncComponentState(context)
                }
            }
        }
    }
}
