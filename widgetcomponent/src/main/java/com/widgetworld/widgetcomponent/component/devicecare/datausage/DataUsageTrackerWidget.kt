package com.widgetworld.widgetcomponent.component.devicecare.datausage

import android.R.attr.theme
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.color.DynamicThemeColorProviders
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.room.util.TableInfo
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import com.widgetworld.widgetcomponent.R
import com.widgetworld.widgetcomponent.SizeType
import com.widgetworld.widgetcomponent.WidgetCategory
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.component.datastore.ComponentDataStore
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.component.viewid.ViewIdType
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius
import com.widgetworld.core.WidgetScope
import com.widgetworld.core.frontend.Button
import com.widgetworld.core.frontend.Image
import com.widgetworld.core.frontend.Progress
import com.widgetworld.core.frontend.Spacer
import com.widgetworld.core.frontend.Text
import com.widgetworld.core.frontend.layout.Box
import com.widgetworld.core.frontend.layout.Column
import com.widgetworld.core.frontend.layout.Row
import com.widgetworld.core.proto.AlignmentType
import com.widgetworld.core.proto.FontWeight
import com.widgetworld.core.proto.HorizontalAlignment
import com.widgetworld.core.proto.ProgressType
import com.widgetworld.core.proto.VerticalAlignment
import com.widgetworld.core.proto.modifier.WidgetModifier
import com.widgetworld.core.proto.modifier.backgroundColor
import com.widgetworld.core.proto.modifier.cornerRadius
import com.widgetworld.core.proto.modifier.expandWidth
import com.widgetworld.core.proto.modifier.fillMaxHeight
import com.widgetworld.core.proto.modifier.fillMaxWidth
import com.widgetworld.core.proto.modifier.height
import com.widgetworld.core.proto.modifier.padding
import com.widgetworld.core.proto.modifier.partiallyUpdate
import com.widgetworld.core.proto.modifier.viewId
import com.widgetworld.core.proto.modifier.width
import com.widgetworld.core.proto.modifier.wrapContentHeight
import com.widgetworld.core.proto.modifier.wrapContentWidth
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalGlanceId
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalState
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalTheme
import com.widgetworld.widgetcomponent.component.devicecare.datausage.DataUsageLimitAction
import com.widgetworld.widgetcomponent.theme.FontType
import com.widgetworld.widgetcomponent.theme.value
import com.widgetworld.core.widget.action.WidgetActionParameters
import com.widgetworld.core.widget.action.WidgetActionCallbackBroadcastReceiver
import com.widgetworld.core.widget.action.RunWidgetCallbackAction
import com.widgetworld.core.widget.action.widgetActionParametersOf
import com.widgetworld.core.proto.modifier.clickAction
import com.widgetworld.widgetcomponent.action.CustomWidgetActionCallbackBroadcastReceiver

class DataUsageTrackerWidget : WidgetComponent() {

    override fun getName(): String = "Data Usage"

    override fun getDescription(): String = "Data Usage Tracker"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DEVICE_STATUS

    override fun getSizeType(): SizeType = SizeType.SMALL

    override fun getWidgetTag(): String = "DataUsage"

