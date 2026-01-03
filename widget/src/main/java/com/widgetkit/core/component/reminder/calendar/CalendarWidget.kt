package com.widgetkit.core.component.reminder.calendar

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.color.DynamicThemeColorProviders
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.core.component.datastore.ComponentDataStore
import com.widgetkit.core.component.reminder.calendar.CalendarDateUtils.CalendarDay
import com.widgetkit.core.component.reminder.calendar.CalendarDateUtils.YearMonth
import com.widgetkit.core.component.reminder.today.TodoRepository
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.database.TodoEntity
import com.widgetkit.core.util.getSystemBackgroundRadius
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.Text
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.frontend.layout.Column
import com.widgetkit.dsl.frontend.layout.Row
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.HorizontalAlignment
import com.widgetkit.dsl.proto.VerticalAlignment
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.clickAction
import com.widgetkit.dsl.proto.modifier.cornerRadius
import com.widgetkit.dsl.proto.modifier.expandWidth
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.padding
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.proto.modifier.wrapContentHeight
import com.widgetkit.dsl.proto.modifier.wrapContentWidth
import com.widgetkit.dsl.widget.action.RunWidgetCallbackAction
import com.widgetkit.dsl.widget.action.WidgetActionParameters
import com.widgetkit.dsl.widget.action.widgetActionParametersOf
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGlanceId
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalTheme
import com.widgetkit.core.action.CustomWidgetActionCallbackBroadcastReceiver
import com.widgetkit.dsl.proto.modifier.expandHeight
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

/**
 * 캘린더 위젯
 */
class CalendarWidget : WidgetComponent() {

    override fun getName(): String = "Calendar"

    override fun getDescription(): String = "월별 캘린더 및 Todo 개수 표시"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.REMINDER

    override fun getSizeType(): SizeType = SizeType.MEDIUM_PLUS

    override fun getWidgetTag(): String = "Calendar"

    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val isPreview = getLocal(WidgetLocalPreview) as Boolean
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val widgetSize = getLocal(WidgetLocalSize) ?: DpSize.Zero

        val yearMonth = if (isPreview) {
            CalendarDateUtils.getCurrentYearMonth()
        } else {
            runBlocking {
                CalendarDataStore.loadData(context).yearMonth
            }
        }

        val todosByDate = if (isPreview) {
            getPreviewTodosByDate()
        } else {
            loadTodosForMonth(context, yearMonth)
        }

