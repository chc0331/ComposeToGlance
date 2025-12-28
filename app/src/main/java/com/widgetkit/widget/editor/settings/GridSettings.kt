package com.widgetkit.widget.editor.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 그리드 설정 데이터 모델
 */
data class GridSettings(
    val globalMultiplier: Int = 1 // 전역 배수 (1, 2, 4, 6)
) {
    companion object {
        val VALID_MULTIPLIERS = listOf(1, 2, 4, 6)
        val DEFAULT = GridSettings()
        
        /**
         * 배수가 유효한지 확인
         */
        fun isValidMultiplier(multiplier: Int): Boolean {
            return multiplier in VALID_MULTIPLIERS
        }
    }
    
    /**
     * 전역 배수 설정
     */
    fun withGlobalMultiplier(multiplier: Int): GridSettings {
        return if (isValidMultiplier(multiplier)) {
            copy(globalMultiplier = multiplier)
        } else {
            this
        }
    }
}

/**
 * 그리드 설정 DataStore
 */
private val Context.gridSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "grid_settings"
)

object GridSettingsDataStore {
    private val GLOBAL_MULTIPLIER_KEY = intPreferencesKey("global_multiplier")
    
    /**
     * 설정 저장
     */
    suspend fun saveSettings(context: Context, settings: GridSettings) {
        context.gridSettingsDataStore.edit { preferences ->
            preferences[GLOBAL_MULTIPLIER_KEY] = settings.globalMultiplier
        }
    }
    
    /**
     * 설정 로드
     */
    suspend fun loadSettings(context: Context): GridSettings {
        val preferences = context.gridSettingsDataStore.data.first()
        val globalMultiplier = preferences[GLOBAL_MULTIPLIER_KEY] ?: 1
        
        return GridSettings(globalMultiplier)
    }
    
    /**
     * 설정 Flow
     */
    fun getSettingsFlow(context: Context): Flow<GridSettings> {
        return context.gridSettingsDataStore.data.map { preferences ->
            val globalMultiplier = preferences[GLOBAL_MULTIPLIER_KEY] ?: 1
            GridSettings(globalMultiplier)
        }
    }
    
    /**
     * 전역 배수만 업데이트
     */
    suspend fun updateGlobalMultiplier(context: Context, multiplier: Int) {
        if (GridSettings.isValidMultiplier(multiplier)) {
            context.gridSettingsDataStore.edit { preferences ->
                preferences[GLOBAL_MULTIPLIER_KEY] = multiplier
            }
        }
    }
}