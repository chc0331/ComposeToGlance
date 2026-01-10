package com.widgetworld.widget

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.widgetworld.widget.editor.bottompanel.BottomPanelWithTabs
import com.widgetworld.widget.editor.canvas.WidgetCanvas
import com.widgetworld.widget.editor.WidgetEditorContainer
import com.widgetworld.widget.editor.canvas.CanvasConstants
import com.widgetworld.widget.editor.canvas.canvasBorder
import com.widgetworld.widget.editor.settings.GridSettingsButton
import com.widgetworld.widget.editor.settings.GridSettingsPanel
import com.widgetworld.widget.editor.viewmodel.WidgetEditorViewModel
import com.widgetworld.widgetcomponent.component.WidgetComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    viewModel: WidgetEditorViewModel = viewModel()
) {
    val outline = MaterialTheme.colorScheme.outline
    val canvasBackgroundColor = MaterialTheme.colorScheme.outlineVariant.copy(
        alpha = 0.05f
    )
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "위젯 편집", style = MaterialTheme.typography.titleLarge
                    )
                },
                modifier = Modifier.height(72.dp),
                actions = {
                    // 그리드 설정 버튼
                    GridSettingsButton(
                        onClick = { viewModel.showGridSettingsPanel() }
                    )
                    TextButton(onClick = { viewModel.save(context) }) {
                        Text(
                            "저장",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        val gridSettings by viewModel.gridSettings.collectAsState()

        // 그리드 설정 초기화
        LaunchedEffect(Unit) {
            viewModel.initializeGridSettings(context)
        }

        WidgetEditorContainer(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp)
            ) {
                var widgetToAdd by remember { mutableStateOf<WidgetComponent?>(null) }

                WidgetCanvas(
                    modifier = Modifier
                        .weight(2.2f)
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .canvasBorder(outline, canvasBackgroundColor),
                    viewModel = viewModel,
                    widgetToAdd = widgetToAdd,
                    onWidgetAddProcessed = { widgetToAdd = null }
                )

                Spacer(modifier = Modifier.size(6.dp))

                BottomPanelWithTabs(
                    widgets = viewModel.widgets,
                    categories = viewModel.categories,
                    onLayoutSelected = { viewModel.selectLayout(it) },
                    onWidgetSelected = { widget ->
                        widgetToAdd = widget
                    },
                    selectedLayout = viewModel.selectedLayout,
                    modifier = Modifier
                        .weight(2f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            outline,
                            RoundedCornerShape(12.dp)
                        )
                )
            }
        }

        // 그리드 설정 패널
        GridSettingsPanel(
            isVisible = viewModel.showGridSettings,
            onDismiss = { viewModel.hideGridSettingsPanel() },
            onSettingsChanged = { newSettings ->
                viewModel.updateGridSettingsAndClearWidgets(newSettings)
            }
        )
    }
}
