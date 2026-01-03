package com.widgetkit.widget.editor.bottompanel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.widgetkit.widget.editor.widget.Layout
import com.widgetkit.core.WidgetCategory
import com.widgetkit.core.component.WidgetComponent

val DefaultLayouts = listOf(
    Layout("Small"),
    Layout("Medium"),
    Layout("Large"),
    Layout("ExtraLarge")
)

object BottomPanelConstants {
    // 기타 코드에서 사용 가능하게 public
    val TAB_PADDING_TOP = 8.dp
    val TAB_PADDING_HORIZONTAL = 16.dp
    val TAB_PADDING_BOTTOM = 8.dp
    val LAYOUT_SPACING = 4.dp
    val LAYOUT_TEXT_SIZE = 16.sp
    val LAYOUT_ITEM_SPACING = 4.dp
    val WIDGET_LIST_PADDING = 16.dp
    val WIDGET_LIST_SPACING = 8.dp
    val TAB_INDICATOR_WIDTH = 80.dp // Tab Indicator의 너비 (짧게 설정)
    val TAB_INDICATOR_HEIGHT = 3.dp // Tab Indicator의 높이
}

@Composable
fun BottomPanelWithTabs(
    widgets: List<WidgetComponent>,
    categories: List<WidgetCategory>,
    onLayoutSelected: (Layout) -> Unit,
    modifier: Modifier = Modifier,
    onWidgetSelected: (WidgetComponent) -> Unit = {},
    selectedLayout: Layout? = null
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("레이아웃", "위젯")
    val isWidgetTabEnabled = selectedLayout != null

    // 레이아웃이 선택 해제되면 레이아웃 탭으로 자동 전환
    LaunchedEffect(selectedLayout) {
        if (selectedLayout == null && tabIndex == 1) {
            tabIndex = 0
        }
    }

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[tabIndex])
                        .fillMaxHeight()
                        .width(BottomPanelConstants.TAB_INDICATOR_WIDTH),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .width(BottomPanelConstants.TAB_INDICATOR_WIDTH)
                            .height(BottomPanelConstants.TAB_INDICATOR_HEIGHT)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val isTabEnabled = index != 1 || isWidgetTabEnabled
                val isWidgetTabDisabled = index == 1 && !isWidgetTabEnabled
                Tab(
                    selected = tabIndex == index,
                    onClick = { 
                        if (index == 1 && !isWidgetTabEnabled) {
                            // 위젯 탭이 비활성화된 경우 클릭 무시
                            return@Tab
                        }
                        tabIndex = index 
                    },
                    enabled = isTabEnabled,
                    text = { 
                        Text(
                            text = title,
                            color = if (isWidgetTabDisabled) {
                                MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f) // disabled 색상
                            } else {
                                MaterialTheme.colorScheme.onSecondary
                            }
                        ) 
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = if (isWidgetTabDisabled) {
                        MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.38f) // disabled 색상
                    } else {
                        MaterialTheme.colorScheme.onSecondary
                    }
                )
            }
        }
        AnimatedContent(
            targetState = tabIndex,
            transitionSpec = {
                fadeIn(tween(durationMillis = 400)) togetherWith fadeOut(tween(durationMillis = 200))
            }) { tabIndex ->
            when (tabIndex) {
                0 -> LayoutsTabContent(onLayoutSelected)
                1 -> WidgetsList(
                    widgetList = widgets,
                    categories = categories,
                    onWidgetSelected = onWidgetSelected,
                    modifier = Modifier
                )
            }
        }
    }
}
