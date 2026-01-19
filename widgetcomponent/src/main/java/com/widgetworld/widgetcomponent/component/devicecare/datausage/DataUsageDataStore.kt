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
    // Legacy keys (for backward compatibility)
    val DataLimitBytes = longPreferencesKey("data_limit_bytes")
    val CurrentUsageBytes = longPreferencesKey("current_usage_bytes")
    val UsagePercent = longPreferencesKey("usage_percent")

    // Wi-Fi keys
    val WifiLimitBytes = longPreferencesKey("wifi_limit_bytes")
    val WifiUsageBytes = longPreferencesKey("wifi_usage_bytes")

    // Mobile Data keys
    val MobileLimitBytes = longPreferencesKey("mobile_limit_bytes")
    val MobileUsageBytes = longPreferencesKey("mobile_usage_bytes")
}

object DataUsageDataStore : ComponentDataStore<DataUsageData>() {

    override val datastoreName = "data_usage_pf"

    private val Context.dataUsageDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )

    override suspend fun saveData(context: Context, data: DataUsageData) {
        context.dataUsageDataStore.edit { preferences ->
            // Wi-Fi fields
            preferences[DataUsagePreferenceKey.WifiLimitBytes] = data.wifiLimitBytes
            preferences[DataUsagePreferenceKey.WifiUsageBytes] = data.wifiUsageBytes
            // Mobile Data fields
            preferences[DataUsagePreferenceKey.MobileLimitBytes] = data.mobileLimitBytes
            preferences[DataUsagePreferenceKey.MobileUsageBytes] = data.mobileUsageBytes
        }
    }

    override suspend fun loadData(context: Context): DataUsageData {
        val preferences = context.dataUsageDataStore.data.first()

        // Load Wi-Fi data (with fallback to legacy or default)
        val wifiLimitBytes = preferences[DataUsagePreferenceKey.WifiLimitBytes]
            ?: preferences[DataUsagePreferenceKey.DataLimitBytes]
            ?: (DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024)
        val wifiUsageBytes = preferences[DataUsagePreferenceKey.WifiUsageBytes] ?: 0L

        // Load Mobile Data (with fallback to legacy or default)
        val mobileLimitBytes = preferences[DataUsagePreferenceKey.MobileLimitBytes]
            ?: preferences[DataUsagePreferenceKey.DataLimitBytes]
            ?: (DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024)
        val mobileUsageBytes = preferences[DataUsagePreferenceKey.MobileUsageBytes] ?: 0L

        return DataUsageData.create(
            wifiUsageBytes = wifiUsageBytes,
            wifiLimitBytes = wifiLimitBytes,
            mobileUsageBytes = mobileUsageBytes,
            mobileLimitBytes = mobileLimitBytes
        )
    }

    override fun getDefaultData(): DataUsageData {
        val defaultLimit = DataUsageData.DEFAULT_DATA_LIMIT_GB * 1024 * 1024 * 1024
        return DataUsageData.create(
            wifiUsageBytes = 0L,
            wifiLimitBytes = defaultLimit,
            mobileUsageBytes = 0L,
            mobileLimitBytes = defaultLimit
        )
    }
}

