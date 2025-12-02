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
import com.example.dsl.glance.renderer.RendererInitializer
import com.example.widget.repository.WidgetLayoutRepository

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.w("MainActivity", "Notification permission denied")
        }
        // Request Bluetooth permissions after notification permission
        requestBluetoothPermissionsIfNeeded()
    }
    
    private val requestBluetoothPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Log.d("MainActivity", "Bluetooth permissions granted")
        } else {
            Log.w("MainActivity", "Some Bluetooth permissions denied")
        }
        // Start service regardless of permission result
        startWidgetForegroundServiceIfNeeded()
    }
    
    private lateinit var viewModel: WidgetEditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Renderer 초기화 (앱 시작 시 한 번만 실행)
        RendererInitializer.initialize()
        
        enableEdgeToEdge()

        // Android 13+ 알림 권한 확인 및 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                requestBluetoothPermissionsIfNeeded()
            } else {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            requestBluetoothPermissionsIfNeeded()
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
    
    private fun requestBluetoothPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissionsToRequest = mutableListOf<String>()
            
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            
            if (permissionsToRequest.isNotEmpty()) {
                requestBluetoothPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
            } else {
                startWidgetForegroundServiceIfNeeded()
            }
        } else {
            startWidgetForegroundServiceIfNeeded()
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
