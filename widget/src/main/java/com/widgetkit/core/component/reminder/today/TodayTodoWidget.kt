package com.widgetkit.core.component.reminder.today

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.color.DynamicThemeColorProviders
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
import com.widgetkit.dsl.frontend.Spacer
import com.widgetkit.dsl.frontend.Text
import com.widgetkit.dsl.frontend.layout.Box
import com.widgetkit.dsl.frontend.layout.Column
import com.widgetkit.dsl.frontend.layout.List
import com.widgetkit.dsl.frontend.layout.Row
import com.widgetkit.dsl.proto.AlignmentType
import com.widgetkit.dsl.proto.FontWeight
import com.widgetkit.dsl.proto.HorizontalAlignment
import com.widgetkit.dsl.proto.TextDecoration
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
import com.widgetkit.dsl.widget.action.WidgetActionParameters
import com.widgetkit.dsl.widget.action.widgetActionParametersOf
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalGlanceId
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetkit.dsl.widget.widgetlocalprovider.WidgetLocalSize
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

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DAILY_SCHEDULE

    override fun getSizeType(): SizeType = SizeType.MEDIUM

    override fun getWidgetTag(): String = "TodayTodo"

    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val isPreview = getLocal(WidgetLocalPreview) as Boolean
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val widgetHeight = getLocal(WidgetLocalSize)?.height ?: 0.dp
        val widgetId = (getLocal(WidgetLocalGlanceId) as AppWidgetId?)?.appWidgetId ?: 0
        val selectedDate = if (isPreview) {
            TodoDateUtils.getTodayDateString()
        } else {
            runBlocking {
                TodayTodoDataStore.loadData(context, widgetId).selectedDate
            }
        }
        val todos = if (isPreview) {
            getPreviewTodos()
        } else {
            loadTodosFromDb(context, selectedDate)
        }
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
                    .fillMaxHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                }
            ) {
                Header(
                    modifier = WidgetModifier.fillMaxWidth().height(widgetHeight.value * 0.16f),
                    isPreview,
                    selectedDate,
                    widgetId
                )
                TodoList(modifier = WidgetModifier.fillMaxWidth().expandHeight(), todos = todos)
                Divider(modifier = WidgetModifier.fillMaxWidth().height(1f))
                Footer(modifier = WidgetModifier.fillMaxWidth().height(28f), todos = todos)
            }
        }
    }

    private fun WidgetScope.Header(
        modifier: WidgetModifier = WidgetModifier,
        isPreview: Boolean,
        selectedDate: String,
        widgetId: Int
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val widgetSize = getLocal(WidgetLocalSize) ?: DpSize.Zero
        val isToday = TodoDateUtils.isToday(selectedDate)
        val displayText = if (isToday) "Today" else TodoDateUtils.formatShortDate(selectedDate)
        val dateMillis = TodoDateUtils.parseDate(selectedDate)?.time ?: System.currentTimeMillis()
        val formattedDate = TodoDateUtils.formatWidgetDate(Date(dateMillis))

        val titleBarBackgroundColor = theme.surfaceVariant.getColor(context).toArgb()
        val iconColor = theme.primary.getColor(context).toArgb()
        val iconContentColor = theme.onSecondary.getColor(context)
        val mainTextColor = theme.onSurface.getColor(context).toArgb()

        val iconSize = widgetSize.height.value * 0.1f
        val mainTextSize = widgetSize.height.value * 0.06f
        Row(
            modifier = modifier
                .backgroundColor(titleBarBackgroundColor)
                .padding(horizontal = 12f, vertical = 4f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            Row(
                modifier = WidgetModifier
                    .expandWidth()
                    .wrapContentHeight(),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }
            ) {
                Box(
                    modifier = WidgetModifier
                        .width(iconSize)
                        .height(iconSize)
                        .padding(all = 2f).clickAction(
                            ComponentName(context, TodoActivity::class.java),
                            mapOf("SHOW_DATE_PICKER" to "true", "WIDGET_ID" to widgetId.toString())
                        ), contentProperty = {
                        contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                    }
                ) {
                    Image(
                        modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                        contentProperty = {
                            Provider {
                                drawableResId = R.drawable.ic_calendar
                            }
                            TintColor {
                                argb = iconColor
                            }
                        }
                    )
                }

                Row(
                    modifier = WidgetModifier.expandWidth().wrapContentHeight()
                        .padding(start = 6f),
                    contentProperty = {
                    }
                ) {
                    Text(
                        modifier = WidgetModifier.padding(end = 6f),
                        text = displayText,
                        fontSize = mainTextSize,
                        fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                        fontColor = Color(mainTextColor)
                    )
                }
            }
            Box(
                modifier = WidgetModifier
                    .width(iconSize * 0.8f)
                    .height(iconSize * 0.8f)
                    .backgroundColor(iconColor)
                    .cornerRadius(iconSize / 2).clickAction(
                        ComponentName(context, TodoActivity::class.java),
                        mapOf("WIDGET_ID" to widgetId.toString())
                    ),
                contentProperty = {
                    contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                }
            ) {
                Image(modifier = WidgetModifier.fillMaxWidth().fillMaxHeight()) {
                    Provider {
                        drawableResId = R.drawable.ic_add
                    }
                    TintColor {
                        argb = iconContentColor.toArgb()
                    }
                }
            }
        }
    }

    /**
     * Todo 리스트
     */
    private fun WidgetScope.TodoList(
        modifier: WidgetModifier = WidgetModifier,
        todos: List<TodoEntity>
    ) {
        val widgetSize = getLocal(WidgetLocalSize) ?: DpSize.Zero
        if (todos.isEmpty()) {
            EmptyState()
        } else {
            List(
                modifier = modifier.padding(horizontal = 0f, vertical = 4f),
                contentProperty = {
                    horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                }
            ) {
                todos.forEach { todo ->
                    item {
                        TodoItem(
                            modifier = WidgetModifier.fillMaxWidth()
                                .height(widgetSize.height.value * 0.24f), todo
                        )
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

    private fun WidgetScope.TodoItem(modifier: WidgetModifier, todo: TodoEntity) {
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val context = getLocal(WidgetLocalContext) as Context
        val widgetId = getLocal(WidgetLocalGlanceId) as AppWidgetId?
        val completedColor = (theme?.onSurfaceVariant as? Int) ?: Color.Gray.toArgb()
        val activeColor = (theme?.onSurface as? Int) ?: Color.Black.toArgb()
        val isCompleted = todo.status == TodoStatus.COMPLETED

        Row(
            modifier = modifier.padding(vertical = 2f, horizontal = 4f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            CheckBox(
                modifier = WidgetModifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .padding(vertical = 4f, horizontal = 4f)
                    .clickAction(
                        context,
                        com.widgetkit.dsl.widget.action.RunWidgetCallbackAction(
                            com.widgetkit.core.action.CustomWidgetActionCallbackBroadcastReceiver::class.java,
                            TodayTodoAction::class.java,
                            widgetActionParametersOf(
                                WidgetActionParameters.Key<String>("actionClass") to TodayTodoAction::class.java.canonicalName,
                                WidgetActionParameters.Key<Int>("widgetId") to (widgetId?.appWidgetId
                                    ?: 0),
                                WidgetActionParameters.Key<Long>(TodayTodoAction.PARAM_TODO_ID) to todo.id
                            )
                        )
                    )
            ) {
                checked = isCompleted
                CheckedColor {
                    Color {
                        argb = theme.primary.getColor(context).toArgb()
                    }

                }
                UncheckedColor {
                    Color {
                        argb = theme.outline.getColor(context).toArgb()
                    }
                }
            }
            Column(
                modifier = WidgetModifier.expandWidth().fillMaxHeight(),
                contentProperty = {
                    verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
                }) {
                Text(
                    modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(),
                    text = todo.title,
                    fontSize = 13f,
                    fontWeight = if (isCompleted) FontWeight.FONT_WEIGHT_NORMAL else FontWeight.FONT_WEIGHT_MEDIUM,
                    fontColor = if (isCompleted) Color(completedColor) else Color(activeColor),
                    textDecoration = if (isCompleted) TextDecoration.TEXT_DECORATION_LINE_THROUGH else TextDecoration.TEXT_DECORATION_NONE
                )
                todo.description?.let {
                    Text(
                        modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(),
                        text = it,
                        fontSize = 8f,
                        fontWeight = if (isCompleted) FontWeight.FONT_WEIGHT_NORMAL else FontWeight.FONT_WEIGHT_NORMAL,
                        fontColor = if (isCompleted) Color(completedColor) else Color(activeColor),
                        textDecoration = if (isCompleted) TextDecoration.TEXT_DECORATION_LINE_THROUGH else TextDecoration.TEXT_DECORATION_NONE
                    )

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

    private fun WidgetScope.Divider(modifier: WidgetModifier = WidgetModifier) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val dividerColor = theme.outline.getColor(context = context).toArgb()
        Box(modifier = modifier.padding(horizontal = 12f), contentProperty = {
            contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
        }) {
            Spacer(
                modifier = WidgetModifier.fillMaxWidth().height(1f).backgroundColor(dividerColor)
            )
        }
    }

    private fun WidgetScope.Footer(
        modifier: WidgetModifier = WidgetModifier,
        todos: List<TodoEntity>
    ) {
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val context = getLocal(WidgetLocalContext) as Context
        val subTextColor = theme.onSurfaceVariant.getColor(context).toArgb()

        val totalCount = todos.size
        val completedCount = todos.count { it.status == TodoStatus.COMPLETED }

        Row(
            modifier = modifier.padding(horizontal = 12f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            Text(
                text = "$totalCount tasks • $completedCount completed",
                fontSize = 12f,
                fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                fontColor = Color(subTextColor)
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

