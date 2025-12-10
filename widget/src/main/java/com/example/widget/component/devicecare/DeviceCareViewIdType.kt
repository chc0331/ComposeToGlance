package com.example.widget.component.devicecare

import com.example.widget.component.viewid.ViewIdType

sealed class DeviceCareViewIdType(override val typeName: String) : ViewIdType() {
    object ScoreText : DeviceCareViewIdType("device_care_score_text")
    object MemoryProgress : DeviceCareViewIdType("device_care_memory_progress")
    object StorageProgress : DeviceCareViewIdType("device_care_storage_progress")
    object CpuProgress : DeviceCareViewIdType("device_care_cpu_progress")
    object TemperatureProgress : DeviceCareViewIdType("device_care_temperature_progress")

    companion object {
        fun all(): List<DeviceCareViewIdType> = listOf(
            ScoreText,
            MemoryProgress,
            StorageProgress,
            CpuProgress,
            TemperatureProgress
        )
    }
}

