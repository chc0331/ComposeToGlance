package com.example.composetoglance.draganddrop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composetoglance.draganddrop.bottompanel.BottomPanelWithTabs
import com.example.composetoglance.draganddrop.canvas.WidgetCanvas
import com.example.composetoglance.draganddrop.layout.Layout
import com.example.composetoglance.draganddrop.widget.Widget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    val widgets = remember {
        mutableStateListOf(
            Widget("1", "2"),
            Widget("2", "3")
        )
    }
    var selectedLayout by remember { mutableStateOf<Layout?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Glance") },
                modifier = Modifier.height(64.dp)
            )
        }
    ) { paddingValues ->
        LongPressDrawable(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                WidgetCanvas(
                    selectedLayout = selectedLayout,
                    modifier = Modifier
                        .weight(2.8f)
                        .fillMaxWidth()
                )

                BottomPanelWithTabs(
                    widgets = widgets,
                    onLayoutSelected = { selectedLayout = it },
                    modifier = Modifier.weight(1.2f)
                )
            }
        }
    }
}
