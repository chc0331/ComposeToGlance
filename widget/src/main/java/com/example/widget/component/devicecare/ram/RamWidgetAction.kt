package com.example.widget.component.devicecare.ram

import android.app.ActivityManager
import android.content.Context
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
        val packageManager = context.packageManager

        // MATCH_UNINSTALLED_PACKAGES 등 플래그를 쓰지 않으면 기본 활성 앱만 가져옴
        val installedApps = packageManager.getInstalledPackages(0)
        for (appInfo in installedApps) {
            val pkgName = appInfo.packageName
            if (pkgName == context.packageName) continue

            // 3. 맹목적으로 종료 시도 (Blind Kill)
            // 실행 중이 아니면 시스템이 알아서 무시하므로 괜찮습니다.
            try {
                activityManager.killBackgroundProcesses(pkgName)
            } catch (e: Exception) {
                // 권한 부족 등으로 실패할 수 있음
            }
        }
    }
}