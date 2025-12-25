package com.widgetkit.core.component.devicecare.ram

import com.widgetkit.core.component.viewid.ViewIdType

sealed class RamViewIdType(override val typeName: String) : ViewIdType() {
    object Text : RamViewIdType("ram_text")
    object Progress : RamViewIdType("ram_progress")

    object Animation : RamViewIdType("ram_animation")

    companion object {
        fun all(): List<RamViewIdType> = listOf(Text, Progress, Animation)
    }
}