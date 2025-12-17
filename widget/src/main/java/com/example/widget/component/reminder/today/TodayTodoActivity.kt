package com.example.widget.component.reminder.today

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.widget.theme.TodoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Today Todo를 표시하는 Dialog 스타일 Activity
 */
class TodayTodoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            TodoTheme {
                TodayTodoContent(
                    context = this,
                    onDismiss = { finish() }
                )
            }
        }
    }
}

/**
 * 오늘 날짜를 yyyy-MM-dd 형식으로 반환
 */
fun getTodayDateString(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(Date())
}

/**
 * yyyy-MM-dd 형식 문자열을 반환
 */
fun formatDateString(date: Date): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(date)
}

/**
 * 헤더용 로컬라이즈된 날짜 텍스트(예: 12월 17일 (수))
 */
fun formatHeaderDate(date: Date): String {
    val dateFormat = SimpleDateFormat("M월 d일 (E)", Locale.getDefault())
    return dateFormat.format(date)
}

