package com.widgetworld.app

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.widgetworld.app.editor.settings.GridSettingsButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTitleBar(
    onGridSettingClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "위젯 편집", style = MaterialTheme.typography.titleLarge
            )
        },
        modifier = modifier,
        actions = {
            // 그리드 설정 버튼
            GridSettingsButton(onClick = { onGridSettingClicked() })
            TextButton(onClick = { onSaveClicked() }) {
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