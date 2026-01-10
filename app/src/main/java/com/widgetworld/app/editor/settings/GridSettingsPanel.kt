package com.widgetworld.app.editor.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

/**
 * 그리드 설정 패널 UI 컴포넌트
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridSettingsPanel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSettingsChanged: (GridSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentSettings by remember { mutableStateOf(GridSettings.DEFAULT) }
    var initialSettings by remember { mutableStateOf<GridSettings?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    // 설정 로드
    LaunchedEffect(isVisible) {
        if (isVisible) {
            scope.launch {
                val loadedSettings = GridSettingsDataStore.loadSettings(context)
                currentSettings = loadedSettings
                initialSettings = loadedSettings
                isLoading = false
            }
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "그리드 설정",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    TextButton(onClick = {
                        scope.launch {
                            // 변경사항이 있으면 저장하고 콜백 호출
                            if (initialSettings?.globalMultiplier != currentSettings.globalMultiplier) {
                                GridSettingsDataStore.saveSettings(context, currentSettings)
                                onSettingsChanged(currentSettings)
                            }
                            onDismiss()
                        }
                    }) {
                        Text("완료")
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // 전역 설정만 표시
                    GlobalMultiplierSection(
                        currentMultiplier = currentSettings.globalMultiplier,
                        onMultiplierChanged = { newMultiplier ->
                            // 로컬 상태만 업데이트 (즉시 저장하지 않음)
                            currentSettings = currentSettings.withGlobalMultiplier(newMultiplier)
                        }
                    )
                }
            }
        }
    }
}

/**
 * 전역 배수 설정 섹션
 */
@Composable
private fun GlobalMultiplierSection(
    currentMultiplier: Int,
    onMultiplierChanged: (Int) -> Unit
) {
    Column {
        Text(
            text = "그리드 배수 설정",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "모든 레이아웃에 적용되는 그리드 밀도를 설정합니다. 높은 배수일수록 더 세밀한 그리드를 제공합니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        MultiplierSelector(
            selectedMultiplier = currentMultiplier,
            onMultiplierSelected = onMultiplierChanged
        )
    }
}

/**
 * 배수 선택 컴포넌트
 */
@Composable
private fun MultiplierSelector(
    selectedMultiplier: Int,
    onMultiplierSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GridSettings.VALID_MULTIPLIERS.forEach { multiplier ->
            val isSelected = selectedMultiplier == multiplier
            
            FilterChip(
                selected = isSelected,
                onClick = { onMultiplierSelected(multiplier) },
                label = {
                    Text("${multiplier}x")
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 그리드 설정 버튼 (메인 UI에서 사용)
 */
@Composable
fun GridSettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "그리드 설정",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}