package com.widgetworld.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.widgetworld.app.editor.WidgetEditorScreen
import com.widgetworld.app.editor.settings.GridSettingsPanel
import com.widgetworld.app.editor.viewmodel.WidgetEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: WidgetEditorViewModel = viewModel()
) {
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
        WidgetEditorScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
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
