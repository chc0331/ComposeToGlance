package com.example.composetoglance.draganddrop.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composetoglance.R
import com.example.composetoglance.draganddrop.widget.DragTargetWidgetItem
import com.example.composetoglance.draganddrop.widget.Widget
import com.example.composetoglance.util.toColor

@Composable
fun BottomPanelWithTabs(widgets: List<Widget>, modifier: Modifier = Modifier) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("레이아웃", "위젯")

    Column(modifier = modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title) })
            }
        }
        when (tabIndex) {
            0 -> LayoutsTabContent()
            1 -> WidgetsList(widgetList = widgets, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun LayoutsTabContent() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(R.color.bottom_panel_background_color.toColor())),
        contentAlignment = Alignment.Center
    ) {
        Text("레이아웃 탭")
    }
}

@Composable
fun WidgetsList(widgetList: List<Widget>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(R.color.bottom_panel_background_color.toColor())),
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
