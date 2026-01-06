package com.widgetworld.widgetcomponent.component.reminder.upcoming.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.widgetworld.widgetcomponent.component.reminder.upcoming.UpcomingFilterType
import com.widgetworld.widgetcomponent.component.reminder.upcoming.UpcomingTasksDataStore
import com.widgetworld.widgetcomponent.component.reminder.upcoming.UpcomingTasksUpdateManager
import com.widgetworld.widgetcomponent.theme.AppTheme
import kotlinx.coroutines.launch

/**
 * Upcoming Tasks 필터 선택 Activity (Dialog 스타일)
 */
class UpcomingFilterActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 바깥쪽 터치 시 액티비티 종료
        setFinishOnTouchOutside(true)
        
        // Intent에서 widgetId 읽기 (없으면 -1)
        val widgetId = when {
            intent.hasExtra("WIDGET_ID") -> {
                when (val value = intent.extras?.get("WIDGET_ID")) {
                    is Int -> value
                    is Long -> value.toInt()
                    is String -> value.toIntOrNull() ?: -1
                    else -> -1
                }
            }
            else -> -1
        }
        android.util.Log.d("UpcomingFilterActivity", "Read widgetId from Intent: $widgetId")
        
        // 현재 필터 타입 로드
        lifecycleScope.launch {
            val currentFilterType = try {
                val loadWidgetId = if (widgetId != -1) widgetId else 0
                UpcomingTasksDataStore.loadFilterType(this@UpcomingFilterActivity, loadWidgetId)
            } catch (e: Exception) {
                android.util.Log.e("UpcomingFilterActivity", "Error loading filter type", e)
                UpcomingFilterType.TODAY
            }
            
            setContent {
                AppTheme {
                    UpcomingFilterContent(
                        currentFilterType = currentFilterType,
                        widgetId = widgetId,
                        onFilterSelected = { selectedFilterType ->
                            lifecycleScope.launch {
                                try {
                                    val updateWidgetId = if (widgetId != -1) widgetId else 0
                                    android.util.Log.d("UpcomingFilterActivity", "Filter selected: $selectedFilterType for widget $updateWidgetId")
                                    
                                    // 필터 타입 저장
                                    UpcomingTasksDataStore.saveFilterType(
                                        this@UpcomingFilterActivity,
                                        updateWidgetId,
                                        selectedFilterType
                                    )
                                    
                                    // 필터된 Todo 로드 및 위젯 업데이트
                                    val updatedData = UpcomingTasksUpdateManager.loadUpcomingTodos(
                                        this@UpcomingFilterActivity,
                                        selectedFilterType
                                    )
                                    UpcomingTasksUpdateManager.updateWidgetById(
                                        this@UpcomingFilterActivity,
                                        updateWidgetId,
                                        updatedData
                                    )
                                    
                                    android.util.Log.d("UpcomingFilterActivity", "Widget $updateWidgetId filter updated successfully")
                                    
                                    // 위젯 업데이트가 완료될 때까지 잠시 대기
                                    kotlinx.coroutines.delay(300)
                                } catch (e: Exception) {
                                    android.util.Log.e("UpcomingFilterActivity", "Error updating widget", e)
                                }
                                finish()
                            }
                        },
                        onDismiss = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}

