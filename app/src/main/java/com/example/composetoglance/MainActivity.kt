package com.example.composetoglance

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.composetoglance.editor.viewmodel.WidgetEditorViewModel
import com.example.composetoglance.editor.viewmodel.WidgetEditorViewModelFactory
import com.example.composetoglance.service.WidgetForegroundService
import com.example.composetoglance.theme.ComposeToGlanceTheme
import com.example.widget.repository.WidgetLayoutRepository

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
            startWidgetForegroundServiceIfNeeded()
        } else {
            Log.w("MainActivity", "Notification permission denied")
            // 권한이 없어도 포그라운드 서비스는 시작할 수 있지만 알림이 표시되지 않을 수 있음
            startWidgetForegroundServiceIfNeeded()
        }
    }
    private lateinit var viewModel: WidgetEditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Android 13+ 알림 권한 확인 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startWidgetForegroundServiceIfNeeded()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startWidgetForegroundServiceIfNeeded()
        }
        viewModel =
            ViewModelProvider(
                this,
                WidgetEditorViewModelFactory(WidgetLayoutRepository(this))
            )[WidgetEditorViewModel::class.java]

        setContent {
            ComposeToGlanceTheme {
                MainContent(viewModel)
            }
        }
    }

    private fun startWidgetForegroundServiceIfNeeded() {
        try {
            val serviceClass = WidgetForegroundService::class.java
            val intent = Intent(this, serviceClass)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(this, intent)
            } else {
                startService(intent)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to start foreground service", e)
        }
    }
}
