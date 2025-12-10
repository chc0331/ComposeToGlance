package com.example.widget.provider

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
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.modifier.*
import com.example.dsl.proto.AlignmentType
import com.example.dsl.localprovider.WidgetLocalCellHeight
import com.example.dsl.localprovider.WidgetLocalCellWidth
import com.example.dsl.localprovider.WidgetLocalContentPadding
import com.example.dsl.localprovider.WidgetLocalContentRadius
import com.example.dsl.localprovider.WidgetLocalGridIndex
import com.example.dsl.localprovider.WidgetLocalProvider
import com.example.dsl.localprovider.WidgetLocalRootPadding
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.localprovider.WidgetLocalState
import com.example.widget.WidgetComponentRegistry
import com.example.widget.component.battery.BatteryData
import com.example.widget.component.battery.BatteryStatusReceiver
import com.example.widget.component.battery.BatteryUpdateManager
import com.example.widget.component.battery.bluetooth.BluetoothBatteryUpdateManager
import com.example.widget.component.battery.bluetooth.checkBluetoothBatteryComponentExist
import com.example.widget.component.battery.checkBatteryComponentExist
import com.example.widget.proto.PlacedWidgetComponent
import com.example.widget.proto.WidgetLayout
import com.example.widget.repository.WidgetLayoutRepository
import kotlin.random.Random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        val cellWidth = (widgetSize.width - ROOT_PADDING.dp * 2) / 4
        val cellHeight = (widgetSize.height - ROOT_PADDING.dp * 2) / 2

        WidgetLocalProvider(
            WidgetLocalRootPadding provides ROOT_PADDING.dp,
            WidgetLocalContentPadding provides CONTENT_PADDING.dp,
            WidgetLocalCellWidth provides cellWidth,
            WidgetLocalCellHeight provides cellHeight
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
                WidgetLocalProvider(
                    WidgetLocalSize provides DpSize(componentWidth, componentHeight),
                    WidgetLocalGridIndex provides gridIndex
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
        super.onReceive(context, intent)
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
            }
        }
    }
}
