package com.widgetkit.core.component.reminder.today

import android.content.ComponentName
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.widgetkit.core.R
import com.widgetkit.core.SizeType
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.WidgetComponent
import com.widgetkit.core.component.datastore.ComponentDataStore
import com.widgetkit.core.component.reminder.today.ui.TodoActivity
import com.widgetkit.core.component.update.ComponentUpdateManager
import com.widgetkit.core.component.viewid.ViewIdType
import com.widgetkit.core.database.TodoDatabase
import com.widgetkit.core.database.TodoEntity
import com.widgetkit.core.util.getSystemBackgroundRadius
import com.widgetkit.dsl.WidgetScope
import com.widgetkit.dsl.frontend.CheckBox
import com.widgetkit.dsl.frontend.Image
import com.widgetkit.dsl.frontend.Text
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.frontend.layout.Column
import com.widgetkit.dsl.frontend.layout.List
import com.widgetkit.dsl.frontend.layout.Row
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.HorizontalAlignment
import com.widgetkit.dsl.proto.VerticalAlignment
import com.widgetkit.dsl.proto.modifier.WidgetModifier
import com.widgetkit.dsl.proto.modifier.backgroundColor
import com.widgetkit.dsl.proto.modifier.clickAction
import com.widgetkit.dsl.proto.modifier.cornerRadius
import com.widgetkit.dsl.proto.modifier.expandHeight
import com.widgetkit.dsl.proto.modifier.expandWidth
import com.widgetkit.dsl.proto.modifier.fillMaxHeight
import com.widgetkit.dsl.proto.modifier.fillMaxWidth
import com.widgetkit.dsl.proto.modifier.height
import com.widgetkit.dsl.proto.modifier.padding
import com.widgetkit.dsl.proto.modifier.width
import com.widgetkit.dsl.proto.modifier.wrapContentHeight
import com.widgetkit.dsl.proto.modifier.wrapContentWidth
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Date

/**
 * 오늘의 Todo를 표시하는 위젯
 */
class TodayTodoWidget : WidgetComponent() {
    
    override fun getName(): String = "Today Todo"
    
    override fun getDescription(): String = "오늘의 할 일 목록"
    
    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.BASIC
    
    override fun getSizeType(): SizeType = SizeType.MEDIUM_PLUS
    
    override fun getWidgetTag(): String = "TodayTodo"
    
    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val isPreview = getLocal(WidgetLocalPreview) as Boolean
        val theme = getLocal(WidgetLocalTheme)
        
        // DataStore에서 선택된 날짜 로드
        val selectedDate = if (isPreview) {
            TodoDateUtils.getTodayDateString()
        } else {
            runBlocking {
                TodayTodoDataStore.loadData(context).selectedDate
            }
        }
        
        // 선택된 날짜의 Todo 조회
        val todos = if (isPreview) {
            getPreviewTodos()
        } else {
            loadTodosFromDb(context, selectedDate)
        }
        
        val backgroundColor = (theme?.surface as? Int) ?: Color.White.toArgb()
        
        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight()
                .backgroundColor(backgroundColor)
                .cornerRadius(context.getSystemBackgroundRadius().value),
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
                // 헤더: 캘린더 아이콘 + "Today" + 날짜 + 추가 버튼
                Header(context, isPreview, selectedDate)
                
                // Todo 리스트
                TodoList(todos = todos)
                
                // 구분선
                Divider()
                
