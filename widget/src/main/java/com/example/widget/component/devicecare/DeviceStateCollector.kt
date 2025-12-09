package com.example.widget.component.devicecare

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import java.io.RandomAccessFile


object DeviceStateCollector {

    fun collect(context: Context): DeviceState {
        val memRatio = MemoryCollector().collect(context)
        val storageRatio = StorageCollector().collect(context)
        val cpuLoad = CpuCollector().collect(context)
        val temp = TemperatureCollector().collect(context)

        return DeviceState(
            memoryUsageRatio = memRatio,
            storageUsageRatio = storageRatio,
            cpuLoad = cpuLoad,
            temperatureCelsius = temp
        )
    }
}

internal interface Collector {
    fun collect(context: Context): Float
}

class MemoryCollector : Collector {
    override fun collect(context: Context): Float {
        val am = context.getSystemService(ActivityManager::class.java)
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)

        val total = memInfo.totalMem.toFloat()
        val available = memInfo.availMem.toFloat()
        val used = total - available

        return (used / total).coerceIn(0f, 1f)   // 0.0 ~ 1.0
    }
}

class StorageCollector : Collector {

    override fun collect(context: Context): Float {
        val stat = StatFs(Environment.getDataDirectory().path)

        val total = stat.totalBytes.toFloat()
        val free = stat.availableBytes.toFloat()
        val used = total - free

        return (used / total).coerceIn(0f, 1f)  // 0.0 ~ 1.0
    }
}

class CpuCollector : Collector {

    private var prevIdle: Long = 0
    private var prevTotal: Long = 0

    /**
     * CPU Load 정규값 (0.0 ~ 1.0)
     *
     * 첫 호출 시 정확히 계산할 수 없기 때문에 0f 반환 → 두 번째 호출부터 정상 값 나옴.
     */
    override fun collect(context: Context): Float {
        val stat = readCpuStat() ?: return 0f
        val idle = stat.idle
        val total = stat.total

        val diffIdle = idle - prevIdle
        val diffTotal = total - prevTotal

        prevIdle = idle
        prevTotal = total

        if (diffTotal <= 0) return 0f

        val usage = 1f - (diffIdle.toFloat() / diffTotal.toFloat())
        return usage.coerceIn(0f, 1f)
    }

    private fun readCpuStat(): CpuStat? {
        return try {
            val reader = RandomAccessFile("/proc/stat", "r")
            val load = reader.readLine()
            reader.close()

            val toks = load.split(" ").filter { it.isNotBlank() }.drop(1) // "cpu" 제거

            val user = toks[0].toLong()
            val nice = toks[1].toLong()
            val system = toks[2].toLong()
            val idle = toks[3].toLong()
            val iowait = toks[4].toLong()
            val irq = toks[5].toLong()
            val softirq = toks[6].toLong()

            val total = user + nice + system + idle + iowait + irq + softirq

            CpuStat(idle = idle, total = total)
        } catch (e: Exception) {
            null
        }
    }

    data class CpuStat(
        val idle: Long, val total: Long
    )
}

class TemperatureCollector : Collector {

    /**
     * ThermalStatus 만을 사용하여 TemperatureScore 계산 (0~100)
     */
    override fun collect(context: Context): Float {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val status = pm.currentThermalStatus

        return when (status) {
            PowerManager.THERMAL_STATUS_NONE -> 100
            PowerManager.THERMAL_STATUS_LIGHT -> 85
            PowerManager.THERMAL_STATUS_MODERATE -> 70
            PowerManager.THERMAL_STATUS_SEVERE -> 45
            PowerManager.THERMAL_STATUS_CRITICAL -> 20
            PowerManager.THERMAL_STATUS_EMERGENCY -> 10
            PowerManager.THERMAL_STATUS_SHUTDOWN -> 0
            else -> 100 // fallback
        } / 10f
    }
}