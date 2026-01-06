package com.widgetworld.widgetcomponent.component.reminder.upcoming.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.widgetworld.widgetcomponent.component.reminder.upcoming.UpcomingFilterType

/**
 * Upcoming Tasks 필터 선택 컨텐츠
 */
@Composable
fun UpcomingFilterContent(
    currentFilterType: UpcomingFilterType,
    widgetId: Int,
    onFilterSelected: (UpcomingFilterType) -> Unit,
    onDismiss: () -> Unit
) {
    val backgroundInteractionSource = remember { MutableInteractionSource() }
    val surfaceInteractionSource = remember { MutableInteractionSource() }
    
    val filterOptions = listOf(
        UpcomingFilterType.TODAY to "Today",
        UpcomingFilterType.TOMORROW to "Tomorrow",
        UpcomingFilterType.THIS_WEEK to "This Week",
        UpcomingFilterType.ALL to "All"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .clickable(
                indication = null,
                interactionSource = backgroundInteractionSource,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height((filterOptions.size * 60 + 60).dp)
                .clickable(
                    indication = null,
                    interactionSource = surfaceInteractionSource,
                    onClick = {}
                ),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 헤더
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "필터 선택",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "닫기"
                        )
                    }
                }
                
                HorizontalDivider()
                
                // 필터 옵션 리스트
                filterOptions.forEachIndexed { index, (filterType, label) ->
                    val isSelected = currentFilterType == filterType
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onFilterSelected(filterType)
                            }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "선택됨",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    if (index < filterOptions.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