        val calendarGrid = CalendarDateUtils.generateCalendarGrid(yearMonth)

        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(theme.surface.getColor(context).toArgb())
                .cornerRadius(context.getSystemBackgroundRadius().value),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_START
            }
        ) {
            Column(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 8f, vertical = 4f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                }
            ) {
                // 헤더: 월/년 + 좌우 화살표
                val headerHeight = widgetSize.height.value * 0.2f
                WidgetLocalProvider(
                    WidgetLocalSize provides DpSize(
                        widgetSize.width,
                        headerHeight.dp
                    )
                ) {
                    Header(
                        modifier = WidgetModifier.fillMaxWidth().height(headerHeight),
                        yearMonth = yearMonth,
                        isPreview = isPreview
                    )
                }

                val dayHeaderHeight = widgetSize.height.value * 0.06f
                WidgetLocalProvider(
                    WidgetLocalSize provides DpSize(
                        widgetSize.width, dayHeaderHeight.dp
                    )
                ) {
                    WeekDayHeader(
                        modifier = WidgetModifier.fillMaxWidth().height(dayHeaderHeight)
                    )
                }

                // 캘린더 그리드
                CalendarGrid(
                    modifier = WidgetModifier.fillMaxWidth().expandHeight(),
                    grid = calendarGrid,
                    todosByDate = todosByDate,
                    widgetSize = widgetSize
                )
            }
        }
    }

    /**
     * 헤더: 월/년 표시 + 좌우 화살표 버튼
     */
    private fun WidgetScope.Header(
        modifier: WidgetModifier,
        yearMonth: YearMonth,
        isPreview: Boolean
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val widgetId = getLocal(WidgetLocalGlanceId) as AppWidgetId?
        val headerSize = getLocal(WidgetLocalSize) as DpSize

        val textColor = theme.onSurface.getColor(context).toArgb()
        val iconColor = theme.primary.getColor(context).toArgb()
        val fontSize = headerSize.height.value * 0.4f
        val buttonSize = headerSize.height * 0.6f

        Row(
            modifier = modifier,
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            // 이전 달 버튼
            Box(
                modifier = WidgetModifier
                    .width(buttonSize.value)
                    .height(buttonSize.value)
                    .clickAction(
                        context,
                        RunWidgetCallbackAction(
                            CustomWidgetActionCallbackBroadcastReceiver::class.java,
                            CalendarAction::class.java,
                            widgetActionParametersOf(
                                WidgetActionParameters.Key<String>("actionClass") to CalendarAction::class.java.canonicalName,
                                WidgetActionParameters.Key<Int>("widgetId") to (widgetId?.appWidgetId
                                    ?: 0),
                                WidgetActionParameters.Key<String>(CalendarAction.PARAM_ACTION) to "prev"
                            )
                        )
                    ),
                contentProperty = {
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                }
            ) {
                Text(
                    text = "‹",
                    fontSize = fontSize * 1.5f,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    fontColor = Color(iconColor)
                )
            }

            // 월/년 표시
            Box(
                modifier = WidgetModifier.expandWidth().wrapContentHeight(),
                contentProperty = {
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                }
            ) {
                Text(
                    text = yearMonth.getDisplayName(Locale.ENGLISH),
                    fontSize = fontSize,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    fontColor = Color(textColor)
                )
            }

            // 다음 달 버튼
            Box(
                modifier = WidgetModifier
                    .width(buttonSize.value)
                    .height(buttonSize.value)
                    .clickAction(
                        context,
                        RunWidgetCallbackAction(
                            CustomWidgetActionCallbackBroadcastReceiver::class.java,
                            CalendarAction::class.java,
                            widgetActionParametersOf(
                                WidgetActionParameters.Key<String>("actionClass") to CalendarAction::class.java.canonicalName,
                                WidgetActionParameters.Key<Int>("widgetId") to (widgetId?.appWidgetId
                                    ?: 0),
                                WidgetActionParameters.Key<String>(CalendarAction.PARAM_ACTION) to "next"
                            )
                        )
                    ),
                contentProperty = {
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                }
            ) {
                Text(
                    text = "›",
                    fontSize = fontSize * 1.5f,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    fontColor = Color(iconColor)
                )
            }
        }
    }

    /**
     * 요일 헤더
     */
    private fun WidgetScope.WeekDayHeader(
        modifier: WidgetModifier
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val textColor = theme.onSurfaceVariant.getColor(context).toArgb()
        val widgetSize = getLocal(WidgetLocalSize) as DpSize
        val fontSize = widgetSize.height.value * 0.8f
        val weekDayNames = CalendarDateUtils.getWeekDayNames(Locale.KOREAN)

        Row(
            modifier = modifier,
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            weekDayNames.forEach { dayName ->
                Box(
                    modifier = WidgetModifier
                        .expandWidth()
                        .fillMaxHeight(),
                    contentProperty = {
                        contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                    }
                ) {
                    Text(
                        text = dayName,
                        fontSize = fontSize,
                        fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
                        fontColor = Color(textColor)
                    )
                }
            }
        }
    }

    /**
     * 캘린더 그리드
     */
    private fun WidgetScope.CalendarGrid(
        modifier: WidgetModifier,
        grid: List<List<CalendarDay>>,
        todosByDate: Map<String, Int>,
        widgetSize: DpSize
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val cellHeight = widgetSize.height.value * 0.12f
        val dateFontSize = widgetSize.height.value * 0.04f
        val countFontSize = widgetSize.height.value * 0.03f

        Column(
            modifier = modifier,
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
            }
        ) {
            grid.forEach { week ->
                Row(
                    modifier = WidgetModifier.fillMaxWidth(),
                    contentProperty = {
                        horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    }
                ) {
                    week.forEach { day ->
                        CalendarDayCell(
                            modifier = WidgetModifier
                                .expandWidth()
                                .height(cellHeight),
                            day = day,
                            todoCount = todosByDate[day.date] ?: 0,
                            dateFontSize = dateFontSize,
                            countFontSize = countFontSize
                        )
                    }
                }
            }
        }
    }

    /**
     * 날짜 셀
     */
    private fun WidgetScope.CalendarDayCell(
        modifier: WidgetModifier,
        day: CalendarDay,
        todoCount: Int,
        dateFontSize: Float,
        countFontSize: Float
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders

        val dateColor = if (day.isCurrentMonth) {
            if (day.isToday) {
                theme.primary.getColor(context).toArgb()
            } else {
                theme.onSurface.getColor(context).toArgb()
            }
        } else {
            theme.onSurfaceVariant.getColor(context).toArgb()
        }

        val countColor = theme.primary.getColor(context).toArgb()
        val todayBackgroundColor = if (day.isToday) {
            theme.primaryContainer.getColor(context).toArgb()
        } else {
            null
        }

        Box(
            modifier = modifier
                .padding(all = 2f)
                .let { mod ->
                    if (todayBackgroundColor != null) {
                        mod.backgroundColor(todayBackgroundColor)
                    } else {
                        mod
                    }
                },
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Column(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 4f, vertical = 2f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                // 날짜 숫자
                Text(
                    text = day.dayOfMonth.toString(),
                    fontSize = dateFontSize,
                    fontWeight = if (day.isToday) FontWeight.FONT_WEIGHT_BOLD else FontWeight.FONT_WEIGHT_NORMAL,
                    fontColor = Color(dateColor)
                )

                // Todo 개수
                if (todoCount > 0) {
                    Text(
                        text = todoCount.toString(),
                        fontSize = countFontSize,
                        fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
                        fontColor = Color(countColor)
                    )
                }
            }
        }
    }

    /**
     * 한 달의 Todo를 날짜별로 그룹화하여 로드
     */
    private fun loadTodosForMonth(context: Context, yearMonth: YearMonth): Map<String, Int> {
        return try {
            runBlocking {
                val repository = TodoRepository(context)
                val startDate = yearMonth.getFirstDayString()
                val endDate = yearMonth.getLastDayString()
                val todos = repository.getTodosByDateRange(startDate, endDate).first()

                // 날짜별로 그룹화하여 개수 계산
                todos.groupBy { it.date }
                    .mapValues { it.value.size }
            }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    /**
     * Preview용 샘플 데이터
     */
    private fun getPreviewTodosByDate(): Map<String, Int> {
        val today = CalendarDateUtils.getCurrentYearMonth()
        return mapOf(
            "${today.year}-${today.month}-15" to 3,
            "${today.year}-${today.month}-20" to 1,
            "${today.year}-${today.month}-25" to 2
        )
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = CalendarUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = CalendarDataStore
}

