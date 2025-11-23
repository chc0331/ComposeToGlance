package com.example.composetoglance

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composetoglance.bottompanel.BottomPanelWithTabs
import com.example.composetoglance.modifier.CanvasConstants
import com.example.composetoglance.modifier.canvasBorder
import com.example.composetoglance.viewmodel.WidgetEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    viewModel: WidgetEditorViewModel = viewModel()
) {
    val outline = MaterialTheme.colorScheme.outline

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("위젯 편집") },
                modifier = Modifier.height(64.dp),
                actions = {
                    TextButton(onClick = { viewModel.save() }) {
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
        WidgetEditorContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = CanvasConstants.HORIZONTAL_PADDING)
            ) {
                WidgetCanvas(
                    modifier = Modifier
                        .weight(CanvasConstants.CANVAS_WEIGHT)
                        .fillMaxWidth()
                        .padding(top = CanvasConstants.TOP_PADDING)
                        .canvasBorder(outline),
                    viewModel = viewModel,
                )

                Spacer(modifier = Modifier.size(CanvasConstants.SPACER_SIZE))

                BottomPanelWithTabs(
                    widgets = viewModel.widgets,
                    categories = viewModel.categories,
                    onLayoutSelected = { viewModel.selectLayout(it) },
                    modifier = Modifier
                        .weight(CanvasConstants.BOTTOM_PANEL_WEIGHT)
                        .clip(RoundedCornerShape(CanvasConstants.BOTTOM_PANEL_CORNER_RADIUS))
                        .border(
                            CanvasConstants.BORDER_WIDTH,
                            outline,
                            RoundedCornerShape(CanvasConstants.BOTTOM_PANEL_CORNER_RADIUS)
                        )
                )
            }
        }
    }
}
