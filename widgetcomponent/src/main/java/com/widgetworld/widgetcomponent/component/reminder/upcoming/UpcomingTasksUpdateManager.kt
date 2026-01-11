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
import com.widgetworld.widgetcomponent.provider.LargeAppWidget
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
                state[ComponentContainerWidget.WIDGET_SYNC_KEY] =
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
            val sortedTodos = allTodos.sortedWith(compareBy(
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

