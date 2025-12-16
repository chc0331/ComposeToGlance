package com.example.widget.component.devicecare.ram

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.glance.GlanceId
import com.example.dsl.action.WidgetActionCallback
import com.example.dsl.action.WidgetActionParameters
import com.example.widget.component.devicecare.DeviceStateCollector
import kotlinx.coroutines.delay

class RamWidgetAction : WidgetActionCallback {

    companion object {
        const val TAG = "RamWidgetAction"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: WidgetActionParameters
    ) {
        val widget = RamWidget()
        val ramWidgetUpdateManager = widget.getUpdateManager() as RamUpdateManager
        ramWidgetUpdateManager.showAnimation(context, true)
        clearMemory(context)
        delay(3000)
        ramWidgetUpdateManager.showAnimation(context, false)

        val deviceState = DeviceStateCollector.collect(context = context)
        Log.i(TAG, "Device state : $deviceState")
        val ramUsagePercent =
            String.format("%.1f", (deviceState.memoryUsage * 100) / deviceState.totalMemory)
                .toFloat()
        val ramData = RamData(ramUsagePercent)
        Log.i(TAG, "RamData : $ramData")
        ramWidgetUpdateManager.updateComponent(context, ramData)
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
}