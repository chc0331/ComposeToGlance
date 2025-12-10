# Widget Component 생성 가이드

이 문서는 새로운 위젯 컴포넌트를 생성하고 등록하는 방법을 설명합니다.

# Widget Component Architecture

**Architecture Style**: Pluggable Component Architecture with Layered Responsibilities

**Core Design Patterns**:
- Component-Based Architecture (주 패턴)
- Template Method Pattern
- Strategy Pattern
- Registry Pattern
- Repository Pattern

**Architecture Principles**:
- Separation of Concerns
- Dependency Injection
- Plugin-based Extensibility
- Partial Update Optimization

## 목차

1. [아키텍처 개요](#아키텍처-개요)
2. [파일 구조](#파일-구조)
3. [컴포넌트 유형별 가이드](#컴포넌트-유형별-가이드)
4. [단계별 생성 가이드](#단계별-생성-가이드)
5. [참고 예제](#참고-예제)

---

## 아키텍처 개요

각 위젯 컴포넌트는 다음 요소들로 구성됩니다:

```
┌─────────────────────────────────────────┐
│         WidgetComponent                 │
│  (추상 클래스 - 모든 컴포넌트의 베이스)   │
└─────────────────────────────────────────┘
                    ▲
                    │ extends
                    │
┌───────────────────┴──────────────────────┐
│                                          │
│  1. GUI Definition (필수)                 │
│     - WidgetScope.Content()              │
│                                          │
│  2. View IDs (partially update 필요시)   │
│     - ViewIdType sealed class            │
│     - getViewIdTypes()                   │
│                                          │
│  3. Update Logic (업데이트 필요시)         │
│     - ComponentUpdateManager             │
│     - getUpdateManager()                 │
│                                          │
│  4. Data Store (상태 저장 필요시)          │
│     - ComponentDataStore                 │
│     - getDataStore()                     │
│                                          │
│  5. Lifecycle (외부 리소스 사용시)         │
│     - ComponentLifecycle                 │
│     - getLifecycle()                     │
│                                          │
└──────────────────────────────────────────┘
```

### 핵심 인터페이스 및 클래스

- **WidgetComponent**: 모든 컴포넌트의 베이스 클래스
- **ComponentUpdateManager<T>**: 업데이트 로직 인터페이스
- **ComponentDataStore<T>**: 상태 저장 추상 클래스
- **ComponentLifecycle**: 생명주기 관리 인터페이스
- **ViewIdProvider**: View ID 생성 인터페이스

---

## 파일 구조

각 컴포넌트는 독립적인 패키지에 다음 구조를 따릅니다:

```
widget/component/
└── {componentName}/
    ├── {ComponentName}Widget.kt          # GUI 정의 (필수)
    ├── {ComponentName}ViewIdType.kt      # View ID 타입 (partially update 필요시)
    ├── {ComponentName}UpdateManager.kt   # 업데이트 로직 (업데이트 필요시)
    ├── {ComponentName}DataStore.kt       # DataStore 관리 (상태 필요시)
    ├── {ComponentName}Data.kt            # 데이터 모델 (필요시)
    └── {ComponentName}Lifecycle.kt       # Receiver/Worker (필요시)
```

### 실제 예시: Battery 컴포넌트

```
widget/component/battery/
├── BatteryWidget.kt              # GUI 정의
├── BatteryViewIdType.kt          # View ID 타입
├── BatteryUpdateManager.kt       # 업데이트 로직
├── BatteryDataStore.kt           # DataStore
├── BatteryData.kt                # 데이터 모델
├── BatteryLifecycle.kt           # BroadcastReceiver 관리
├── BatteryStatusReceiver.kt      # BroadcastReceiver 구현
└── DeviceType.kt                 # 공통 Enum
```

---

## 컴포넌트 유형별 가이드

### 1. 정적 컴포넌트 (Static Component)

업데이트가 필요 없는 단순 UI 컴포넌트 (예: Text, Image, Spacer)

**필요한 파일:**
- `XxxWidget.kt` (필수)

**예시:**

```kotlin
// TextComponent.kt
class TextComponent : WidgetComponent() {
    override fun getName() = "Text"
    override fun getDescription() = "Text Display"
    override fun getWidgetCategory() = WidgetCategory.BASIC
    override fun getSizeType() = SizeType.TINY
    override fun getWidgetTag() = "Text"
    
    override fun WidgetScope.Content() {
        Text(
            modifier = WidgetModifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentProperty = {
                TextContent { text = "Hello" }
            }
        )
    }
    
    // 업데이트 불필요
    override fun getUpdateManager() = null
}
```

---

### 2. 실시간 업데이트 컴포넌트 (Real-time Update Component)

BroadcastReceiver를 사용하여 실시간으로 업데이트되는 컴포넌트 (예: Battery, Time)

⚠️ **중요**: BroadcastReceiver는 `WidgetForegroundService`에서 관리됩니다!

**필요한 파일:**
- `XxxWidget.kt` (필수)
- `XxxViewIdType.kt` (partially update 용)
- `XxxUpdateManager.kt`
- `XxxDataStore.kt`
- `XxxData.kt`
- `XxxReceiver.kt` (WidgetForegroundService에 등록)

**주의**: `XxxLifecycle.kt`는 생성하지 않습니다 (Foreground Service에서 관리)

**예시:**

```kotlin
// 1. Data Model (BatteryData.kt)
data class BatteryData(
    val level: Float,
    val charging: Boolean
)

// 2. DataStore (BatteryDataStore.kt)
object BatteryComponentDataStore : ComponentDataStore<BatteryData>() {
    override val datastoreName = "battery_info_pf"
    
    private val Context.dataStore by preferencesDataStore(name = datastoreName)
    
    override suspend fun saveData(context: Context, data: BatteryData) {
        context.dataStore.updateData { preferences ->
            preferences.toMutablePreferences().apply {
                this[levelKey] = data.level
                this[chargingKey] = data.charging
            }
        }
    }
    
    override suspend fun loadData(context: Context): BatteryData {
        val preferences = context.dataStore.data.first()
        return BatteryData(
            level = preferences[levelKey] ?: 0f,
            charging = preferences[chargingKey] ?: false
        )
    }
    
    override fun getDefaultData() = BatteryData(0f, false)
    
    private val levelKey = floatPreferencesKey("battery_level")
    private val chargingKey = booleanPreferencesKey("battery_charging")
}

// 3. View ID Types (BatteryViewIdType.kt)
sealed class BatteryViewIdType(override val typeName: String) : ViewIdType() {
    object Text : BatteryViewIdType("battery_text")
    object Progress : BatteryViewIdType("battery_progress")
    
    companion object {
        fun all(): List<BatteryViewIdType> = listOf(Text, Progress)
    }
}

// 4. Receiver는 WidgetForegroundService에 등록
// BatteryStatusReceiver.kt는 이미 존재하며, 
// WidgetForegroundService.registerBatteryReceiver()에서 관리됨

// 5. Update Manager (BatteryUpdateManager.kt)
object BatteryUpdateManager : ComponentUpdateManager<BatteryData> {
    override val widget: BatteryWidget get() = BatteryWidget()
    
    override suspend fun updateComponent(context: Context, data: BatteryData) {
        // 1. DataStore에 저장
        BatteryComponentDataStore.saveData(context, data)
        
        // 2. 배치된 모든 위젯 찾기
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                // 3. GlanceAppWidgetState 업데이트
                updateWidgetState(context, widgetId, data)
                
                // 4. RemoteViews로 부분 업데이트
                val gridIndex = component.gridIndex
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                
                remoteViews.setProgressBar(
                    widget.getBatteryProgressId(gridIndex),
                    100,
                    data.level.toInt(),
                    false
                )
                remoteViews.setTextViewText(
                    widget.getBatteryTextId(gridIndex),
                    "${data.level.toInt()}%"
                )
                
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
    }
    
    private suspend fun updateWidgetState(
        context: Context,
        widgetId: Int,
        data: BatteryData
    ) {
        val glanceManager = GlanceAppWidgetManager(context)
        val glanceId = glanceManager.getGlanceIdBy(widgetId)
        updateAppWidgetState(context, glanceId) { pref ->
            pref[levelKey] = data.level
            pref[chargingKey] = data.charging
        }
    }
}

// 6. Widget (BatteryWidget.kt)
class BatteryWidget : WidgetComponent() {
    override fun getName() = "Battery"
    override fun getDescription() = "Battery Status"
    override fun getWidgetCategory() = WidgetCategory.DEVICE_INFO
    override fun getSizeType() = SizeType.TINY
    override fun getWidgetTag() = "Battery"
    
    override fun WidgetScope.Content() {
        val state = getLocal(WidgetLocalState)
        val level = state?.get(levelKey) ?: 0f
        
        Column {
            Progress(
                modifier = WidgetModifier
                    .viewId(getBatteryProgressId(gridIndex))
                    .partiallyUpdate(true),
                contentProperty = {
                    progressValue = level
                    maxValue = 100f
                }
            )
            Text(
                modifier = WidgetModifier
                    .viewId(getBatteryTextId(gridIndex))
                    .partiallyUpdate(true),
                contentProperty = {
                    TextContent { text = "${level.toInt()}%" }
                }
            )
        }
    }
    
    // View ID Provider
    override fun getViewIdTypes() = BatteryViewIdType.all()
    
    fun getBatteryProgressId(gridIndex: Int) =
        generateViewId(BatteryViewIdType.Progress, gridIndex)
    
    fun getBatteryTextId(gridIndex: Int) =
        generateViewId(BatteryViewIdType.Text, gridIndex)
    
    // 새 아키텍처
    override fun getUpdateManager() = BatteryUpdateManager
    override fun getDataStore() = BatteryComponentDataStore
    
    // BroadcastReceiver는 WidgetForegroundService에서 관리
    override fun getLifecycle() = null
    override fun requiresAutoLifecycle() = false
}
```

---

### 3. 주기적 업데이트 컴포넌트 (Periodic Update Component)

WorkManager를 사용하여 주기적으로 업데이트되는 컴포넌트 (예: DeviceCare, Weather)

**필요한 파일:**
- `XxxWidget.kt` (필수)
- `XxxViewIdType.kt` (필요시)
- `XxxUpdateManager.kt`
- `XxxDataStore.kt`
- `XxxData.kt`
- `XxxLifecycle.kt`
- `XxxWorker.kt`

**예시:**

```kotlin
// 1. Data Model (DeviceCareData.kt)
data class DeviceState(
    val memoryUsageRatio: Float,
    val storageUsageRatio: Float,
    val cpuLoad: Float,
    val temperatureCelsius: Float
)

// 2. DataStore (DeviceCareDataStore.kt)
object DeviceCareComponentDataStore : ComponentDataStore<DeviceState>() {
    override val datastoreName = "device_care_pf"
    
    private val Context.dataStore by preferencesDataStore(name = datastoreName)
    
    override suspend fun saveData(context: Context, data: DeviceState) {
        context.dataStore.edit { preferences ->
            preferences[memoryKey] = data.memoryUsageRatio
            preferences[storageKey] = data.storageUsageRatio
            preferences[cpuKey] = data.cpuLoad
            preferences[tempKey] = data.temperatureCelsius
        }
    }
    
    override suspend fun loadData(context: Context): DeviceState {
        val preferences = context.dataStore.data.first()
        return DeviceState(
            memoryUsageRatio = preferences[memoryKey] ?: 0f,
            storageUsageRatio = preferences[storageKey] ?: 0f,
            cpuLoad = preferences[cpuKey] ?: 0f,
            temperatureCelsius = preferences[tempKey] ?: 0f
        )
    }
    
    override fun getDefaultData() = DeviceState(0f, 0f, 0f, 0f)
    
    private val memoryKey = floatPreferencesKey("memory_usage")
    private val storageKey = floatPreferencesKey("storage_usage")
    private val cpuKey = floatPreferencesKey("cpu_load")
    private val tempKey = floatPreferencesKey("temperature")
}

// 3. Lifecycle (DeviceCareLifecycle.kt)
object DeviceCareLifecycle : ComponentLifecycle {
    private const val WORK_NAME = "device_care_worker"
    private var registered = false
    
    override fun register(context: Context) {
        if (registered) return
        
        val workRequest = PeriodicWorkRequestBuilder<DeviceCareWorker>(
            15, TimeUnit.MINUTES
        ).build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
        
        registered = true
    }
    
    override fun unregister(context: Context) {
        if (!registered) return
        
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        registered = false
    }
    
    override fun isRegistered() = registered
}

// 4. Worker (DeviceCareWorker.kt)
class DeviceCareWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // 디바이스 상태 수집
            val deviceState = DeviceStateCollector.collect(applicationContext)
            
            // DataStore에 저장
            DeviceCareComponentDataStore.saveData(applicationContext, deviceState)
            
            // 위젯 업데이트
            DeviceCareUpdateManager.updateComponent(applicationContext, deviceState)
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

// 5. Update Manager (DeviceCareUpdateManager.kt)
object DeviceCareUpdateManager : ComponentUpdateManager<DeviceState> {
    override val widget get() = DeviceCareWidget()
    
    override suspend fun updateComponent(context: Context, data: DeviceState) {
        DeviceCareComponentDataStore.saveData(context, data)
        
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                updateWidgetState(context, widgetId, data)
                // RemoteViews 업데이트 로직...
            }
    }
}

// 6. Widget (DeviceCareWidget.kt)
class DeviceCareWidget : WidgetComponent() {
    override fun getName() = "DeviceCare"
    override fun getWidgetTag() = "DeviceCare"
    override fun getSizeType() = SizeType.SMALL
    
    override fun WidgetScope.Content() {
        // GUI 구현...
    }
    
    override fun getUpdateManager() = DeviceCareUpdateManager
    override fun getDataStore() = DeviceCareComponentDataStore
    override fun getLifecycle() = DeviceCareLifecycle
    override fun requiresAutoLifecycle() = true
}
```

---

## 단계별 생성 가이드

### Step 1: 컴포넌트 패키지 생성

```
widget/src/main/java/com/example/widget/component/yourcomponent/
```

### Step 2: 데이터 모델 정의 (필요시)

```kotlin
// YourComponentData.kt
data class YourComponentData(
    val property1: String,
    val property2: Int
)
```

### Step 3: DataStore 구현 (상태 저장 필요시)

```kotlin
// YourComponentDataStore.kt
object YourComponentDataStore : ComponentDataStore<YourComponentData>() {
    override val datastoreName = "your_component_pf"
    
    private val Context.dataStore by preferencesDataStore(name = datastoreName)
    
    override suspend fun saveData(context: Context, data: YourComponentData) {
        // 구현
    }
    
    override suspend fun loadData(context: Context): YourComponentData {
        // 구현
    }
    
    override fun getDefaultData() = YourComponentData("", 0)
}
```

### Step 4: View ID 타입 정의 (partially update 필요시)

```kotlin
// YourComponentViewIdType.kt
sealed class YourComponentViewIdType(override val typeName: String) : ViewIdType() {
    object Element1 : YourComponentViewIdType("element1")
    object Element2 : YourComponentViewIdType("element2")
    
    companion object {
        fun all() = listOf(Element1, Element2)
    }
}
```

### Step 5: Lifecycle 구현 (외부 리소스 사용시)

```kotlin
// YourComponentLifecycle.kt
object YourComponentLifecycle : ComponentLifecycle {
    private var registered = false
    
    override fun register(context: Context) {
        // BroadcastReceiver 또는 WorkManager 등록
        registered = true
    }
    
    override fun unregister(context: Context) {
        // 리소스 해제
        registered = false
    }
    
    override fun isRegistered() = registered
}
```

### Step 6: Update Manager 구현 (업데이트 필요시)

```kotlin
// YourComponentUpdateManager.kt
object YourComponentUpdateManager : ComponentUpdateManager<YourComponentData> {
    override val widget get() = YourComponentWidget()
    
    override suspend fun updateComponent(context: Context, data: YourComponentData) {
        // 1. DataStore 저장
        YourComponentDataStore.saveData(context, data)
        
        // 2. 배치된 위젯 찾기 및 업데이트
        ComponentUpdateHelper.findPlacedComponents(context, widget.getWidgetTag())
            .forEach { (widgetId, component) ->
                // Glance State 업데이트
                updateWidgetState(context, widgetId, data)
                
                // RemoteViews로 부분 업데이트
                val remoteViews = ComponentUpdateHelper.createRemoteViews(context)
                // remoteViews 설정...
                ComponentUpdateHelper.partiallyUpdateWidget(context, widgetId, remoteViews)
            }
    }
}
```

### Step 7: Widget 클래스 구현

```kotlin
// YourComponentWidget.kt
class YourComponentWidget : WidgetComponent() {
    // 기본 정보
    override fun getName() = "YourComponent"
    override fun getDescription() = "Description"
    override fun getWidgetCategory() = WidgetCategory.CUSTOM
    override fun getSizeType() = SizeType.SMALL
    override fun getWidgetTag() = "YourComponent"
    
    // GUI 정의
    override fun WidgetScope.Content() {
        Box(modifier = WidgetModifier.fillMaxSize()) {
            // UI 구현...
        }
    }
    
    // View IDs (필요시)
    override fun getViewIdTypes() = YourComponentViewIdType.all()
    
    // 새 아키텍처
    override fun getUpdateManager() = YourComponentUpdateManager  // 또는 null
    override fun getDataStore() = YourComponentDataStore          // 또는 null
    override fun getLifecycle() = YourComponentLifecycle          // 또는 null
    override fun requiresAutoLifecycle() = true                   // 또는 false
}
```

### Step 8: 컴포넌트 등록

```kotlin
// WidgetComponentRegistry.kt
fun initializeWidgetComponents() {
    // ... 기존 컴포넌트들 ...
    
    WidgetComponentRegistry.registerComponent(YourComponentWidget())
}
```

### Step 9: Lifecycle 자동 초기화 (이미 구현됨)

```kotlin
// MainActivity.kt (이미 설정되어 있음)
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    initializeWidgetComponents()
    WidgetComponentRegistry.initializeLifecycles(applicationContext)
    // ...
}

override fun onDestroy() {
    super.onDestroy()
    WidgetComponentRegistry.shutdownLifecycles(applicationContext)
}
```

---

## 참고 예제

### 완성된 컴포넌트 예제

프로젝트에 이미 구현된 컴포넌트들을 참고하세요:

1. **Battery** (`component/battery/`)
   - 실시간 업데이트 (BroadcastReceiver)
   - DataStore 사용
   - Partially update
   - View ID 관리

2. **DeviceCare** (`component/devicecare/`)
   - 주기적 업데이트 (WorkManager)
   - DataStore 사용
   - 복잡한 상태 계산

3. **BluetoothBattery** (`component/battery/bluetooth/`)
   - 실시간 업데이트 (BroadcastReceiver)
   - 복수 기기 관리
   - 복합 DataStore

4. **Text** (`component/TextComponent.kt`)
   - 정적 컴포넌트
   - 최소 구현

---

## 체크리스트

새 컴포넌트를 만들 때 다음을 확인하세요:

- [ ] 패키지 구조가 올바른가? (`component/{name}/`)
- [ ] Widget 클래스 구현 완료
- [ ] getWidgetTag() 가 고유한 값을 반환하는가?
- [ ] 필요한 파일만 생성했는가?
- [ ] DataStore가 필요한 경우 구현했는가?
- [ ] Lifecycle이 필요한 경우 구현했는가?
- [ ] UpdateManager가 필요한 경우 구현했는가?
- [ ] View IDs가 필요한 경우 정의했는가?
- [ ] WidgetComponentRegistry에 등록했는가?
- [ ] requiresAutoLifecycle() 설정이 올바른가?

---

## 문제 해결

### Q: View ID 충돌이 발생합니다.

**A:** ViewIdAllocator가 자동으로 ID를 할당하므로 충돌이 발생하지 않아야 합니다. getWidgetTag()가 고유한지 확인하세요.

### Q: Lifecycle이 등록되지 않습니다.

**A:** 
1. getLifecycle()이 null이 아닌 ComponentLifecycle 객체를 반환하는지 확인
2. requiresAutoLifecycle()이 true인지 확인
3. MainActivity에서 initializeLifecycles()가 호출되는지 확인

### Q: DataStore 값이 저장/로드되지 않습니다.

**A:**
1. datastoreName이 고유한지 확인
2. Context.dataStore extension이 올바르게 정의되었는지 확인
3. Preferences keys가 충돌하지 않는지 확인

### Q: Partially update가 작동하지 않습니다.

**A:**
1. ViewIdType이 정의되었는지 확인
2. getViewIdTypes()가 올바른 리스트를 반환하는지 확인
3. Widget에서 .viewId()와 .partiallyUpdate(true) 모디파이어를 사용했는지 확인
4. UpdateManager에서 RemoteViews를 올바르게 설정했는지 확인

---

## 추가 리소스

- [WidgetComponent.kt](src/main/java/com/example/widget/component/WidgetComponent.kt)
- [ComponentLifecycle.kt](src/main/java/com/example/widget/component/lifecycle/ComponentLifecycle.kt)
- [ComponentDataStore.kt](src/main/java/com/example/widget/component/datastore/ComponentDataStore.kt)
- [ComponentUpdateManager.kt](src/main/java/com/example/widget/component/update/ComponentUpdateManager.kt)

---

## 결론

이 가이드를 따라 새로운 위젯 컴포넌트를 생성하면:

✅ **일관성**: 모든 컴포넌트가 동일한 구조를 따릅니다.
✅ **확장성**: 새 컴포넌트 추가가 명확하고 빠릅니다.
✅ **유지보수성**: 각 컴포넌트가 독립적이고 책임이 명확합니다.
✅ **안정성**: Lifecycle 자동 관리로 메모리 누수를 방지합니다.
✅ **테스트 용이성**: 각 레이어를 독립적으로 테스트할 수 있습니다.

문의사항이 있으면 프로젝트 관리자에게 연락하세요.

