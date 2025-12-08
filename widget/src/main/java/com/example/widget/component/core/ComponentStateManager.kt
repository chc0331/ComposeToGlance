package com.example.widget.component.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * 위젯 컴포넌트의 상태 관리를 위한 인터페이스
 *
 * 각 컴포넌트는 이 인터페이스를 구현하여 자신의 DataStore와 PreferenceKey를 관리합니다.
 * Repository 패턴을 통해 데이터 접근을 추상화합니다.
 */
interface ComponentStateManager {
    /**
     * 컴포넌트의 DataStore 인스턴스를 반환합니다.
     * @param context Context
     * @return DataStore<Preferences>
     */
    fun getDataStore(context: Context): DataStore<Preferences>

    /**
     * 컴포넌트의 PreferenceKey 이름을 반환합니다.
     * DataStore 파일 이름으로 사용됩니다.
     * @return PreferenceKey 이름 (예: "battery_info_pf")
     */
    fun getPreferenceKeyName(): String
}

