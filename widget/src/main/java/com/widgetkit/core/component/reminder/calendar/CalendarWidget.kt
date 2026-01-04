package com.widgetkit.core.component.reminder.calendar

import android.content.Context
import android.util.Log
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
import com.widgetkit.core.component.reminder.calendar.api.HolidayResponse
import com.widgetkit.core.component.reminder.today.TodoRepository
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.database.TodoEntity
import com.widgetkit.core.util.getSystemBackgroundRadius
import com.widgetkit.core.R
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.Image
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
import java.util.Calendar
import java.util.Locale

/**
 * 캘린더 위젯
 */
class CalendarWidget : WidgetComponent() {

    override fun getName(): String = "Calendar"

    override fun getDescription(): String = "월별 캘린더 및 Todo 개수 표시"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.REMINDER

    override fun getSizeType(): SizeType = SizeType.LARGE

    override fun getWidgetTag(): String = "Calendar"

    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val isPreview = getLocal(WidgetLocalPreview) as Boolean
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val widgetSize = getLocal(WidgetLocalSize) ?: DpSize.Zero

        val calendarData = if (isPreview) {
            CalendarData.empty()
        } else {
            runBlocking {
                CalendarDataStore.loadData(context)
            }
        }
        val yearMonth = calendarData.yearMonth
        val countryCode = calendarData.countryCode

        val todosByDate = if (isPreview) {
            getPreviewTodosByDate()
        } else {
            loadTodosForMonth(context, yearMonth)
        }

        // 공휴일 데이터 로드
        val holidaysByDate = if (isPreview) {
            emptyMap<String, HolidayResponse>()
        } else {
            runBlocking {
                try {
                    // 현재 연도의 공휴일 로드
                    val currentYear = yearMonth.year
                    val holidays = mutableListOf<HolidayResponse>()
                    holidays.addAll(HolidayManager.loadHolidays(context, currentYear, countryCode))
                    
                    // 다음 연도도 로드 시도 (12월인 경우, 실패해도 무방)
                    val nextYear = if (yearMonth.month == 12) currentYear + 1 else null
                    if (nextYear != null) {
                        try {
                            holidays.addAll(HolidayManager.loadHolidays(context, nextYear, countryCode))
                        } catch (e: Exception) {
                            // 다음 연도 데이터가 없어도 정상 (아직 API에 없을 수 있음)
                            Log.d("CalendarWidget", "Next year holidays not available: $nextYear")
                        }
                    }
                    
                    HolidayManager.holidaysToMap(holidays)
                } catch (e: Exception) {
                    Log.e("CalendarWidget", "Error loading holidays", e)
                    emptyMap()
                }
            }
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
                    holidaysByDate = holidaysByDate,
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
        val buttonSize = headerSize.height * 0.8f

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
                Image(
                    modifier = WidgetModifier
                        .width(buttonSize.value * 0.6f)
                        .height(buttonSize.value * 0.6f),
                    contentProperty = {
                        Provider {
                            drawableResId = R.drawable.ic_arrow_left
                        }
                        TintColor {
                            argb = iconColor
                        }
                    }
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
                Image(
                    modifier = WidgetModifier
                        .width(buttonSize.value * 0.6f)
                        .height(buttonSize.value * 0.6f),
                    contentProperty = {
                        Provider {
                            drawableResId = R.drawable.ic_arrow_right
                        }
                        TintColor {
                            argb = iconColor
                        }
                    }
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
            weekDayNames.forEachIndexed { index, dayName ->
                // 인덱스 0: 일요일 (빨간색), 인덱스 6: 토요일 (파란색)
                val dayTextColor = when (index) {
                    0 -> Color(0xFFFF0000).toArgb()  // 일요일: 빨간색
                    6 -> Color(0xFF0000FF).toArgb()  // 토요일: 파란색
                    else -> textColor  // 나머지: 기본 색상
                }
                
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
                        fontColor = Color(dayTextColor)
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
        holidaysByDate: Map<String, HolidayResponse>,
        widgetSize: DpSize
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val cellHeight = widgetSize.height.value * 0.12f
        val dateFontSize = widgetSize.height.value * 0.05f
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
                            holiday = holidaysByDate[day.date],
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
        holiday: HolidayResponse?,
        dateFontSize: Float,
        countFontSize: Float
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        
        // 날짜 문자열에서 요일 계산
        val dayOfWeek = getDayOfWeek(day.date)
        val isSunday = dayOfWeek == Calendar.SUNDAY
        val isSaturday = dayOfWeek == Calendar.SATURDAY
        val isHoliday = holiday != null
        
        val dateColor = if (day.isCurrentMonth) {
            when {
                day.isToday -> {
                    // 오늘인 경우: 배경이 primary이므로 onPrimary 색상
                    theme.onPrimary.getColor(context).toArgb()
                }
                isSaturday -> {
                    // 토요일: 항상 파란색 (공휴일과 겹쳐도 파란색 유지)
                    Color(0xFF0000FF).toArgb()
                }
                isHoliday -> {
                    // 공휴일: 빨간색
                    Color(0xFFFF0000).toArgb()
                }
                isSunday -> {
                    // 일요일: 빨간색
                    Color(0xFFFF0000).toArgb()
                }
                else -> {
                    theme.onSurface.getColor(context).toArgb()
                }
            }
        } else {
            if (day.isToday) {
                theme.onPrimary.getColor(context).toArgb()
            } else {
                Color.Transparent.toArgb()
            }
        }

        val countColor = if (day.isToday) {
            theme.onPrimary.getColor(context).toArgb()
        } else {
            theme.primary.getColor(context).toArgb()
        }
        
        val todayBackgroundColor = if (day.isToday) {
            theme.primary.getColor(context).toArgb()
        } else {
            null
        }

        Box(
            modifier = modifier
                .padding(all = 2f)
                .let { mod ->
                    if (todayBackgroundColor != null) {
                        mod
                            .backgroundColor(todayBackgroundColor)
                            .cornerRadius(8f)
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
                // 날짜 숫자 - 현재 달에 해당하는 날짜만 표시
                if (day.isCurrentMonth) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        fontSize = dateFontSize,
                        fontWeight = if (day.isToday) FontWeight.FONT_WEIGHT_BOLD else FontWeight.FONT_WEIGHT_MEDIUM,
                        fontColor = Color(dateColor)
                    )

                    // Todo 개수 - 현재 달에 해당하는 날짜만 표시
                    if (todoCount > 0) {
                        Text(
                            text = todoCount.toString(),
                            fontSize = countFontSize,
                            fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                            fontColor = Color(countColor)
                        )
                    }
                }
            }
        }
    }

    /**
     * 날짜 문자열에서 요일 계산 (yyyy-MM-dd 형식)
     * @return Calendar.SUNDAY(1) ~ Calendar.SATURDAY(7)
     */
    private fun getDayOfWeek(dateString: String): Int {
        return try {
            val parts = dateString.split("-")
            if (parts.size == 3) {
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, parts[0].toInt())
                    set(Calendar.MONTH, parts[1].toInt() - 1)  // Calendar.MONTH는 0부터 시작
                    set(Calendar.DAY_OF_MONTH, parts[2].toInt())
                }
                calendar.get(Calendar.DAY_OF_WEEK)
            } else {
                Calendar.SUNDAY  // 기본값
            }
        } catch (e: Exception) {
            Calendar.SUNDAY  // 기본값
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

