package com.widgetkit.widgetcomponent.component.devicecare.storage

import android.content.ComponentName
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.widgetkit.widgetcomponent.R
import com.widgetkit.widgetcomponent.SizeType
import com.widgetkit.widgetcomponent.WidgetCategory
import com.widgetkit.widgetcomponent.component.WidgetComponent
import com.widgetkit.widgetcomponent.component.datastore.ComponentDataStore
import com.widgetkit.widgetcomponent.component.reminder.today.ui.TodoActivity
import com.widgetkit.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetkit.widgetcomponent.util.getSystemBackgroundRadius
import com.widgetkit.core.WidgetScope
import com.widgetkit.core.frontend.Image
import com.widgetkit.core.frontend.Progress
import com.widgetkit.core.frontend.Text
import com.widgetkit.core.frontend.layout.Box
import com.widgetkit.core.frontend.layout.Column
import com.widgetkit.core.frontend.layout.Row
import com.widgetkit.core.proto.AlignmentType
import com.widgetkit.core.proto.FontWeight
import com.widgetkit.core.proto.HorizontalAlignment
import com.widgetkit.core.proto.ProgressType
import com.widgetkit.core.proto.VerticalAlignment
import com.widgetkit.core.proto.modifier.WidgetModifier
import com.widgetkit.core.proto.modifier.backgroundColor
import com.widgetkit.core.proto.modifier.clickAction
import com.widgetkit.core.proto.modifier.cornerRadius
import com.widgetkit.core.proto.modifier.fillMaxHeight
import com.widgetkit.core.proto.modifier.fillMaxWidth
import com.widgetkit.core.proto.modifier.height
import com.widgetkit.core.proto.modifier.padding
import com.widgetkit.core.proto.modifier.width
import com.widgetkit.core.proto.modifier.wrapContentHeight
import com.widgetkit.core.proto.modifier.wrapContentWidth
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalState
import com.widgetkit.core.widget.widgetlocalprovider.WidgetLocalTheme

//class StorageWidget : WidgetComponent() {
//
//    override fun getName(): String = "Storage"
//
//    override fun getDescription(): String = "Storage"
//
//    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_INFO
//
//    override fun getSizeType(): SizeType = SizeType.TINY
//
//    override fun getWidgetTag(): String = "Storage"
//
//    override fun WidgetScope.Content() {
//        val theme = getLocal(WidgetLocalTheme)
//        val backgroundColor = (theme?.surface as? Int) ?: Color.White.toArgb()
//        val localSize = getLocal(WidgetLocalSize) as DpSize
//        val context = getLocal(WidgetLocalContext) as Context
//        val isPreview = getLocal(WidgetLocalPreview) as Boolean
//        var backgroundModifier = WidgetModifier
//            .fillMaxWidth().fillMaxHeight().backgroundColor(backgroundColor)
//        if (!isPreview) {
//            backgroundModifier = backgroundModifier.clickAction(
//                ComponentName(
//                    context,
//                    TodoActivity::class.java
//                )
//            )
//        }
//        Box(
//            modifier = backgroundModifier,
//            contentProperty = {
//                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
//            }
//        ) {
//            Column(
//                modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
//                contentProperty = {
//                    horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
//                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
//                }
//            ) {
//                StorageIcon()
//                StorageTitle()
//                StorageUsageProgress(
//                    modifier = WidgetModifier.fillMaxWidth().height(
//                        localSize.height.value * 0.2f
//                    )
//                )
//            }
//        }
//    }
//
//    override fun getUpdateManager(): ComponentUpdateManager<*> = StorageUpdateManager
//
//    override fun getDataStore(): ComponentDataStore<*> = StorageDataStore
//
//    private fun WidgetScope.StorageIcon() {
//        val size = getLocal(WidgetLocalSize) as DpSize
//        val height = size.height.value
//        Box(
//            modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(),
//            contentProperty = {
//                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
//            }
//        ) {
//            Image(
//                modifier = WidgetModifier
//                    .width(height * 0.34f)
//                    .height(height * 0.34f),
//                contentProperty = {
//                    Provider {
//                        drawableResId = R.drawable.ic_storage
//                    }
//                }
//            )
//        }
//    }
//
//    private fun WidgetScope.StorageTitle() {
//        val theme = getLocal(WidgetLocalTheme)
//        val textColor = (theme?.onSurfaceVariant as? Int) ?: Color.Black.toArgb()
//
//        Text(
//            text = "Storage",
//            fontSize = 12f,
//            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
//            fontColor = Color(textColor)
//        )
//    }
//
//    private fun WidgetScope.StorageUsageProgress(modifier: WidgetModifier = WidgetModifier) {
//        val context = getLocal(WidgetLocalContext) as Context
//        val currentRamUsage = getStorageValue()
//
//        Box(
//            modifier = modifier.padding(horizontal = 8f, vertical = 2f),
//            contentProperty = {
//                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER_START
//            }
//        ) {
//            val theme = getLocal(WidgetLocalTheme)
//            val progressColor = (theme?.primary as? Int) ?: Color(0x80000000).toArgb()
//            val progressBgColor = (theme?.surfaceVariant as? Int) ?: Color(0xFFFFFFFF).toArgb()
//
//            Progress(
//                modifier = WidgetModifier
//                    .fillMaxWidth().fillMaxHeight()
//                    .cornerRadius(context.getSystemBackgroundRadius().value),
//                contentProperty = {
//                    progressType = ProgressType.PROGRESS_TYPE_LINEAR
//                    progressValue = currentRamUsage
//                    maxValue = 100f
//                    ProgressColor {
//                        Color {
//                            argb = progressColor
//                        }
//                    }
//                    BackgroundColor {
//                        Color {
//                            argb = progressBgColor
//                        }
//                    }
//                }
//            )
//            Row(
//                modifier = WidgetModifier.fillMaxWidth().wrapContentHeight().padding(end = 4f),
//                contentProperty = {
//                    horizontalAlignment = HorizontalAlignment.H_ALIGN_END
//                }
//            ) {
//                val theme = getLocal(WidgetLocalTheme)
//                val textColor = (theme?.onSurface as? Int) ?: Color(0xFF000000).toArgb()
//
//                Text(
//                    text = "${String.format("%.1f", currentRamUsage)}%",
//                    fontSize = 8f,
//                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
//                    fontColor = Color(textColor)
//                )
//            }
//        }
//    }
//
//    private fun WidgetScope.getStorageValue(): Float {
//        val currentState = getLocal(WidgetLocalState)
//        val isPreview = getLocal(WidgetLocalPreview) ?: false
//        if (isPreview) return 66.7f
//        return currentState?.let { state ->
//            val currentValue = state[StoragePreferenceKey.UsagePercent]
//            currentValue ?: 0f
//        } ?: 0f
//    }
//
//    fun getStorageTextId(gridIndex: Int): Int {
//        return generateViewId(StorageViewIdType.Text, gridIndex)
//    }
//
//    fun getStorageProgressId(gridIndex: Int): Int {
//        return generateViewId(StorageViewIdType.Progress, gridIndex)
//    }
//}
