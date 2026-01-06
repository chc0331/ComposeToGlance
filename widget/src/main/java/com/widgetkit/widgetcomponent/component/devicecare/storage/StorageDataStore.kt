package com.widgetkit.widgetcomponent.component.devicecare.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.widgetkit.widgetcomponent.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

data class StorageData(
    val usagePercent: Float
)

internal object StoragePreferenceKey {
    val UsagePercent = floatPreferencesKey("storage_usage_percent")
}

object StorageDataStore : ComponentDataStore<StorageData>() {

    override val datastoreName: String = "storage_pf"

    private val Context.storageDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )

    override suspend fun saveData(context: Context, data: StorageData) {
        context.storageDataStore.edit { preferences ->
            preferences[StoragePreferenceKey.UsagePercent] = data.usagePercent
        }
    }

    override suspend fun loadData(context: Context): StorageData {
        val preferences = context.storageDataStore.data.first()
        return StorageData(
            usagePercent = preferences[StoragePreferenceKey.UsagePercent] ?: 0f
        )
    }

    override fun getDefaultData(): StorageData {
        return StorageData(usagePercent = 0f)
    }
}
