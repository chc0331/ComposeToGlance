package com.example.composetoglance.ui.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.ui.layout.ClickableLayoutComponent
import com.example.composetoglance.ui.layout.Layout
import com.example.composetoglance.ui.widget.DragTargetWidgetItem
import com.example.composetoglance.ui.widget.Widget

@Composable
fun BottomPanelWithTabs(
    widgets: List<Widget>,
    onLayoutSelected: (Layout) -> Unit,
    modifier: Modifier = Modifier
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("레이아웃", "위젯")

    Column(modifier = modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title) })
            }
        }
        when (tabIndex) {
            0 -> LayoutsTabContent(onLayoutSelected)
            1 -> WidgetsList(widgetList = widgets, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun LayoutsTabContent(onLayoutSelected: (Layout) -> Unit) {
    var activeLayout by remember { mutableStateOf<Layout?>(null) }
    val layouts = listOf(
        Layout("Full", "Small"),
        Layout("Full", "Medium"),
        Layout("Full", "Large")
    )

    LazyRow(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(layouts) { layout ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = layout.sizeType, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                ClickableLayoutComponent(
                    data = layout,
                    isClicked = activeLayout == layout,
                    onComponentClick = {
                        activeLayout = if (activeLayout == layout) null else layout
                    },
                    onAddClick = {
                        onLayoutSelected(it)
                        activeLayout = null
                    }
                )
            }
        }
    }
}

@Composable
fun WidgetsList(widgetList: List<Widget>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(widgetList) { widget ->
            DragTargetWidgetItem(
                data = widget,
            )
        }
    }
}
