package com.widgetkit.widget

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
import com.widgetkit.widget.editor.viewmodel.WidgetEditorViewModel
import com.widgetkit.widget.editor.viewmodel.WidgetEditorViewModelFactory
import com.widgetkit.widget.service.WidgetForegroundService
import com.widgetkit.dsl.widget.node.RendererInitializer
import com.widgetkit.core.repository.WidgetLayoutRepository
import com.widgetkit.core.WidgetComponentRegistry
import com.widgetkit.core.initializeWidgetComponents
import com.widgetkit.core.theme.AppTheme

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
        
        // Widget 컴포넌트 초기화
        initializeWidgetComponents()
        
        // Widget 컴포넌트 Lifecycle 초기화
//        WidgetComponentRegistry.initializeLifecycles(applicationContext)
        
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
            AppTheme {
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
