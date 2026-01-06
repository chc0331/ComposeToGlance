package com.widgetkit.widgetcomponent.component.devicecare.storage

import com.widgetkit.widgetcomponent.component.viewid.ViewIdType

sealed class StorageViewIdType(override val typeName: String) : ViewIdType() {
    object Text : StorageViewIdType("storage_text")
    object Progress : StorageViewIdType("storage_progress")

    companion object {
        fun all(): List<StorageViewIdType> = listOf(Text, Progress)
    }
}
