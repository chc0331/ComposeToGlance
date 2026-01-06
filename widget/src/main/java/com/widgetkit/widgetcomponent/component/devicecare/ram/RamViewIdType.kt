package com.widgetkit.widgetcomponent.component.devicecare.ram

import com.widgetkit.widgetcomponent.component.viewid.ViewIdType

sealed class RamViewIdType(override val typeName: String) : ViewIdType() {
    object Text : RamViewIdType("ram_text")
    object Progress : RamViewIdType("ram_progress")

    object Animation : RamViewIdType("ram_animation")

    companion object {
        fun all(): List<RamViewIdType> = listOf(Text, Progress, Animation)
    }
}
