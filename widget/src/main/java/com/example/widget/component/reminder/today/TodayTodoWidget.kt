package com.example.widget.component.reminder.today

import android.content.ComponentName
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.WidgetScope
import com.example.dsl.component.Box
import com.example.dsl.component.Column
import com.example.dsl.component.Row
import com.example.dsl.component.Text
import com.example.dsl.localprovider.WidgetLocalContext
import com.example.dsl.localprovider.WidgetLocalGlanceId
import com.example.dsl.localprovider.WidgetLocalGridIndex
import com.example.dsl.localprovider.WidgetLocalPreview
import com.example.dsl.localprovider.WidgetLocalSize
import com.example.dsl.localprovider.WidgetLocalState
import com.example.dsl.modifier.WidgetModifier
import com.example.dsl.modifier.backgroundColor
import com.example.dsl.modifier.clickAction
import com.example.dsl.modifier.fillMaxHeight
import com.example.dsl.modifier.fillMaxWidth
import com.example.dsl.modifier.height
import com.example.dsl.modifier.padding
import com.example.dsl.modifier.viewId
import com.example.dsl.modifier.wrapContentHeight
import com.example.dsl.proto.AlignmentType
import com.example.dsl.proto.FontWeight
import com.example.dsl.proto.HorizontalAlignment
import com.example.dsl.proto.VerticalAlignment
import com.example.widget.SizeType
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import com.example.widget.component.datastore.ComponentDataStore
import com.example.widget.component.lifecycle.ComponentLifecycle
import com.example.widget.component.reminder.today.ui.TodoActivity
import com.example.widget.component.update.ComponentUpdateManager
import com.example.widget.component.viewid.ViewIdType
import com.example.widget.database.TodoDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.jvm.java

/**
 * 오늘 날짜의 Todo를 표시하는 위젯
 */
class TodayTodoWidget : WidgetComponent() {

    override fun getName(): String = "Today Todo"

    override fun getDescription(): String = "오늘의 Todo를 표시합니다"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.BASIC

    override fun getSizeType(): SizeType = SizeType.SMALL

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
        
        val incompleteCount = todos.count { it.status != TodoStatus.COMPLETED }
        val completedCount = todos.count { it.status == TodoStatus.COMPLETED }
        
        // 클릭 액션: TodayTodoActivity 열기
        var backgroundModifier = WidgetModifier
            .fillMaxWidth()
            .fillMaxHeight()
            .backgroundColor(Color.White.toArgb())
        
        if (!isPreview) {
            backgroundModifier = backgroundModifier.clickAction(
                ComponentName(context, TodoActivity ::class.java)
            )
        }
        
