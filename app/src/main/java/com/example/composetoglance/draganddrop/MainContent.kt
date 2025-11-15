package com.example.composetoglance.draganddrop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.composetoglance.draganddrop.bottompanel.BottomPanelWithTabs
import com.example.composetoglance.draganddrop.canvas.WidgetCanvas
import com.example.composetoglance.draganddrop.layout.Layout
import com.example.composetoglance.draganddrop.widget.Widget

@Composable
fun MainContent() {
    val widgets = remember {
        mutableStateListOf(
            Widget("1", "2"),
            Widget("2", "3")
        )
    }
    var selectedLayout by remember { mutableStateOf<Layout?>(null) }

    LongPressDrawable(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            WidgetCanvas(
                selectedLayout = selectedLayout,
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
            )

            BottomPanelWithTabs(
                widgets = widgets,
                onLayoutSelected = { selectedLayout = it },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
