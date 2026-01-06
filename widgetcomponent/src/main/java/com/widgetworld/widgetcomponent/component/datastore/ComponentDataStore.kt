package com.widgetworld.widgetcomponent.component.datastore

import android.content.Context

/**
 * 위젯 컴포넌트의 상태 저장을 위한 DataStore 추상 클래스
 *
 * 각 컴포넌트는 이 클래스를 상속하여 자신만의 DataStore를 정의합니다.
 * DataStore 생성 및 관리 로직을 표준화하여 일관성을 제공합니다.
 *
 * 사용 예시:
 * ```kotlin
 * object BatteryDataStore : ComponentDataStore<BatteryData>() {
 *     override val datastoreName = "battery_info_pf"
 *
 *     override suspend fun saveData(context: Context, data: BatteryData) {
 *         getDataStore(context).updateData { preferences ->
 *             preferences.toMutablePreferences().apply {
 *                 this[levelKey] = data.level
 *                 this[chargingKey] = data.charging
 *             }
 *         }
 *     }
 *
 *     override suspend fun loadData(context: Context): BatteryData {
 *         val preferences = getDataStore(context).data.first()
 *         return BatteryData(
 *             level = preferences[levelKey] ?: 0f,
 *             charging = preferences[chargingKey] ?: false
 *         )
 *     }
 *
 *     override fun getDefaultData(): BatteryData {
 *         return BatteryData(level = 0f, charging = false)
 *     }
 *
 *     private val levelKey = floatPreferencesKey("battery_level")
 *     private val chargingKey = booleanPreferencesKey("battery_charging")
 * }
 * ```
 *
 * @param T 저장할 데이터 타입
 */
abstract class ComponentDataStore<T> {

    /**
     * DataStore의 이름
     * 각 컴포넌트마다 고유한 이름을 사용해야 합니다.
     */
    abstract val datastoreName: String

    /**
     * 서브클래스는 Context extension property로 DataStore를 정의해야 합니다.
     *
     * 예시:
     * ```
     * private val Context.dataStore by preferencesDataStore(name = datastoreName)
     * ```
     *
     * 그리고 saveData(), loadData()에서 context.dataStore를 직접 사용하세요.
     */

    /**
     * 데이터를 저장합니다.
     *
     * @param context Context
     * @param data 저장할 데이터
     */
    abstract suspend fun saveData(context: Context, data: T)

    /**
     * 저장된 데이터를 불러옵니다.
     *
     * @param context Context
     * @return 저장된 데이터, 없으면 기본값
     */
    abstract suspend fun loadData(context: Context): T

    /**
     * 기본 데이터를 반환합니다.
     * 저장된 데이터가 없거나 오류가 발생했을 때 사용됩니다.
     *
     * @return 기본 데이터
     */
    abstract fun getDefaultData(): T

    /**
     * 저장된 데이터를 모두 삭제합니다.
     *
     * @param context Context
     */
    open suspend fun clearData(context: Context) {
        // 기본 구현: 서브클래스에서 필요시 오버라이드
        saveData(context, getDefaultData())
    }
}
