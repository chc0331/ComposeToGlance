package com.widgetworld.widget

import android.Manifest
import android.app.AppOpsManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.AppOpsManagerCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.widgetworld.widget.editor.viewmodel.WidgetEditorViewModel
import com.widgetworld.widget.editor.viewmodel.WidgetEditorViewModelFactory
import com.widgetworld.widget.service.WidgetForegroundService
import com.widgetworld.core.widget.node.RendererInitializer
import com.widgetworld.widgetcomponent.repository.WidgetLayoutRepository
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.initializeWidgetComponents
import com.widgetworld.widgetcomponent.theme.AppTheme

class MainActivity : ComponentActivity() {

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.KILL_BACKGROUND_PROCESSES,
            Manifest.permission.PACKAGE_USAGE_STATS,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private fun isAllPermissionsGranted(): Boolean = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun isUsageAccessGranted(): Boolean {
        val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
        val mode = AppOpsManagerCompat.checkOrNoteProxyOp(
            this,
            applicationInfo.uid,
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            applicationInfo.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private val multiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            when {

            }
        }
    }

    private lateinit var viewModel: WidgetEditorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        RendererInitializer.initialize()
        initializeWidgetComponents()
        enableEdgeToEdge()
        if (!isAllPermissionsGranted()) {
            multiplePermissionsLauncher.launch(REQUIRED_PERMISSIONS)
        }
        if (!isUsageAccessGranted()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                setData(("package:" + packageName).toUri())
            }
            startActivity(intent)
        }
        startWidgetForegroundService()
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


    private fun startWidgetForegroundService() {
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
