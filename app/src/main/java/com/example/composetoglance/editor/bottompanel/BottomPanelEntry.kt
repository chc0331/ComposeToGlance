package com.example.composetoglance.editor.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.editor.widget.Layout
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent

val DefaultLayouts = listOf(
    Layout("Full", "Small"),
    Layout("Full", "Medium"),
    Layout("Full", "Large")
)

object BottomPanelConstants {
    // 기타 코드에서 사용 가능하게 public
    val TAB_PADDING_TOP = 8.dp
    val TAB_PADDING_HORIZONTAL = 16.dp
    val TAB_PADDING_BOTTOM = 8.dp
    val LAYOUT_SPACING = 16.dp
    val LAYOUT_TEXT_SIZE = 16.sp
    val LAYOUT_ITEM_SPACING = 8.dp
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
    onWidgetSelected: (WidgetComponent) -> Unit = {}
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("레이아웃", "위젯")

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
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
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title) },
                    selectedContentColor = MaterialTheme.colorScheme.secondary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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