    @SuppressLint("RestrictedApi")
    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val backgroundColor = theme.surface.getColor(context).toArgb()

        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(backgroundColor)
                .padding(horizontal = 6f, vertical = 2f),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Column(
                modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                WifiSection(modifier = WidgetModifier.fillMaxWidth().wrapContentHeight())
                MobileDataSection(modifier = WidgetModifier.fillMaxWidth().wrapContentHeight())
            }
            // Date range text in top right
            DateRangeText(modifier = WidgetModifier.fillMaxWidth().fillMaxHeight())
        }
    }

    private fun WidgetScope.DataUsageTemplate(
        iconSize: Dp,
        iconResId: Int,
        titleName: String,
        dataUsage: String,
        dataViewId: Int,
        progress: Float,
        progressViewId: Int,
        action: RunWidgetCallbackAction,
        modifier: WidgetModifier = WidgetModifier
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val iconColor = theme.primary.getColor(context).toArgb()
        val textColor = theme.onSurfaceVariant.getColor(context).toArgb()

        Row(
            modifier = modifier,
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }) {
            Image(
                modifier = WidgetModifier.width(iconSize.value).height(iconSize.value).padding(2f),
                contentProperty = {
                    Provider {
                        drawableResId = iconResId
                    }
                    TintColor {
                        argb = iconColor
                    }
                }
            )
            Spacer(modifier = WidgetModifier.wrapContentHeight().width(4f))
            Column(
                modifier = WidgetModifier.expandWidth().wrapContentHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                Text(
                    modifier = WidgetModifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    text = titleName,
                    fontSize = FontType.TitleSmall.value,
                    fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
                    fontColor = Color(textColor)
                )

                // Progress Bar with Percentage
                Box(
                    modifier = WidgetModifier
                        .fillMaxWidth()
                        .height(8f)
                        .padding(top = 2f, bottom = 2f),
                    contentProperty = {
                        contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER_START
                    }
                ) {
                    val progressColor = theme.primary.getColor(context).toArgb()
                    val progressBgColor = theme.surfaceVariant.getColor(context).toArgb()

                    Progress(
                        modifier = WidgetModifier
                            .width(100f)
                            .height(3f)
                            .viewId(progressViewId)
                            .partiallyUpdate(true)
                            .cornerRadius(context.getSystemBackgroundRadius().value),
                        contentProperty = {
                            progressType = ProgressType.PROGRESS_TYPE_LINEAR
                            progressValue = progress
                            maxValue = 100f
                            ProgressColor {
                                Color {
                                    argb = progressColor
                                }
                            }
                            BackgroundColor {
                                Color {
                                    argb = progressBgColor
                                }
                            }
                        }
                    )
                }
                Spacer(modifier = WidgetModifier.fillMaxWidth().height(1f))
                // Usage Text with Limit Button
                Row(
                    modifier = WidgetModifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentProperty = {
                        horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                        verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                    }
                ) {
                    Text(
                        modifier = WidgetModifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .viewId(dataViewId)
                            .partiallyUpdate(true),
                        text = String.format(
                            dataUsage
                        ),
                        fontSize = FontType.Caption.value,
                        fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                        fontColor = Color(theme.onSurface.getColor(context).toArgb())
                    )
                }
            }
            Image(
                modifier = WidgetModifier.padding(end = 4f).width(18f).height(18f).clickAction(
                    context, action
                ),
                contentProperty = {
                    Provider {
                        drawableResId = R.drawable.ic_setting
                    }
                    TintColor {
                        argb = iconColor
                    }
                }
            )
        }
    }

    private fun WidgetScope.WifiSection(modifier: WidgetModifier = WidgetModifier) {
        val context = getLocal(WidgetLocalContext) as Context
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val dataUsageData = getDataUsageValue()
        val iconSize = localSize.height.value * 0.24f
        val dataUsage = String.format(
            "%.1fGB / %.1fGB",
            dataUsageData.wifiUsageGb,
            dataUsageData.wifiLimitGb
        )
        val dataViewId = generateViewId(DataUsageViewIdType.WifiUsageText, gridIndex)
        val progressViewId = generateViewId(DataUsageViewIdType.WifiProgress, gridIndex)
        val glanceId = getLocal(WidgetLocalGlanceId)
        val appWidgetId = glanceId?.let {
            androidx.glance.appwidget.GlanceAppWidgetManager(context).getAppWidgetId(it)
        } ?: -1
        val action = RunWidgetCallbackAction(
            CustomWidgetActionCallbackBroadcastReceiver::class.java,
            DataUsageLimitAction::class.java,
            widgetActionParametersOf(
                WidgetActionParameters.Key<String>("actionClass") to DataUsageLimitAction::class.java.canonicalName,
                WidgetActionParameters.Key<Int>("widgetId") to appWidgetId,
                WidgetActionParameters.Key<String>(DataUsageLimitAction.PARAM_LIMIT_TYPE) to "wifi",
                WidgetActionParameters.Key<Int>(DataUsageLimitAction.PARAM_WIDGET_ID) to appWidgetId
            )
        )

        DataUsageTemplate(
            modifier = modifier,
            iconSize = iconSize.dp,
            iconResId = R.drawable.ic_wifi,
            titleName = "Wi-Fi",
            dataUsage = dataUsage,
            dataViewId = dataViewId,
            progress = dataUsageData.wifiUsagePercent,
            progressViewId = progressViewId,
            action = action
        )
    }

    private fun WidgetScope.MobileDataSection(modifier: WidgetModifier = WidgetModifier) {

        val context = getLocal(WidgetLocalContext) as Context
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val dataUsageData = getDataUsageValue()
        val iconSize = localSize.height.value * 0.24f
        val dataUsage = String.format(
            "%.1fGB / %.1fGB",
            dataUsageData.wifiUsageGb,
            dataUsageData.wifiLimitGb
        )
        val dataViewId = generateViewId(DataUsageViewIdType.MobileUsageText, gridIndex)
        val progressViewId = generateViewId(DataUsageViewIdType.MobileProgress, gridIndex)
        val glanceId = getLocal(WidgetLocalGlanceId)
        val appWidgetId = glanceId?.let {
            androidx.glance.appwidget.GlanceAppWidgetManager(context).getAppWidgetId(it)
        } ?: -1
        val action = RunWidgetCallbackAction(
            CustomWidgetActionCallbackBroadcastReceiver::class.java,
            DataUsageLimitAction::class.java,
            widgetActionParametersOf(
                WidgetActionParameters.Key<String>("actionClass") to DataUsageLimitAction::class.java.canonicalName,
                WidgetActionParameters.Key<Int>("widgetId") to appWidgetId,
                WidgetActionParameters.Key<String>(DataUsageLimitAction.PARAM_LIMIT_TYPE) to "mobile",
                WidgetActionParameters.Key<Int>(DataUsageLimitAction.PARAM_WIDGET_ID) to appWidgetId
            )
        )

        DataUsageTemplate(
            modifier = modifier,
            iconSize = iconSize.dp,
            iconResId = R.drawable.ic_mobile_data,
            titleName = "Mobile Data",
            dataUsage = dataUsage,
            dataViewId = dataViewId,
            progress = dataUsageData.mobileUsagePercent,
            progressViewId = progressViewId,
            action = action
        )
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = DataUsageUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = DataUsageDataStore

    private fun WidgetScope.DateRangeText(modifier: WidgetModifier = WidgetModifier) {
        val context = getLocal(WidgetLocalContext) as Context
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val dateRange = getCurrentMonthDateRange()

        Box(
            modifier = modifier.padding(end = 2f, top = 2f),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_END
            }
        ) {
            Text(
                modifier = WidgetModifier
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .viewId(generateViewId(DataUsageViewIdType.DateRangeText, gridIndex))
                    .partiallyUpdate(true),
                text = dateRange,
                fontSize = FontType.Caption.value,
                fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                fontColor = Color(theme.onSurfaceVariant.getColor(context).toArgb())
            )
        }
    }

    private fun getCurrentMonthDateRange(): String {
        val dateFormat = SimpleDateFormat("M월 d일", Locale.KOREAN)
        val startDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endDate = Calendar.getInstance()

        val startDateStr = dateFormat.format(startDate.time)
        val endDateStr = dateFormat.format(endDate.time)

        return "$startDateStr - $endDateStr"
    }

    private fun WidgetScope.getDataUsageValue(): DataUsageData {
        val currentState = getLocal(WidgetLocalState)
        val isPreview = getLocal(WidgetLocalPreview) ?: false

        if (isPreview) {
            // Preview 모드: 샘플 데이터
            val defaultLimit = DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
            return DataUsageData.create(
                wifiUsageBytes = 3L * 1024 * 1024 * 1024 + 512L * 1024 * 1024, // 3.5GB
                wifiLimitBytes = defaultLimit, // 5GB
                mobileUsageBytes = 2L * 1024 * 1024 * 1024, // 2GB
                mobileLimitBytes = defaultLimit // 5GB
            )
        }

        return currentState?.let { state ->
            val wifiLimitBytes = state[DataUsagePreferenceKey.WifiLimitBytes]
                ?: state[DataUsagePreferenceKey.DataLimitBytes]
                ?: (DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024)
            val wifiUsageBytes = state[DataUsagePreferenceKey.WifiUsageBytes] ?: 0L

            val mobileLimitBytes = state[DataUsagePreferenceKey.MobileLimitBytes]
                ?: state[DataUsagePreferenceKey.DataLimitBytes]
                ?: (DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024)
            val mobileUsageBytes = state[DataUsagePreferenceKey.MobileUsageBytes] ?: 0L

            DataUsageData.create(
                wifiUsageBytes = wifiUsageBytes,
                wifiLimitBytes = wifiLimitBytes,
                mobileUsageBytes = mobileUsageBytes,
                mobileLimitBytes = mobileLimitBytes
            )
        } ?: DataUsageDataStore.getDefaultData()
    }

    override fun getViewIdTypes(): List<ViewIdType> = DataUsageViewIdType.all()

    fun getWifiProgressId(gridIndex: Int): Int {
        return generateViewId(DataUsageViewIdType.WifiProgress, gridIndex)
    }

    fun getWifiPercentId(gridIndex: Int): Int {
        return generateViewId(DataUsageViewIdType.WifiPercent, gridIndex)
    }

    fun getWifiUsageTextId(gridIndex: Int): Int {
        return generateViewId(DataUsageViewIdType.WifiUsageText, gridIndex)
    }

    fun getMobileProgressId(gridIndex: Int): Int {
        return generateViewId(DataUsageViewIdType.MobileProgress, gridIndex)
    }

    fun getMobilePercentId(gridIndex: Int): Int {
        return generateViewId(DataUsageViewIdType.MobilePercent, gridIndex)
    }

    fun getMobileUsageTextId(gridIndex: Int): Int {
        return generateViewId(DataUsageViewIdType.MobileUsageText, gridIndex)
    }

    fun getDateRangeTextId(gridIndex: Int): Int {
        return generateViewId(DataUsageViewIdType.DateRangeText, gridIndex)
    }
}

