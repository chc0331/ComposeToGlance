package com.widgetworld.widgetcomponent.component.reminder.upcoming

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.color.DynamicThemeColorProviders
import com.widgetworld.widgetcomponent.R
import com.widgetworld.widgetcomponent.SizeType
import com.widgetworld.widgetcomponent.WidgetCategory
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.component.datastore.ComponentDataStore
import com.widgetworld.widgetcomponent.component.reminder.today.TodoDateUtils
import com.widgetworld.widgetcomponent.component.reminder.today.TodoRepository
import com.widgetworld.widgetcomponent.component.reminder.today.TodoStatus
import com.widgetworld.widgetcomponent.component.reminder.upcoming.ui.UpcomingFilterActivity
import com.widgetworld.widgetcomponent.component.update.ComponentUpdateManager
import com.widgetworld.widgetcomponent.database.TodoEntity
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius
import com.widgetworld.core.WidgetScope
import com.widgetworld.core.frontend.CheckBox
import com.widgetworld.core.frontend.Image
import com.widgetworld.core.frontend.Spacer
import com.widgetworld.core.frontend.Text
import com.widgetworld.core.frontend.layout.Box
import com.widgetworld.core.frontend.layout.Column
import com.widgetworld.core.frontend.layout.List
import com.widgetworld.core.frontend.layout.Row
import com.widgetworld.core.proto.AlignmentType
import com.widgetworld.core.proto.FontWeight
import com.widgetworld.core.proto.HorizontalAlignment
import com.widgetworld.core.proto.TextDecoration
import com.widgetworld.core.proto.VerticalAlignment
import com.widgetworld.core.proto.modifier.WidgetModifier
import com.widgetworld.core.proto.modifier.backgroundColor
import com.widgetworld.core.proto.modifier.clickAction
import com.widgetworld.core.proto.modifier.cornerRadius
import com.widgetworld.core.proto.modifier.expandHeight
import com.widgetworld.core.proto.modifier.expandWidth
import com.widgetworld.core.proto.modifier.fillMaxHeight
import com.widgetworld.core.proto.modifier.fillMaxWidth
import com.widgetworld.core.proto.modifier.height
import com.widgetworld.core.proto.modifier.padding
import com.widgetworld.core.proto.modifier.width
import com.widgetworld.core.proto.modifier.wrapContentHeight
import com.widgetworld.core.proto.modifier.wrapContentWidth
import com.widgetworld.core.widget.action.RunWidgetCallbackAction
import com.widgetworld.core.widget.action.WidgetActionParameters
import com.widgetworld.core.widget.action.widgetActionParametersOf
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalGlanceId
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalTheme
import com.widgetworld.widgetcomponent.action.CustomWidgetActionCallbackBroadcastReceiver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * 다가오는 할 일을 표시하는 위젯
 */
class UpcomingTasksWidget : WidgetComponent() {

    override fun getName(): String = "Upcoming Tasks"

    override fun getDescription(): String = "다가오는 할 일 목록"

    override fun getWidgetCategory(): WidgetCategory = WidgetCategory.DAILY_SCHEDULE

    override fun getSizeType(): SizeType = SizeType.MEDIUM

    override fun getWidgetTag(): String = "UpcomingTasks"