        Box(
            modifier = backgroundModifier,
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Column(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 8f, vertical = 6f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_TOP
                }
            ) {
                // 헤더: "Today" 또는 날짜
                HeaderText(
                    date = todayDate,
                    gridIndex = gridIndex
                )
                
                // Todo 리스트 (최대 3개)
                TodoList(
                    todos = todos.take(3),
                    modifier = WidgetModifier.fillMaxWidth()
                )
                
                // 완료/미완료 개수
                if (todos.isNotEmpty()) {
                    CountText(
                        incompleteCount = incompleteCount,
                        completedCount = completedCount,
                        gridIndex = gridIndex
                    )
                }
            }
        }
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = TodayTodoUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = TodayTodoDataStore

    override fun getLifecycle(): ComponentLifecycle? = null

    override fun requiresAutoLifecycle(): Boolean = false

    override fun getViewIdTypes(): List<ViewIdType> = TodayTodoViewIdType.all()

    /**
     * 헤더 텍스트 표시
     */
    private fun WidgetScope.HeaderText(
        date: String,
        gridIndex: Int
    ) {
        val headerText = if (date == TodoDateUtils.getTodayDateString()) {
            "Today"
        } else {
            TodoDateUtils.formatHeaderDate(java.util.Date(System.currentTimeMillis()))
        }
        
        Text(
            modifier = WidgetModifier
                .viewId(generateViewId(TodayTodoViewIdType.HeaderText, gridIndex))
                .wrapContentHeight(),
            contentProperty = {
                TextContent {
                    text = headerText
                }
                fontSize = 14f
                fontWeight = FontWeight.FONT_WEIGHT_BOLD
                FontColor {
                    Color {
                        argb = Color.Black.toArgb()
                    }
                }
            }
        )
    }

    /**
     * Todo 리스트 표시
     */
    private fun WidgetScope.TodoList(
        todos: List<com.example.widget.database.TodoEntity>,
        modifier: WidgetModifier = WidgetModifier
    ) {
        if (todos.isEmpty()) {
            Text(
                modifier = modifier.padding(top = 4f),
                contentProperty = {
                    TextContent {
                        text = "No todos"
                    }
                    fontSize = 10f
                    FontColor {
                        Color {
                            argb = Color.Gray.toArgb()
                        }
                    }
                }
            )
            return
        }
        
        Column(
            modifier = modifier.padding(top = 4f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
            }
        ) {
            todos.forEachIndexed { index, todo ->
                TodoItem(todo = todo)
                if (index < todos.size - 1) {
                    // 구분선 (간단한 공백)
                    Box(
                        modifier = WidgetModifier
                            .fillMaxWidth()
                            .height(2f)
                    ){}
                }
            }
        }
    }

    /**
     * 개별 Todo 아이템 표시
     */
    private fun WidgetScope.TodoItem(
        todo: com.example.widget.database.TodoEntity
    ) {
        Row(
            modifier = WidgetModifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            // 체크 상태 표시 (간단한 텍스트)
            Text(
                modifier = WidgetModifier.padding(end = 4f),
                contentProperty = {
                    TextContent {
                        text = if (todo.status == TodoStatus.COMPLETED) "✓" else "○"
                    }
                    fontSize = 10f
                    FontColor {
                        Color {
                            argb = if (todo.status == TodoStatus.COMPLETED) {
                                Color(0xFF4CAF50).toArgb()
                            } else {
                                Color.Gray.toArgb()
                            }
                        }
                    }
                }
            )
            
            // Todo 제목
            Text(
                modifier = WidgetModifier.fillMaxWidth(),
                contentProperty = {
                    TextContent {
                        text = todo.title
                    }
                    fontSize = 10f
                    fontWeight = if (todo.status == TodoStatus.COMPLETED) {
                        FontWeight.FONT_WEIGHT_NORMAL
                    } else {
                        FontWeight.FONT_WEIGHT_MEDIUM
                    }
                    FontColor {
                        Color {
                            argb = if (todo.status == TodoStatus.COMPLETED) {
                                Color.Gray.toArgb()
                            } else {
                                Color.Black.toArgb()
                            }
                        }
                    }
                }
            )
        }
    }

    /**
     * 완료/미완료 개수 표시
     */
    private fun WidgetScope.CountText(
        incompleteCount: Int,
        completedCount: Int,
        gridIndex: Int
    ) {
        val countText = if (incompleteCount > 0 && completedCount > 0) {
            "$incompleteCount incomplete, $completedCount completed"
        } else if (incompleteCount > 0) {
            "$incompleteCount incomplete"
        } else if (completedCount > 0) {
            "$completedCount completed"
        } else {
            ""
        }
        
        if (countText.isNotEmpty()) {
            Text(
                modifier = WidgetModifier
                    .viewId(generateViewId(TodayTodoViewIdType.CountText, gridIndex))
                    .padding(top = 4f)
                    .wrapContentHeight(),
                contentProperty = {
                    TextContent {
                        text = countText
                    }
                    fontSize = 8f
                    FontColor {
                        Color {
                            argb = Color.Gray.toArgb()
                        }
                    }
                }
            )
        }
    }

    /**
     * Room DB에서 오늘 날짜의 Todo를 조회
     */
    private fun loadTodayTodos(context: Context, date: String): List<com.example.widget.database.TodoEntity> {
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
    private fun getPreviewTodos(): List<com.example.widget.database.TodoEntity> {
        val todayDate = TodoDateUtils.getTodayDateString()
        return listOf(
            com.example.widget.database.TodoEntity(
                id = 1,
                title = "Sample Todo 1",
                description = null,
                date = todayDate,
                dateTime = null,
                status = TodoStatus.INCOMPLETE,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            com.example.widget.database.TodoEntity(
                id = 2,
                title = "Sample Todo 2",
                description = null,
                date = todayDate,
                dateTime = null,
                status = TodoStatus.COMPLETED,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun getCountTextId(gridIndex: Int): Int {
        return generateViewId(TodayTodoViewIdType.CountText, gridIndex)
    }

    fun getHeaderTextId(gridIndex: Int): Int {
        return generateViewId(TodayTodoViewIdType.HeaderText, gridIndex)
    }
}

