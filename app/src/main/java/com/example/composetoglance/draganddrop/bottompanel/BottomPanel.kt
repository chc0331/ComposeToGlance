package com.example.composetoglance.draganddrop.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.R
import com.example.composetoglance.draganddrop.layout.DragTargetLayoutComponent
import com.example.composetoglance.draganddrop.layout.Layout
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(R.color.bottom_panel_background_color.toColor()))
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LayoutTypeSection("Small", "Small", listOf("Full", "1:1", "1:N"))
        }
        item {
            LayoutTypeSection("Medium", "Medium", listOf("Full"))
        }
        item {
            LayoutTypeSection("Large", "Large", listOf("Full"))
        }
    }
}

@Composable
fun LayoutTypeSection(title: String, layoutType: String, components: List<String>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(components) { componentType ->
                DragTargetLayoutComponent(data = Layout(componentType, layoutType))
            }
        }
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
