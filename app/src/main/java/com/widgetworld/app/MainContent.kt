package com.widgetworld.app

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
import com.widgetworld.app.editor.bottompanel.BottomPanelWithTabs
import com.widgetworld.app.editor.canvas.WidgetCanvas
import com.widgetworld.app.editor.WidgetEditorContainer
import com.widgetworld.app.editor.canvas.canvasBorder
import com.widgetworld.app.editor.settings.GridSettingsButton
import com.widgetworld.app.editor.settings.GridSettingsPanel
import com.widgetworld.app.editor.viewmodel.WidgetEditorViewModel
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
            MainTitleBar(
                modifier = Modifier.height(72.dp),
                onSaveClicked = {
                    viewModel.save(context)
                },
                onGridSettingClicked = {
                    viewModel.showGridSettingsPanel()
                })
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
