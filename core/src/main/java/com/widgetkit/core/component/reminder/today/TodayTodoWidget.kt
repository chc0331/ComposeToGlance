package com.widgetkit.core.component.reminder.today

import android.content.ComponentName
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.glance.appwidget.lazy.LazyColumn
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.core.component.datastore.ComponentDataStore
import com.widgetkit.core.component.lifecycle.ComponentLifecycle
import com.widgetkit.core.component.reminder.today.ui.TodoActivity
import com.widgetkit.core.component.reminder.today.ui.TodoItem
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.component.viewid.ViewIdType
import com.widgetkit.core.database.TodoDatabase
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.CheckBox
import com.widgetkit.dsl.frontend.Image
import com.widgetkit.dsl.frontend.Text
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.frontend.layout.Column
import com.widgetkit.dsl.frontend.layout.List
import com.widgetkit.dsl.frontend.layout.Row
import com.widgetkit.core.R
import com.widgetkit.core.util.getSystemBackgroundRadius
import com.widgetkit.dsl.proto.modifier.cornerRadius
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.HorizontalAlignment
import com.widgetkit.dsl.proto.TextContent
import com.widgetkit.dsl.proto.VerticalAlignment
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.clickAction
import com.widgetkit.dsl.proto.modifier.expandHeight
import com.widgetkit.dsl.proto.modifier.expandWidth
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.padding
import com.widgetkit.dsl.proto.modifier.viewId
import com.widgetkit.dsl.proto.modifier.wrapContentHeight
import com.widgetkit.dsl.proto.modifier.wrapContentWidth
import com.widgetkit.dsl.proto.property.TextPropertyDsl
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGridIndex
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.collections.isNotEmpty

/**
 * 오늘 날짜의 Todo를 표시하는 위젯
 */
class TodayTodoWidget : WidgetComponent() {

    override fun getName(): String = "Today Todo"

    override fun getDescription(): String = "오늘의 Todo를 표시합니다"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.BASIC

    override fun getSizeType(): SizeType = SizeType.MEDIUM

    override fun getWidgetTag(): String = "TodayTodo"

    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val isPreview = getLocal(WidgetLocalPreview) as Boolean
        val localSize = getLocal(WidgetLocalSize) as DpSize
        val gridIndex = getLocal(WidgetLocalGridIndex) as Int

        // 오늘 날짜의 Todo 조회
        val todayDate = TodoDateUtils.getTodayDateString()
        val todos = if (isPreview) {
            // Preview 모드: 샘플 데이터
            getPreviewTodos()
        } else {
            // 실제 모드: Room DB에서 조회
            loadTodayTodos(context, todayDate)
        }

        // 클릭 액션: TodayTodoActivity 열기
        var backgroundModifier = WidgetModifier
            .fillMaxWidth()
            .fillMaxHeight()
            .backgroundColor(Color.White.toArgb())
            .cornerRadius(context.getSystemBackgroundRadius().value)

        if (!isPreview) {
            backgroundModifier = backgroundModifier.clickAction(
                ComponentName(context, TodoActivity::class.java)
            )
        }

