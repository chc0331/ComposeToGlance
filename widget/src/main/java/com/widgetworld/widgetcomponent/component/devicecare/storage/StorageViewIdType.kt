package com.widgetworld.widgetcomponent.component.devicecare.storage

import com.widgetworld.widgetcomponent.component.viewid.ViewIdType

sealed class StorageViewIdType(override val typeName: String) : ViewIdType() {
    object Text : StorageViewIdType("storage_text")
    object Progress : StorageViewIdType("storage_progress")

    companion object {
        fun all(): List<StorageViewIdType> = listOf(Text, Progress)
    }
}
