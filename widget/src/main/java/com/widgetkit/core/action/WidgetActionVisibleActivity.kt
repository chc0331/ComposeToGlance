package com.widgetkit.core.action

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Process
import android.os.storage.StorageManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.widgetkit.core.component.devicecare.StorageCollector
import com.widgetkit.core.component.devicecare.StorageDetailInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 위젯 클릭 액션으로 표시되는 Dialog 스타일 Activity
 */
class WidgetActionVisibleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                StorageDetailContent(
                    context = this,
                    onDismiss = { finish() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // When returning from settings, recreate to check permission again
        // This will be handled by the LaunchedEffect in StorageDetailContent
    }
}

@Composable
private fun StorageDetailContent(context: ComponentActivity, onDismiss: () -> Unit) {
    var storageInfo by remember { mutableStateOf<StorageDetailInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var needsPermission by remember { mutableStateOf(false) }

    // Try to collect storage info - will detect permission issue during collection
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val collector = StorageCollector()
                val info = collector.collectDetailed(context)

                // If no apps were found and we have queryable packages, likely permission issue
                if (info.appStorageList.isEmpty() || info.appStorageList.size == 1) {
                    // Check if permission might be needed by trying to query a known app
                    val hasPermission = checkUsageStatsPermission(context)
                    if (!hasPermission) {
                        needsPermission = true
                        isLoading = false
                        return@withContext
                    }
                }

                storageInfo = info
                isLoading = false
            } catch (e: SecurityException) {
                if (e.message?.contains("PACKAGE_USAGE_STATS") == true) {
                    needsPermission = true
                    isLoading = false
                } else {
                    errorMessage = e.message ?: "스토리지 정보를 가져오는 중 오류가 발생했습니다."
                    isLoading = false
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "스토리지 정보를 가져오는 중 오류가 발생했습니다."
                isLoading = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header
                Text(
                    text = "Storage",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                when {
                    needsPermission -> {
                        PermissionRequiredContent(
                            onRequestPermission = {
                                openUsageAccessSettings(context)
                            },
                            onDismiss = onDismiss
                        )
                    }
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "스토리지 정보를 불러오는 중...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                    errorMessage != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "오류",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onDismiss) {
                                Text("닫기")
                            }
                        }
                    }
                    storageInfo != null -> {
                        StorageInfoContent(
                            storageInfo = storageInfo!!,
                            onDismiss = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StorageInfoContent(storageInfo: StorageDetailInfo, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Basic Storage Info
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Usage Percentage
            Text(
                text = "사용률: ${String.format("%.1f", storageInfo.usagePercent)}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = storageInfo.usagePercent / 100f,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Storage Details
            StorageInfoRow("총 용량", "${storageInfo.totalStorageGb} GB")
            Spacer(modifier = Modifier.height(8.dp))
            StorageInfoRow("사용 중", "${storageInfo.usedStorageGb} GB")
            Spacer(modifier = Modifier.height(8.dp))
            StorageInfoRow("사용 가능", "${storageInfo.freeStorageGb} GB")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Storage Breakdown - 사용 중 상세 정보
        Text(
            text = "사용 중 상세 내역",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 앱 관련 총합
                StorageBreakdownRow(
                    label = "앱 관련",
                    value = "${storageInfo.storageBreakdown.totalAppStorageGb} GB",
                    isTotal = true
                )

                // 앱 크기
                StorageBreakdownRow(
                    label = "  • 앱 크기",
                    value = "${storageInfo.storageBreakdown.appSizeGb} GB",
                    indent = true
                )

                // 앱 데이터
                StorageBreakdownRow(
                    label = "  • 앱 데이터",
                    value = "${storageInfo.storageBreakdown.appDataGb} GB",
                    indent = true
                )

                // 앱 캐시
                StorageBreakdownRow(
                    label = "  • 앱 캐시",
                    value = "${storageInfo.storageBreakdown.appCacheGb} GB",
                    indent = true
                )

                // 구분선
                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // 기타 (시스템, 미디어 등)
                StorageBreakdownRow(
                    label = "기타",
                    value = "${storageInfo.storageBreakdown.otherStorageGb} GB",
                    description = "시스템 파일, 미디어, 다운로드 등"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Storage List
        Text(
            text = "앱별 스토리지 사용량",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (storageInfo.appStorageList.isEmpty()) {
            Text(
                text = "앱별 정보를 가져올 수 없습니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                storageInfo.appStorageList.forEach { appInfo ->
                    AppStorageItem(appInfo)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Close Button
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("닫기")
        }
    }
}

@Composable
private fun StorageInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StorageBreakdownRow(
    label: String,
    value: String,
    indent: Boolean = false,
    isTotal: Boolean = false,
    description: String? = null
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    style = if (isTotal) {
                        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    } else {
                        MaterialTheme.typography.bodyMedium
                    },
                    fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                style = if (isTotal) {
                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AppStorageItem(appInfo: com.widgetkit.core.component.devicecare.AppStorageInfo) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appInfo.appName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = appInfo.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${appInfo.storageGb} GB",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * PACKAGE_USAGE_STATS 권한 체크
 * StorageStatsManager를 사용하여 실제 권한 확인
 */
private fun checkUsageStatsPermission(context: Context): Boolean {
    return try {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
            ?: return false
        val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as? StorageStatsManager
            ?: return false

        val uuid = storageManager.getUuidForPath(Environment.getDataDirectory())
        val userHandle = Process.myUserHandle()

        // Try to query storage stats for current app
        // If permission is not granted, this will throw SecurityException
        storageStatsManager.queryStatsForPackage(
            uuid,
            context.packageName,
            userHandle
        )
        true
    } catch (e: SecurityException) {
        if (e.message?.contains("PACKAGE_USAGE_STATS") == true) {
            false
        } else {
            true // Other security exceptions might not be permission related
        }
    } catch (e: Exception) {
        true // Assume permission is granted if we can't check
    }
}

/**
 * Usage Access 설정 화면 열기
 */
private fun openUsageAccessSettings(context: ComponentActivity) {
    try {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to app settings
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e2: Exception) {
            // Ignore
        }
    }
}

@Composable
private fun PermissionRequiredContent(onRequestPermission: () -> Unit, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "권한 필요",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "앱별 스토리지 사용량을 확인하려면\n" +
                "사용 통계 접근 권한이 필요합니다.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("설정으로 이동")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("닫기")
        }
    }
}
