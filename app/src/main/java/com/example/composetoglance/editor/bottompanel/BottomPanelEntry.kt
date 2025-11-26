package com.example.composetoglance.editor.bottompanel

import androidx.compose.foundation.layout.Column
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
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                    color = MaterialTheme.colorScheme.secondary
                )
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
