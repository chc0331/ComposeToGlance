package com.widgetworld.widgetcomponent.component.devicecare.datausage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 데이터 사용량 Limit 변경 다이얼로그 Activity
 */
class DataUsageLimitDialogActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val limitType = intent.getStringExtra(DataUsageLimitAction.PARAM_LIMIT_TYPE) ?: "wifi"
        val widgetId = intent.getIntExtra(DataUsageLimitAction.PARAM_WIDGET_ID, -1)
        
        if (widgetId == -1) {
            finish()
            return
        }
        
        setContent {
            MaterialTheme {
                Surface {
                    LimitDialogContent(
                        context = this,
                        limitType = limitType,
                        widgetId = widgetId,
                        onDismiss = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LimitDialogContent(
    context: Context,
    limitType: String,
    widgetId: Int,
    onDismiss: () -> Unit
) {
    val isWifi = limitType == "wifi"
    val title = if (isWifi) "Wi-Fi 데이터 제한 설정" else "모바일 데이터 제한 설정"
    val scope = rememberCoroutineScope()
    
    // 현재 limit 값 로드
    var currentLimitGb by remember { mutableStateOf(0f) }
    var inputText by remember { mutableStateOf("") }
    
    // 초기값 로드
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val currentData = DataUsageDataStore.loadData(context)
            withContext(Dispatchers.Main) {
                currentLimitGb = if (isWifi) currentData.wifiLimitGb else currentData.mobileLimitGb
                inputText = String.format("%.1f", currentLimitGb)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = inputText,
            onValueChange = { newValue ->
                // 숫자와 소수점만 허용
                if (newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    inputText = newValue
                }
            },
            label = { Text("제한량 (GB)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.width(100.dp)
            ) {
                Text("취소")
            }
            
            Button(
                onClick = {
                    val newLimitGb = inputText.toFloatOrNull() ?: currentLimitGb
                    if (newLimitGb > 0) {
                        val newLimitBytes = (newLimitGb * 1024 * 1024 * 1024).toLong()
                        
                        // DataStore에 저장하고 위젯 업데이트
                        scope.launch {
                            val currentData = withContext(Dispatchers.IO) {
                                DataUsageDataStore.loadData(context)
                            }
                            val updatedData = if (isWifi) {
                                DataUsageData.create(
                                    wifiUsageBytes = currentData.wifiUsageBytes,
                                    wifiLimitBytes = newLimitBytes,
                                    mobileUsageBytes = currentData.mobileUsageBytes,
                                    mobileLimitBytes = currentData.mobileLimitBytes
                                )
                            } else {
                                DataUsageData.create(
                                    wifiUsageBytes = currentData.wifiUsageBytes,
                                    wifiLimitBytes = currentData.wifiLimitBytes,
                                    mobileUsageBytes = currentData.mobileUsageBytes,
                                    mobileLimitBytes = newLimitBytes
                                )
                            }
                            
                            withContext(Dispatchers.IO) {
                                DataUsageDataStore.saveData(context, updatedData)
                                DataUsageUpdateManager.updateByState(context, widgetId, updatedData)
                            }
                            
                            onDismiss()
                        }
                    }
                },
                modifier = Modifier.width(100.dp)
            ) {
                Text("확인")
            }
        }
    }
}

