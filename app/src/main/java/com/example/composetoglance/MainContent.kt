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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composetoglance.editor.bottompanel.BottomPanelWithTabs
import com.example.composetoglance.editor.canvas.WidgetCanvas
import com.example.composetoglance.editor.WidgetEditorContainer
import com.example.composetoglance.editor.canvas.CanvasConstants
import com.example.composetoglance.editor.canvas.canvasBorder
import com.example.composetoglance.editor.viewmodel.WidgetEditorViewModel
import com.example.widget.component.WidgetComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    viewModel: WidgetEditorViewModel = viewModel()
) {
    val outline = MaterialTheme.colorScheme.outline
    val canvasBackgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(
        alpha = CanvasConstants.CANVAS_BACKGROUND_ALPHA
    )
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("위젯 편집", style = MaterialTheme.typography.titleLarge) },
                modifier = Modifier.height(72.dp),
                actions = {
                    TextButton(onClick = { viewModel.save(context) }) {
                        Text(
                            "저장",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondary
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
                var widgetToAdd by remember { mutableStateOf<WidgetComponent?>(null) }
                
                WidgetCanvas(
                    modifier = Modifier
                        .weight(CanvasConstants.CANVAS_WEIGHT)
                        .fillMaxWidth()
                        .padding(top = CanvasConstants.TOP_PADDING)
                        .canvasBorder(outline, canvasBackgroundColor),
                    viewModel = viewModel,
                    widgetToAdd = widgetToAdd,
                    onWidgetAddProcessed = { widgetToAdd = null }
                )

                Spacer(modifier = Modifier.size(CanvasConstants.SPACER_SIZE))

                BottomPanelWithTabs(
                    widgets = viewModel.widgets,
                    categories = viewModel.categories,
                    onLayoutSelected = { viewModel.selectLayout(it) },
                    onWidgetSelected = { widget ->
                        widgetToAdd = widget
                    },
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
