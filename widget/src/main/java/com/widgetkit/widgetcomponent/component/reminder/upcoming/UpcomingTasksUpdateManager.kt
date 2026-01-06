package com.widgetkit.widgetcomponent.component.reminder.upcoming

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetkit.widgetcomponent.component.reminder.today.TodoDateUtils
import com.widgetkit.widgetcomponent.component.reminder.today.TodoRepository
import com.widgetkit.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetkit.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetkit.widgetcomponent.provider.LargeAppWidget
import com.widgetkit.widgetcomponent.provider.common.DslAppWidget
import java.util.Calendar

/**
 * Upcoming Tasks 위젯 업데이트 관리자
 */
object UpcomingTasksUpdateManager : ComponentUpdateManager<UpcomingTasksData> {

    private const val TAG = "UpcomingTasksUpdateManager"

    override val widget: UpcomingTasksWidget
        get() = UpcomingTasksWidget()

    override suspend fun syncState(context: Context, data: UpcomingTasksData) {
        // 각 위젯의 개별 필터 설정을 로드하여 각 위젯을 업데이트
        val placedComponents =
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
        placedComponents.forEach { (widgetId, _) ->
            try {
                // 각 위젯의 개별 필터 타입 로드
                val filterType = UpcomingTasksDataStore.loadFilterType(context, widgetId)
                val updatedData = loadUpcomingTodos(context, filterType)
                Log.d(
                    TAG,
                    "Sync widget state for widget $widgetId, filter $filterType: ${updatedData.todos.size} tasks"
                )
                // 각 위젯별로 데이터 저장 및 업데이트
                UpcomingTasksDataStore.saveData(context, widgetId, updatedData)
                updateWidget(context, widgetId)
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing widget $widgetId", e)
            }
        }
    }

    override suspend fun updateByPartially(context: Context, widgetId: Int?, data: UpcomingTasksData) {
        // 부분 업데이트는 지원하지 않음
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: UpcomingTasksData) {
        if (widgetId != null) {
            // 특정 위젯만 업데이트
            UpcomingTasksDataStore.saveData(context, widgetId, data)
            updateWidget(context, widgetId)
        } else {
            // 모든 위젯 업데이트
            val placedComponents =
                ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            placedComponents.forEach { (id, _) ->
                // 각 위젯별로 필터 타입 로드하여 데이터 업데이트
                val filterType = UpcomingTasksDataStore.loadFilterType(context, id)
                val updatedData = loadUpcomingTodos(context, filterType)
                UpcomingTasksDataStore.saveData(context, id, updatedData)
                updateWidget(context, id)
            }
        }
    }

    /**
     * 특정 widget id만 업데이트
     */
    suspend fun updateWidgetById(context: Context, widgetId: Int, data: UpcomingTasksData) {
        // widget id별 데이터 저장
        UpcomingTasksDataStore.saveData(context, widgetId, data)
        // 해당 위젯만 업데이트
        updateWidget(context, widgetId)
    }

    /**
     * 위젯 전체 업데이트
     */
    private suspend fun updateWidget(context: Context, widgetId: Int) {
        try {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)

            Log.d(TAG, "Updating widget $widgetId with glanceId $glanceId")

            // WIDGET_SYNC_KEY를 업데이트하여 위젯 갱신 트리거
            updateAppWidgetState(context, glanceId) { state ->
                state[DslAppWidget.WIDGET_SYNC_KEY] =
                    System.currentTimeMillis()
            }

            // LargeAppWidget을 직접 업데이트
            LargeAppWidget().update(context, glanceId)

            Log.d(TAG, "Widget updated: $widgetId")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating widget: $widgetId", e)
        }
    }

    /**
     * 필터 타입에 따라 Upcoming Todos 로드
     * dateTime이 null인 Todo도 포함하여 조회
     */
    suspend fun loadUpcomingTodos(
        context: Context,
        filterType: UpcomingFilterType
    ): UpcomingTasksData {
        return try {
            val repository = TodoRepository(context)
            val currentTime = System.currentTimeMillis()
            
            // 필터 타입에 따라 날짜 범위 계산
            val (startDate, endDate) = when (filterType) {
                UpcomingFilterType.TODAY -> {
                    val today = TodoDateUtils.getTodayDateString()
                    Pair(today, today)
                }
                UpcomingFilterType.TOMORROW -> {
                    val calendar = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                    val tomorrow = TodoDateUtils.formatDateString(calendar.time)
                    Pair(tomorrow, tomorrow)
                }
                UpcomingFilterType.THIS_WEEK -> {
                    val calendar = Calendar.getInstance()
                    val today = calendar.time
                    calendar.add(Calendar.DAY_OF_MONTH, 7)
                    val nextWeek = calendar.time
                    val todayStr = TodoDateUtils.formatDateString(today)
                    val nextWeekStr = TodoDateUtils.formatDateString(nextWeek)
                    Pair(todayStr, nextWeekStr)
                }
                UpcomingFilterType.ALL -> {
                    // ALL의 경우 충분히 큰 날짜 범위 사용 (예: 오늘부터 1년 후)
                    val calendar = Calendar.getInstance()
                    val today = TodoDateUtils.formatDateString(calendar.time)
                    calendar.add(Calendar.YEAR, 1)
                    val nextYear = TodoDateUtils.formatDateString(calendar.time)
                    Pair(today, nextYear)
                }
            }
            
            // dateTime이 null인 Todo도 포함하여 조회
            val allUpcomingTodos = repository.getUpcomingTodosByDateRange(
                currentTime = currentTime,
                startDate = startDate,
                endDate = endDate
            )
            
            // 정렬: dateTime이 있는 항목 우선, 그 다음 날짜 순
            val sortedTodos = allUpcomingTodos.sortedWith(compareBy(
                { it.dateTime == null }, // dateTime이 null인 항목을 뒤로
                { it.dateTime ?: Long.MAX_VALUE }, // dateTime 기준 오름차순
                { it.date }, // 날짜 기준 오름차순
                { it.createdAt } // 생성 시간 기준 오름차순
            ))

            UpcomingTasksData(
                filterType = filterType,
                todos = sortedTodos
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error loading upcoming todos", e)
            UpcomingTasksData.empty(filterType)
        }
    }
}

