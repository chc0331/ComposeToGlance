package com.widgetworld.widgetcomponent.component.devicecare.ram

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.glance.GlanceId
import com.widgetworld.core.widget.action.WidgetActionCallback
import com.widgetworld.core.widget.action.WidgetActionParameters
import com.widgetworld.widgetcomponent.component.devicecare.DeviceStateCollector
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class RamWidgetAction : WidgetActionCallback {

    companion object {
        const val TAG = "RamWidgetAction"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: WidgetActionParameters
    ) {
        coroutineScope {
            launch {
                updateRamMemory(context)
            }
            launch {
                //todo : 최적화를 하고나서 업데이트 타이밍은 언제하는게 좋을까
                launchSettingScreen(context)
            }
        }
    }

    private suspend fun updateRamMemory(context: Context) {
        val widget = RamWidget()
        val ramWidgetUpdateManager = widget.getUpdateManager() as RamUpdateManager
        clearMemory(context)
//        ramWidgetUpdateManager.showAnimation(context, true)
//        delay(2800)
//        ramWidgetUpdateManager.showAnimation(context, false)

        val deviceState = DeviceStateCollector.collect(context = context)
        Log.i(TAG, "Device state : $deviceState")
        val ramUsagePercent =
            String.format("%.1f", (deviceState.memoryUsage * 100) / deviceState.totalMemory)
                .toFloat()
        val ramData = RamData(ramUsagePercent)
        Log.i(TAG, "RamData : $ramData")
        ramWidgetUpdateManager.updateByPartially(context, null, ramData)
    }

    private suspend fun launchSettingScreen(context: Context) {
        var launched = false
        // 삼성 디바이스인 경우 디바이스 케어 앱 실행 시도
        if (isSamsungDevice()) {
            launched = tryLaunchSamsungDeviceCare(context)
            if (launched) return
        }

        // 삼성 앱 실행 실패 또는 삼성이 아닌 경우 일반 설정 화면 시도
        if (!launched) {
            launched = tryLaunchDeviceMaintenanceSettings(context)
            if (launched) return
        }

        // 모두 실패한 경우 일반 설정 화면으로 폴백
        if (!launched) {
            tryLaunchGeneralSettings(context)
        }
    }

    private fun clearMemory(context: Context) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // MATCH_UNINSTALLED_PACKAGES 등 플래그를 쓰지 않으면 기본 활성 앱만 가져옴
        val installedApps = getAllOptimizableApps(context)
        for (appInfo in installedApps) {
            if (appInfo == context.packageName) continue

            // 3. 맹목적으로 종료 시도 (Blind Kill)
            // 실행 중이 아니면 시스템이 알아서 무시하므로 괜찮습니다.
            try {
                activityManager.killBackgroundProcesses(appInfo)
            } catch (e: Exception) {
                // 권한 부족 등으로 실패할 수 있음
            }
        }
    }

    private fun getAllOptimizableApps(context: Context): Set<String> {
        val pm = context.packageManager
        val targetPackages = mutableSetOf<String>() // 중복 제거를 위해 Set 사용

        // 1. 런처 앱 검색
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        // 2. 브라우저 앱 검색
        val browserIntent = Intent(Intent.ACTION_VIEW).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            data = Uri.parse("https://www.google.com")
        }

        // 3. 공유 앱 검색
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*"
        }

        // 헬퍼 함수: 인텐트로 검색해서 패키지 이름만 추출
        fun addPackagesFromIntent(intent: Intent) {
            try {
                val list = pm.queryIntentActivities(intent, 0)
                for (info in list) {
                    targetPackages.add(info.activityInfo.packageName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 각각 검색 수행
        addPackagesFromIntent(launcherIntent)
        addPackagesFromIntent(browserIntent)
        addPackagesFromIntent(shareIntent)

        // 내 앱 제외
        targetPackages.remove(context.packageName)

        return targetPackages
    }

    private fun isSamsungDevice(): Boolean {
        return Build.MANUFACTURER.equals("samsung", ignoreCase = true)
    }

    private fun tryLaunchSamsungDeviceCare(context: Context): Boolean {
        return try {
            val intent = Intent().apply {
                setClassName(
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.score.ui.ScoreBoardActivity"
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Intent가 실행 가능한지 먼저 확인
            if (isIntentResolvable(context, intent)) {
                context.startActivity(intent)
                Log.i(TAG, "Samsung Device Care launched")
                true
            } else {
                Log.w(TAG, "Samsung Device Care app not found")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch Samsung Device Care", e)
            false
        }
    }

    private fun tryLaunchDeviceMaintenanceSettings(context: Context): Boolean {
        return try {
            val intent = Intent("android.settings.DEVICE_MAINTENANCE_SETTINGS").apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            if (isIntentResolvable(context, intent)) {
                context.startActivity(intent)
                Log.i(TAG, "Device maintenance settings launched")
                true
            } else {
                Log.w(TAG, "Device maintenance settings not available")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch device maintenance settings", e)
            false
        }
    }

    private fun tryLaunchGeneralSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Log.i(TAG, "General settings launched as fallback")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch general settings", e)
        }
    }

    private fun isIntentResolvable(context: Context, intent: Intent): Boolean {
        val packageManager = context.packageManager
        return packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null
    }
}
