package com.example.widget.component.devicecare.storage

import android.content.ComponentName
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Image
import com.example.dsl.component.Progress
import com.example.dsl.component.Row
import com.example.dsl.component.Text
import com.example.dsl.localprovider.WidgetLocalContext
import com.example.dsl.localprovider.WidgetLocalPreview
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.localprovider.WidgetLocalState
import com.example.dsl.modifier.WidgetModifier
import com.example.dsl.modifier.backgroundColor
import com.example.dsl.modifier.clickAction
import com.example.dsl.modifier.cornerRadius
import com.example.dsl.modifier.fillMaxHeight
import com.example.dsl.modifier.fillMaxWidth
import com.example.dsl.modifier.height
import com.example.dsl.modifier.padding
import com.example.dsl.modifier.width
import com.example.dsl.modifier.wrapContentHeight
import com.example.dsl.modifier.wrapContentWidth
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.ProgressType
import com.example.dsl.proto.VerticalAlignment
import com.example.widget.R
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.component.datastore.ComponentDataStore
import com.example.widget.component.lifecycle.ComponentLifecycle
import com.example.widget.component.reminder.today.ui.TodayTodoActivity
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.util.getSystemBackgroundRadius

class StorageWidget : WidgetComponent() {

    override fun getName(): String = "Storage"

    override fun getDescription(): String = "Storage"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_INFO

    override fun getSizeType(): SizeType = SizeType.TINY

    override fun getWidgetTag(): String = "Storage"

    override fun WidgetScope.Content() {
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val context = getLocal(WidgetLocalContext) as Context
        val isPreview = getLocal(WidgetLocalPreview) as Boolean
        var backgroundModifier = WidgetModifier
            .fillMaxWidth().fillMaxHeight().backgroundColor(Color.White.toArgb())
        if (!isPreview) {
            backgroundModifier = backgroundModifier.clickAction(
                ComponentName(
                    context,
                    TodayTodoActivity::class.java
                )
            )
        }
        Box(
            modifier = backgroundModifier,
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }) {
            Column(
                modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }) {
                StorageIcon()
                StorageTitle()
                StorageUsageProgress(
                    modifier = WidgetModifier.fillMaxWidth().height(
                        localSize.height.value * 0.2f
                    )
                )
            }
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = StorageUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = StorageDataStore

    override fun getLifecycle(): ComponentLifecycle? = null

    override fun requiresAutoLifecycle(): Boolean = false

    private fun WidgetScope.StorageIcon() {
        val size = getLocal(WidgetLocalSize) as DpSize
        val height = size.height.value
        Box(modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(), contentProperty = {
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }
        ) {
            Image(
                modifier = WidgetModifier
                    .width(height * 0.34f)
                    .height(height * 0.34f),
                contentProperty = {
                    Provider {
                        drawableResId = R.drawable.ic_storage
                    }
                }
            )
        }
    }

    private fun WidgetScope.StorageTitle() {
        Text {
            TextContent {
                text = "Storage"
            }
            fontSize = 12f
            fontWeight = FontWeight.FONT_WEIGHT_MEDIUM
        }
    }

    private fun WidgetScope.StorageUsageProgress(
        modifier: WidgetModifier = WidgetModifier
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val currentRamUsage = getStorageValue()

        Box(
            modifier = modifier.padding(horizontal = 8f, vertical = 2f),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER_START
            }
        ) {
            Progress(
                modifier = WidgetModifier
                    .fillMaxWidth().fillMaxHeight()
                    .cornerRadius(context.getSystemBackgroundRadius().value),
                contentProperty = {
                    progressType = ProgressType.PROGRESS_TYPE_LINEAR
                    progressValue = currentRamUsage
                    maxValue = 100f
                    ProgressColor {
                        Color {
                            argb = Color(0x80000000).toArgb()
                        }
                    }
                    BackgroundColor {
                        Color {
                            argb = Color(0xFFFFFFFF).toArgb()
                        }
                    }
                }
            )
            Row(
                modifier = WidgetModifier.fillMaxWidth().wrapContentHeight().padding(end = 4f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_END
                }) {
                Text(
                    contentProperty = {
                        TextContent {
                            text = "${String.format("%.1f", currentRamUsage)}%"
                        }
                        fontSize = 8f
                        fontWeight = FontWeight.FONT_WEIGHT_BOLD
                        FontColor {
                            Color {
                                argb = Color(0xFF000000).toArgb()
                            }
                        }
                    }
                )
            }
        }
    }

    private fun WidgetScope.getStorageValue(): Float {
        val currentState = getLocal(WidgetLocalState)
        val isPreview = getLocal(WidgetLocalPreview) ?: false
        if (isPreview) return 66.7f
        return currentState?.let { state ->
            val currentValue = state[StoragePreferenceKey.UsagePercent]
            currentValue ?: 0f
        } ?: 0f
    }

    fun getStorageTextId(gridIndex: Int): Int {
        return generateViewId(StorageViewIdType.Text, gridIndex)
    }

    fun getStorageProgressId(gridIndex: Int): Int {
        return generateViewId(StorageViewIdType.Progress, gridIndex)
    }

}