package com.example.composetoglance.draganddrop

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val outline = MaterialTheme.colorScheme.outline

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("위젯 편집") },
                modifier = Modifier.height(64.dp),
                actions = {
                    TextButton(onClick = { /*TODO: 저장 기능 구현*/ }) {
                        Text("저장")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LongPressDrawable(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)) {
                WidgetCanvas(
                    selectedLayout = selectedLayout,
                    modifier = Modifier
                        .weight(2.8f)
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .drawBehind {
                            val stroke = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                            drawRect(
                                color = outline,
                                style = stroke
                            )
                        }
                )

                BottomPanelWithTabs(
                    widgets = widgets,
                    onLayoutSelected = { selectedLayout = it },
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .border(
                            1.dp,
                            outline,
                            RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                )
            }
        }
    }
}
