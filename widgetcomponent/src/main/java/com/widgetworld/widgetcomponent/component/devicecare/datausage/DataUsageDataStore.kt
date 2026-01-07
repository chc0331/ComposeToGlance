package com.widgetworld.widgetcomponent.component.devicecare.datausage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.widgetworld.widgetcomponent.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

internal object DataUsagePreferenceKey {
    val DataLimitBytes = longPreferencesKey("data_limit_bytes")
    val CurrentUsageBytes = longPreferencesKey("current_usage_bytes")
    val UsagePercent = longPreferencesKey("usage_percent")
}

object DataUsageDataStore : ComponentDataStore<DataUsageData>() {

    override val datastoreName = "data_usage_pf"

    private val Context.dataUsageDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )

    override suspend fun saveData(context: Context, data: DataUsageData) {
        context.dataUsageDataStore.edit { preferences ->
            preferences[DataUsagePreferenceKey.DataLimitBytes] = data.dataLimitBytes
            preferences[DataUsagePreferenceKey.CurrentUsageBytes] = data.currentUsageBytes
            preferences[DataUsagePreferenceKey.UsagePercent] = data.usagePercent.toLong()
        }
    }

    override suspend fun loadData(context: Context): DataUsageData {
        val preferences = context.dataUsageDataStore.data.first()
        val dataLimitBytes = preferences[DataUsagePreferenceKey.DataLimitBytes]
            ?: (DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024)
        val currentUsageBytes = preferences[DataUsagePreferenceKey.CurrentUsageBytes] ?: 0L
        
        return DataUsageData.create(
            currentUsageBytes = currentUsageBytes,
            dataLimitBytes = dataLimitBytes
        )
    }

    override fun getDefaultData(): DataUsageData {
        return DataUsageData.create(
            currentUsageBytes = 0L,
            dataLimitBytes = DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
        )
    }
}

