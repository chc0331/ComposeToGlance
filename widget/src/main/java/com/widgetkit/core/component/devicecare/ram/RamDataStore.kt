package com.widgetkit.core.component.devicecare.ram

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.widgetkit.core.component.datastore.ComponentDataStore
import kotlinx.coroutines.flow.first

data class RamData(
    val usagePercent: Float
)

internal object RamPreferenceKey {
    val UsagePercent = floatPreferencesKey("ram_usage_percent")
}

object RamWidgetDataStore : ComponentDataStore<RamData>() {

    override val datastoreName = "ram_pf"

    private val Context.ramDataStore: DataStore<Preferences> by preferencesDataStore(
        name = datastoreName
    )

    override suspend fun saveData(context: Context, data: RamData) {
        context.ramDataStore.edit { preferences ->
            preferences[RamPreferenceKey.UsagePercent] = data.usagePercent
        }
    }

    override suspend fun loadData(context: Context): RamData {
        val preferences = context.ramDataStore.data.first()
        return RamData(
            usagePercent = preferences[RamPreferenceKey.UsagePercent] ?: 0f
        )
    }

    override fun getDefaultData(): RamData {
        return RamData(usagePercent = 0f)
    }
}