    override fun WidgetScope.Content() {
        val context = getLocal(WidgetLocalContext) as Context
        val isPreview = getLocal(WidgetLocalPreview) as Boolean
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val widgetHeight = getLocal(WidgetLocalSize)?.height ?: 0.dp
        val widgetId = (getLocal(WidgetLocalGlanceId) as AppWidgetId?)?.appWidgetId ?: 0

        val filterType = if (isPreview) {
            UpcomingFilterType.TODAY
        } else {
            runBlocking {
                UpcomingTasksDataStore.loadFilterType(context, widgetId)
            }
        }

        val todos = if (isPreview) {
            getPreviewTodos()
        } else {
            runBlocking {
                UpcomingTasksUpdateManager.loadUpcomingTodos(context, filterType).todos
            }
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
                    filterType = filterType,
                    widgetId = widgetId,
                    isPreview = isPreview
                )
                TodoList(
                    modifier = WidgetModifier.fillMaxWidth().expandHeight(),
                    todos = todos
                )
                Divider(modifier = WidgetModifier.fillMaxWidth().height(1f))
                Footer(
                    modifier = WidgetModifier.fillMaxWidth().height(28f),
                    todos = todos
                )
            }
        }
    }

    /**
     * 헤더: 필터 선택 및 제목
     */
    private fun WidgetScope.Header(
        modifier: WidgetModifier = WidgetModifier,
        filterType: UpcomingFilterType,
        widgetId: Int,
        isPreview: Boolean
    ) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val widgetSize = getLocal(WidgetLocalSize) ?: DpSize.Zero

        val titleBarBackgroundColor = theme.surfaceVariant.getColor(context).toArgb()
        val textColor = theme.onSurface.getColor(context).toArgb()
        val subTextColor = Color.Gray.toArgb()
        val iconContentColor = theme.onSecondary.getColor(context)

        val fontSize = widgetSize.height.value * 0.06f
        val filterFontSize = widgetSize.height.value * 0.045f
        val iconSize = widgetSize.height.value * 0.1f

        val filterLabel = when (filterType) {
            UpcomingFilterType.TODAY -> "Today"
            UpcomingFilterType.TOMORROW -> "Tomorrow"
            UpcomingFilterType.THIS_WEEK -> "This Week"
            UpcomingFilterType.ALL -> "All"
        }

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
                // 현재 필터 타입 표시
                Text(
                    text = "Upcoming",
                    fontSize = fontSize,
                    fontWeight = FontWeight.FONT_WEIGHT_BOLD,
                    fontColor = Color(textColor)
                )
                Spacer(modifier = WidgetModifier.width(4f).fillMaxHeight())
                Box(
                    modifier = WidgetModifier.wrapContentWidth().height(fontSize),
                    contentProperty = {
                        contentAlignment = AlignmentType.ALIGNMENT_TYPE_BOTTOM_START
                    }) {
                    Text(
                        text = filterLabel,
                        fontSize = filterFontSize,
                        fontWeight = FontWeight.FONT_WEIGHT_MEDIUM,
                        fontColor = Color(subTextColor)
                    )
                }
            }
            // 필터 선택 아이콘 버튼
            if (!isPreview) {
                Box(
                    modifier = WidgetModifier
                        .width(iconSize * 0.8f)
                        .height(iconSize * 0.8f)
                        .backgroundColor(theme.primary.getColor(context).toArgb())
                        .cornerRadius(iconSize / 2)
                        .padding(2f)
                        .clickAction(
                            ComponentName(context, UpcomingFilterActivity::class.java),
                            mapOf("WIDGET_ID" to widgetId.toString())
                        ),
                    contentProperty = {
                        contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
                    }
                ) {
                    Image(
                        modifier = WidgetModifier.fillMaxWidth().fillMaxHeight(),
                        contentProperty = {
                            Provider {
                                drawableResId = R.drawable.ic_filter
                            }
                            TintColor {
                                argb = iconContentColor.toArgb()
                            }
                        }
                    )
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
                                .height(widgetSize.height.value * 0.2f),
                            todo = todo
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
                text = "다가오는 할 일이 없습니다",
                fontSize = 13f,
                fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                fontColor = Color(secondaryColor)
            )
        }
    }

    /**
     * Todo 항목
     */
    private fun WidgetScope.TodoItem(
        modifier: WidgetModifier,
        todo: TodoEntity
    ) {
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val context = getLocal(WidgetLocalContext) as Context
        val widgetId = getLocal(WidgetLocalGlanceId) as AppWidgetId?
        val completedColor = (theme?.onSurfaceVariant as? Int) ?: Color.Gray.toArgb()
        val activeColor = (theme?.onSurface as? Int) ?: Color.Black.toArgb()
        val timeColor = (theme?.onSurfaceVariant as? Int) ?: Color.Gray.toArgb()
        val isCompleted = todo.status == TodoStatus.COMPLETED

        val dateTime = todo.dateTime ?: 0L
        val timeRemaining = if (dateTime > 0) {
            TodoDateUtils.formatTimeRemaining(dateTime)
        } else {
            ""
        }
        val timeText = if (dateTime > 0) {
            TodoDateUtils.formatTime(dateTime)
        } else {
            ""
        }

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
                        RunWidgetCallbackAction(
                            CustomWidgetActionCallbackBroadcastReceiver::class.java,
                            UpcomingTasksAction::class.java,
                            widgetActionParametersOf(
                                WidgetActionParameters.Key<String>("actionClass") to (UpcomingTasksAction::class.java.canonicalName
                                    ?: ""),
                                WidgetActionParameters.Key<Int>("widgetId") to (widgetId?.appWidgetId
                                    ?: 0),
                                WidgetActionParameters.Key<Long>(UpcomingTasksAction.PARAM_TODO_ID) to todo.id
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
                }
            ) {
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
                        fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                        fontColor = if (isCompleted) Color(completedColor) else Color(activeColor),
                        textDecoration = if (isCompleted) TextDecoration.TEXT_DECORATION_LINE_THROUGH else TextDecoration.TEXT_DECORATION_NONE
                    )
                }
                if (timeRemaining.isNotEmpty()) {
                    Row(
                        modifier = WidgetModifier.wrapContentWidth().wrapContentHeight(),
                        contentProperty = {
                            horizontalAlignment = HorizontalAlignment.H_ALIGN_START
                        }
                    ) {
                        Text(
                            text = timeText,
                            fontSize = 10f,
                            fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                            fontColor = Color(timeColor),
                            modifier = WidgetModifier.padding(end = 4f)
                        )
                        Text(
                            text = "• $timeRemaining",
                            fontSize = 10f,
                            fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                            fontColor = Color(timeColor)
                        )
                    }
                }
            }
        }
    }

    /**
     * 구분선
     */
    private fun WidgetScope.Divider(modifier: WidgetModifier = WidgetModifier) {
        val context = getLocal(WidgetLocalContext) as Context
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val dividerColor = theme.outline.getColor(context = context).toArgb()
        Box(
            modifier = modifier.padding(horizontal = 12f),
            contentProperty = {
                contentAlignment = AlignmentType.ALIGNMENT_TYPE_CENTER
            }
        ) {
            Spacer(
                modifier = WidgetModifier.fillMaxWidth().height(1f).backgroundColor(dividerColor)
            )
        }
    }

    /**
     * 푸터: 통계 정보
     */
    private fun WidgetScope.Footer(
        modifier: WidgetModifier = WidgetModifier,
        todos: List<TodoEntity>
    ) {
        val theme = getLocal(WidgetLocalTheme) ?: DynamicThemeColorProviders
        val context = getLocal(WidgetLocalContext) as Context
        val subTextColor = theme.onSurfaceVariant.getColor(context).toArgb()

        val totalCount = todos.size

        Row(
            modifier = modifier.padding(horizontal = 12f),
            contentProperty = {
                horizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER
                verticalAlignment = VerticalAlignment.V_ALIGN_CENTER
            }
        ) {
            Text(
                text = "$totalCount upcoming tasks",
                fontSize = 12f,
                fontWeight = FontWeight.FONT_WEIGHT_NORMAL,
                fontColor = Color(subTextColor)
            )
        }
    }

    /**
     * Preview용 샘플 데이터
     */
    private fun getPreviewTodos(): List<TodoEntity> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            TodoEntity(
                id = 1,
                title = "팀 미팅",
                description = "프로젝트 리뷰",
                date = TodoDateUtils.getTodayDateString(),
                dateTime = currentTime + 3600000, // 1시간 후
                status = TodoStatus.INCOMPLETE
            ),
            TodoEntity(
                id = 2,
                title = "점심 약속",
                date = TodoDateUtils.getTodayDateString(),
                dateTime = currentTime + 7200000, // 2시간 후
                status = TodoStatus.INCOMPLETE
            ),
            TodoEntity(
                id = 3,
                title = "프로젝트 제출",
                date = TodoDateUtils.getTodayDateString(),
                dateTime = currentTime + 86400000, // 내일
                status = TodoStatus.INCOMPLETE
            )
        )
    }

    override fun getUpdateManager(): ComponentUpdateManager<*> = UpcomingTasksUpdateManager

    override fun getDataStore(): ComponentDataStore<*> = UpcomingTasksDataStore
}

