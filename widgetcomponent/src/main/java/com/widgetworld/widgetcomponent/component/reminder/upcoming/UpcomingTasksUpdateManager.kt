package com.widgetworld.widgetcomponent.component.reminder.upcoming

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.widgetworld.widgetcomponent.component.reminder.today.TodoDateUtils
import com.widgetworld.widgetcomponent.component.reminder.today.TodoRepository
import com.widgetworld.widgetcomponent.component.reminder.today.TodoStatus
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateHelper
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.provider.common.ComponentContainerWidget
import kotlinx.coroutines.flow.first
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
        updateByState(context, null, data)
    }

    override suspend fun updateByPartially(
        context: Context,
        widgetId: Int?,
        data: UpcomingTasksData
    ) {
        // 부분 업데이트는 지원하지 않음
    }

    override suspend fun updateByState(context: Context, widgetId: Int?, data: UpcomingTasksData) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        if (widgetId != null) {
            UpcomingTasksDataStore.saveData(context, widgetId, data)
            val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
            updateUpcomingWidgetState(context, widgetId, data)
            ComponentUpdateHelper.getGlanceAppWidgetForWidgetId(context, widgetId)
                ?.update(context, glanceId)
        } else {
            ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
                .forEach { (id, _, glanceAppWidget) ->
                    // 각 위젯별로 필터 타입 로드하여 데이터 업데이트
                    val filterType = UpcomingTasksDataStore.loadFilterType(context, id)
                    val updatedData = loadUpcomingTodos(context, filterType)
                    val glanceId = glanceAppWidgetManager.getGlanceIdBy(id)
                    updateUpcomingWidgetState(context, id, updatedData)
                    glanceAppWidget.update(context, glanceId)
                }
        }
    }

    private suspend fun updateUpcomingWidgetState(
        context: Context,
        widgetId: Int,
        data: UpcomingTasksData
    ) {
        val glanceAppWidgetManager = GlanceAppWidgetManager(context)
        val glanceId = glanceAppWidgetManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { state ->
            state[ComponentContainerWidget.WIDGET_SYNC_KEY] = System.currentTimeMillis()
        }
    }

    /**
     * 필터 타입에 따라 Upcoming Todos 로드
     * - dateTime이 null인 모든 Todo (항상 표시)
     * - 필터 타입에 맞는 날짜 범위의 Todo 표시
     */
    suspend fun loadUpcomingTodos(
        context: Context,
        filterType: UpcomingFilterType
    ): UpcomingTasksData {
        return try {
            val repository = TodoRepository(context)
            val currentTime = System.currentTimeMillis()

            // 1. dateTime이 null인 모든 Todo 조회 (항상 표시되는 Todo)
            val todosWithoutDateTime = repository.getTodosWithoutDateTime()

            // 2. 필터 타입에 따라 날짜 범위 계산 및 해당 날짜의 Todo 조회
            val filteredTodos = when (filterType) {
                UpcomingFilterType.TODAY -> {
                    val today = TodoDateUtils.getTodayDateString()
                    repository.getTodosByDateSync(today)
                }

                UpcomingFilterType.TOMORROW -> {
                    val calendar = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                    val tomorrow = TodoDateUtils.formatDateString(calendar.time)
                    repository.getTodosByDateSync(tomorrow)
                }

                UpcomingFilterType.THIS_WEEK -> {
                    val calendar = Calendar.getInstance()
                    val today = TodoDateUtils.formatDateString(calendar.time)
                    calendar.add(Calendar.DAY_OF_MONTH, 6) // 오늘부터 6일 후까지 (총 7일)
                    val endDate = TodoDateUtils.formatDateString(calendar.time)
                    // 날짜 범위의 모든 Todo 조회
                    repository.getTodosByDateRange(today, endDate).first()
                        .filter { it.status != TodoStatus.COMPLETED }
                }

                UpcomingFilterType.ALL -> {
                    // 모든 미래 Todo 조회 (dateTime >= currentTime)
                    repository.getUpcomingTodos(currentTime)
                }
            }

            // 3. 두 결과를 합치고 중복 제거 (id 기준)
            val allTodos = (todosWithoutDateTime + filteredTodos)
                .distinctBy { it.id }
                .filter { it.status != TodoStatus.COMPLETED }

            // 정렬: dateTime이 있는 항목 우선, 그 다음 날짜 순
            val sortedTodos = allTodos.sortedWith(
                compareBy(
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

