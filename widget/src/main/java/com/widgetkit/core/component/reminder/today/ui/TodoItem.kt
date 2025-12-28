package com.widgetkit.core.component.reminder.today.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.widgetkit.core.component.reminder.today.TodoStatus
import com.widgetkit.core.database.TodoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 개별 Todo 아이템
 */
@Composable
fun TodoItem(
    todo: TodoEntity,
    onToggleStatus: (TodoEntity) -> Unit,
    onEdit: (TodoEntity) -> Unit,
    onDelete: (TodoEntity) -> Unit
) {
    val isCompleted = todo.status == TodoStatus.COMPLETED
    val textDecoration = if (isCompleted) TextDecoration.LineThrough else null
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(todo) },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 체크 아이콘
            Icon(
                imageVector = if (isCompleted) {
                    Icons.Filled.CheckCircle
                } else {
                    Icons.Outlined.CheckCircle
                },
                contentDescription = if (isCompleted) "완료" else "미완료",
                tint = if (isCompleted) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                modifier = Modifier.clickable { onToggleStatus(todo) }
            )
            
            // Todo 내용
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = textDecoration,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                // 부가 정보 (시간, 설명)
                val subtitleParts = buildList {
                    if (todo.dateTime != null) {
                        val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
                        add(dateFormat.format(Date(todo.dateTime)))
                    }
                    if (!todo.description.isNullOrBlank()) {
                        add(todo.description)
                    }
                }
                
                if (subtitleParts.isNotEmpty()) {
                    Text(
                        text = subtitleParts.joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = textDecoration
                    )
                }
            }
            
            // 삭제 버튼
            IconButton(onClick = { onDelete(todo) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