        Box(
            modifier = backgroundModifier,
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_TOP_START
            }
        ) {
            Column(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(12f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                }
            ) {
                TodoTitle(modifier = WidgetModifier.fillMaxWidth().wrapContentHeight())
                TodoList(modifier = WidgetModifier.fillMaxWidth().expandHeight(), todos = todos)
                // 구분선
                Box(
                    modifier = WidgetModifier
                        .fillMaxWidth()
                        .height(1f)
                        .padding(vertical = 8f)
                        .backgroundColor(Color(0xFFE0E0E0).toArgb())
                ) {}
                Footer(modifier = WidgetModifier.fillMaxWidth().wrapContentHeight(), todos = todos)
            }
        }
    }

    private fun WidgetScope.TodoTitle(
        modifier: WidgetModifier = WidgetModifier
    ) {
        val currentDate = java.util.Date()

        // 헤더: 캘린더 아이콘 + "Today" + 날짜
        Row(
            modifier = modifier,
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            Row(
                modifier = WidgetModifier.expandWidth(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                // 캘린더 아이콘
                Image(
                    modifier = WidgetModifier
                        .width(20f)
                        .height(20f)
                        .padding(end = 4f),
                    contentProperty = {
                        Provider {
                            drawableResId = R.drawable.ic_calendar
                        }
                    }
                )
                // "Today" 텍스트
                Text(
                    text = "Today",
                    fontSize = 14f,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    fontColor = Color.Black
                )
            }
            // 날짜 텍스트
            Text(
                text = TodoDateUtils.formatWidgetDate(currentDate),
                fontSize = 12f,
                fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                fontColor = Color.Gray
            )
        }

    }

    private fun WidgetScope.TodoList(
        modifier: WidgetModifier = WidgetModifier,
        todos: List<com.widgetkit.core.database.TodoEntity>
    ) {
        fun WidgetScope.TodoItem(
            todo: com.widgetkit.core.database.TodoEntity
        ) {
            Row(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 4f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                val isCompleted = todo.status == TodoStatus.COMPLETED

                CheckBox(modifier = WidgetModifier.wrapContentHeight().fillMaxWidth()) {
                    TextProperty {
                        TextContent {
                            text = todo.title
                        }
                        fontSize = 12f
                        fontWeight = if (isCompleted) {
                            FontWeight.FONT_WEIGHT_NORMAL
                        } else {
                            FontWeight.FONT_WEIGHT_MEDIUM
                        }
                        FontColor {
                            Color {
                                argb = if (isCompleted) {
                                    Color.Gray.toArgb()
                                } else {
                                    Color.Black.toArgb()
                                }
                            }
                        }
                    }
                }

                // 시간 표시 (dateTime이 있는 경우)
                if (todo.dateTime != null) {
                    Text(
                        modifier = WidgetModifier.wrapContentWidth(),
                        text = TodoDateUtils.formatTime(todo.dateTime),
                        fontSize = 10f,
                        fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                        fontColor = Color.Gray
                    )
                }
            }
        }

        List(
            modifier = modifier
                .padding(top = 12f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
            }
        ) {
            todos.forEach { todo ->
                item {
                    TodoItem(todo = todo)
                }
            }
        }
    }

    private fun WidgetScope.Footer(
        modifier: WidgetModifier = WidgetModifier,
        todos: List<com.widgetkit.core.database.TodoEntity>
    ) {
        val totalCount = todos.size
        val completedCount = todos.count { it.status == TodoStatus.COMPLETED }
        // 푸터: 요약 정보
        Row(modifier = modifier) {
            Text(
                text = "$totalCount tasks • $completedCount completed",
                fontSize = 10f,
                fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                fontColor = Color.Gray
            )
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = TodayTodoUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = TodayTodoDataStore

    override fun getLifecycle(): ComponentLifecycle? = null

    override fun requiresAutoLifecycle(): Boolean = false

    override fun getViewIdTypes(): List<ViewIdType> = TodayTodoViewIdType.all()


    /**
     * 개별 Todo 아이템 표시
     */


    /**
     * Room DB에서 오늘 날짜의 Todo를 조회
     */
    private fun loadTodayTodos(
        context: Context,
        date: String
    ): List<com.widgetkit.core.database.TodoEntity> {
        return try {
            runBlocking {
                val todoDao = TodoDatabase.getDatabase(context).todoDao()
                todoDao.getTodosByDate(date).first()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Preview 모드용 샘플 Todo 데이터
     */
    private fun getPreviewTodos(): List<com.widgetkit.core.database.TodoEntity> {
        val todayDate = TodoDateUtils.getTodayDateString()
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 17) // 5:00 PM
        calendar.set(java.util.Calendar.MINUTE, 0)
        val dateTime = calendar.timeInMillis

        return listOf(
            com.widgetkit.core.database.TodoEntity(
                id = 1,
                title = "Finish project report",
                description = null,
                date = todayDate,
                dateTime = null,
                status = TodoStatus.COMPLETED,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            com.widgetkit.core.database.TodoEntity(
                id = 2,
                title = "Grocery shopping",
                description = null,
                date = todayDate,
                dateTime = dateTime,
                status = TodoStatus.PENDING,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            com.widgetkit.core.database.TodoEntity(
                id = 3,
                title = "Gym workout",
                description = null,
                date = todayDate,
                dateTime = null,
                status = TodoStatus.INCOMPLETE,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun getTitleDate(gridIndex: Int): Int {
        return generateViewId(TodayTodoViewIdType.TitleDate, gridIndex)
    }

    fun getSelectedDate(gridIndex: Int): Int {
        return generateViewId(TodayTodoViewIdType.SelectedDate, gridIndex)
    }

    fun getAllTodoNumber(gridIndex: Int): Int {
        return generateViewId(TodayTodoViewIdType.AllTodoNumber, gridIndex)
    }

    fun getCompletedTodoNumber(gridIndex: Int): Int {
        return generateViewId(TodayTodoViewIdType.AllTodoNumber, gridIndex)
    }
}