                // 푸터: 완료 개수 표시
                Footer(todos = todos)
            }
        }
    }
    
    /**
     * 헤더: 아이콘 + "Today" + 날짜 + 추가 버튼
     */
    private fun WidgetScope.Header(context: Context, isPreview: Boolean, selectedDate: String) {
        val theme = getLocal(WidgetLocalTheme)
        val titleColor = (theme?.onSurface as? Int) ?: Color.Black.toArgb()
        val secondaryColor = (theme?.onSurfaceVariant as? Int) ?: Color.Gray.toArgb()
        val primaryColor = (theme?.primary as? Int) ?: Color(0xFF6750A4).toArgb()
        
        // 선택된 날짜가 오늘인지 확인
        val isToday = TodoDateUtils.isToday(selectedDate)
        val displayText = if (isToday) "Today" else selectedDate
        val dateMillis = TodoDateUtils.parseDate(selectedDate)?.time ?: System.currentTimeMillis()
        val formattedDate = TodoDateUtils.formatWidgetDate(Date(dateMillis))
        
        Row(
            modifier = WidgetModifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(bottom = 8f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            // 왼쪽: 캘린더 아이콘 + "Today" + 날짜
            Row(
                modifier = WidgetModifier
                    .expandWidth()
                    .wrapContentHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                // 캘린더 아이콘 (클릭 가능)
                var calendarIconModifier = WidgetModifier
                    .width(20f)
                    .height(20f)
                    .padding(end = 6f)
                
                if (!isPreview) {
                    calendarIconModifier = calendarIconModifier.clickAction(
                        ComponentName(context, TodoActivity::class.java),
                        mapOf("SHOW_DATE_PICKER" to "true")
                    )
                }
                
                Image(
                    modifier = calendarIconModifier,
                    contentProperty = {
                        Provider {
                            drawableResId = R.drawable.ic_calendar
                        }
                    }
                )
                
                // "Today" 또는 날짜 텍스트
                Text(
                    modifier = WidgetModifier.padding(end = 8f),
                    text = displayText,
                    fontSize = 16f,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    fontColor = Color(titleColor)
                )
                
                // 날짜 표시
                Text(
                    text = formattedDate,
                    fontSize = 13f,
                    fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                    fontColor = Color(secondaryColor)
                )
            }
            
            // 오른쪽: 추가 버튼
            var addButtonModifier = WidgetModifier
                .width(28f)
                .height(28f)
                .backgroundColor(primaryColor)
                .cornerRadius(14f)
            
            if (!isPreview) {
                addButtonModifier = addButtonModifier.clickAction(
                    ComponentName(context, TodoActivity::class.java)
                )
            }
            
            Box(
                modifier = addButtonModifier,
                contentProperty = {
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                }
            ) {
                Text(
                    text = "+",
                    fontSize = 18f,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    fontColor = Color.White
                )
            }
        }
    }
    
    /**
     * Todo 리스트
     */
    private fun WidgetScope.TodoList(todos: List<TodoEntity>) {
        if (todos.isEmpty()) {
            EmptyState()
        } else {
            List(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .expandHeight()
                    .padding(vertical = 4f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                }
            ) {
                todos.take(5).forEach { todo ->
                    item {
                        TodoItem(todo)
                    }
                }
            }
        }
    }
    
    /**
     * 빈 상태 표시
     */
    private fun WidgetScope.EmptyState() {
        val theme = getLocal(WidgetLocalTheme)
        val secondaryColor = (theme?.onSurfaceVariant as? Int) ?: Color.Gray.toArgb()
        
        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .expandHeight(),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Text(
                text = "할 일이 없습니다",
                fontSize = 13f,
                fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                fontColor = Color(secondaryColor)
            )
        }
    }
    
    /**
     * 개별 Todo 아이템
     */
    private fun WidgetScope.TodoItem(todo: TodoEntity) {
        val theme = getLocal(WidgetLocalTheme)
        val completedColor = (theme?.onSurfaceVariant as? Int) ?: Color.Gray.toArgb()
        val activeColor = (theme?.onSurface as? Int) ?: Color.Black.toArgb()
        
        val isCompleted = todo.status == TodoStatus.COMPLETED
        
        Row(
            modifier = WidgetModifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 3f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            CheckBox(
                modifier = WidgetModifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                TextProperty {
                    TextContent {
                        text = todo.title
                    }
                    fontSize = 13f
                    fontWeight = if (isCompleted) {
                        FontWeight.FONT_WEIGHT_NORMAL
                    } else {
                        FontWeight.FONT_WEIGHT_MEDIUM
                    }
                    FontColor {
                        Color {
                            argb = if (isCompleted) completedColor else activeColor
                        }
                    }
                }
            }
            
            // 시간 표시
            if (todo.dateTime != null) {
                Text(
                    modifier = WidgetModifier
                        .wrapContentWidth()
                        .padding(start = 4f),
                    text = TodoDateUtils.formatTime(todo.dateTime),
                    fontSize = 10f,
                    fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                    fontColor = Color(completedColor)
                )
            }
        }
    }
    
    /**
     * 구분선
     */
    private fun WidgetScope.Divider() {
        val theme = getLocal(WidgetLocalTheme)
        val dividerColor = (theme?.outlineVariant as? Int) ?: Color(0xFFE0E0E0).toArgb()
        
        Box(
            modifier = WidgetModifier
                .fillMaxWidth()
                .height(1f)
                .padding(vertical = 8f)
                .backgroundColor(dividerColor)
        ) {}
    }
    
    /**
     * 푸터: 완료 개수 표시
     */
    private fun WidgetScope.Footer(todos: List<TodoEntity>) {
        val theme = getLocal(WidgetLocalTheme)
        val secondaryColor = (theme?.onSurfaceVariant as? Int) ?: Color.Gray.toArgb()
        
        val totalCount = todos.size
        val completedCount = todos.count { it.status == TodoStatus.COMPLETED }
        
        Row(
            modifier = WidgetModifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = "$totalCount tasks • $completedCount completed",
                fontSize = 11f,
                fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                fontColor = Color(secondaryColor)
            )
        }
    }
    
    /**
     * DB에서 Todo 로드
     */
    private fun loadTodosFromDb(context: Context, date: String): List<TodoEntity> {
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
     * Preview용 샘플 데이터
     */
    private fun getPreviewTodos(): List<TodoEntity> {
        val todayDate = TodoDateUtils.getTodayDateString()
        return listOf(
            TodoEntity(
                id = 1,
                title = "팀 미팅 준비",
                date = todayDate,
                dateTime = System.currentTimeMillis() + 3600000,
                status = TodoStatus.INCOMPLETE
            ),
            TodoEntity(
                id = 2,
                title = "프로젝트 리뷰",
                date = todayDate,
                status = TodoStatus.COMPLETED
            ),
            TodoEntity(
                id = 3,
                title = "점심 약속",
                date = todayDate,
                dateTime = System.currentTimeMillis() + 7200000,
                status = TodoStatus.INCOMPLETE
            )
        )
    }
    
    override fun getUpdateManager(): ComponentUpdateManager<*> = TodayTodoUpdateManager
    
    override fun getDataStore(): ComponentDataStore<*> = TodayTodoDataStore
    
    override fun getViewIdTypes(): List<ViewIdType> = TodayTodoViewIdType.all()
}

