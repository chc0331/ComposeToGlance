package com.example.widget.component.devicecare.ram

import com.example.widget.component.viewid.ViewIdType

sealed class RamViewIdType(override val typeName: String) : ViewIdType() {
    object Text : RamViewIdType("ram_text")
    object Progress : RamViewIdType("ram_progress")

    companion object {
        fun all(): List<RamViewIdType> = listOf(RamViewIdType.Text, RamViewIdType.Progress)
    }
}