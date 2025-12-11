package com.example.widget.component.devicecare

import com.example.widget.component.viewid.ViewIdType

sealed class DeviceCareViewIdType(override val typeName: String) : ViewIdType() {
    object RamUsageText : DeviceCareViewIdType("ram_usage_text")
    object RamUsageProgress : DeviceCareViewIdType("ram_usage_progress")
    object StorageUsageText : DeviceCareViewIdType("storage_usage_text")
    object StorageUsageProgress : DeviceCareViewIdType("storage_usage_progress")

    companion object {
        fun all(): List<DeviceCareViewIdType> = listOf(
            RamUsageText,
            RamUsageProgress,
            StorageUsageText,
            StorageUsageProgress
        )
    }
}


