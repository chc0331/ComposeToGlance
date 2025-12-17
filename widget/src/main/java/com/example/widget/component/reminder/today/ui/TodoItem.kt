package com.example.widget.component.reminder.today.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
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
import com.example.widget.component.reminder.today.TodoStatus
import com.example.widget.database.TodoEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodoItem(
    todo: TodoEntity,
    onToggleStatus: (TodoEntity) -> Unit,
    onEdit: (TodoEntity) -> Unit,
    onDelete: (TodoEntity) -> Unit
) {
    val textDecoration = if (todo.status == TodoStatus.COMPLETED) {
        TextDecoration.LineThrough
    } else {
        null
    }

    // iOS-like "cell" row (actions like edit/delete are expected to be swipe-driven by caller)
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
            Icon(
                imageVector = if (todo.status == TodoStatus.COMPLETED) {
                    Icons.Filled.CheckCircle
                } else {
                    Icons.Outlined.CheckCircle
                },
                contentDescription = if (todo.status == TodoStatus.COMPLETED) "완료" else "미완료",
                tint = if (todo.status == TodoStatus.COMPLETED) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                modifier = Modifier
                    .clickable { onToggleStatus(todo) }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = textDecoration,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (todo.status == TodoStatus.COMPLETED) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                // Subtitle line: time first, then description
                val subtitleParts = buildList {
                    if (todo.dateTime != null) {
                        val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
                        add(dateFormat.format(Date(todo.dateTime)))
                    }
                    if (!todo.description.isNullOrBlank()) add(todo.description!!)
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
        }
    }
}

