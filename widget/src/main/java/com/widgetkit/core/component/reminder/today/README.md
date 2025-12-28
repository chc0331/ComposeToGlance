# TodayTodo Widget - 구현 완료

## 개요
오늘의 할 일을 관리하는 위젯입니다. Battery와 DeviceCare 위젯의 구현 패턴을 참고하여 새롭게 구현되었습니다.

## 구현된 기능

### 1. ✅ 하루 일정 관련 리마인더 위젯
- 오늘 날짜의 Todo 항목을 위젯에 표시
- 캘린더 아이콘과 "Today" 텍스트로 오늘 날짜 강조
- 최대 5개의 Todo 항목을 위젯에 표시

### 2. ✅ Todo 리스트 아이템 추가 (액티비티 팝업)
- 위젯 클릭 시 `TodoActivity` 팝업
- **간단 추가**: 인라인 입력 필드로 제목만 빠르게 추가
- **상세 추가**: 다이얼로그에서 제목, 설명, 날짜/시간 모두 설정 가능

### 3. ✅ 체크박스로 완료 체크 여부 표시
- CheckBox 컴포넌트 사용
- 완료된 항목은 회색 텍스트와 취소선으로 표시
- 클릭으로 상태 토글 가능

### 4. ✅ 날짜 선택 가능 (날짜 표시)
- **위젯**: "Dec 28" 형식으로 날짜 표시
- **액티비티**: 
  - 이전/다음 날짜 버튼으로 날짜 이동
  - 캘린더 아이콘 클릭으로 DatePicker 열기
  - 헤더에 선택된 날짜 표시 (오늘이면 "Today")

### 5. ✅ 완료한 Task 개수 표시
- 위젯 하단에 "X tasks • Y completed" 형식으로 표시
- 전체 개수와 완료된 개수를 함께 표시

## 아키텍처

### 파일 구조
```
reminder/today/
├── TodayTodoWidget.kt          # 메인 위젯 컴포넌트
├── TodayTodoData.kt            # 데이터 모델
├── TodayTodoDataStore.kt       # DataStore (개수, 날짜 저장)
├── TodayTodoUpdateManager.kt   # 위젯 업데이트 관리
├── TodayTodoViewIdType.kt      # ViewId 타입
├── TodoStatus.kt               # 상태 enum (INCOMPLETE, COMPLETED)
├── TodoRepository.kt           # Repository
├── TodoDateUtils.kt            # 날짜 유틸리티
├── ui/
│   ├── TodoActivity.kt         # 메인 액티비티
│   ├── TodoContent.kt          # UI 컨텐츠
│   ├── TodoItem.kt             # 개별 Todo 아이템
│   ├── TodoEditDialog.kt       # 추가/수정 다이얼로그
│   └── TodoDesignConstants.kt  # 디자인 상수
└── viewmodel/
    ├── TodayTodoViewModel.kt   # ViewModel
    └── TodayTodoViewModelFactory.kt
```

### 데이터베이스 (Room)
```
database/
├── TodoEntity.kt               # Todo 엔티티
├── TodoDao.kt                  # DAO
├── TodoDatabase.kt             # Database
└── TodoStatusTypeConverter.kt  # TypeConverter
```

## 데이터 플로우

1. **위젯 로드**: Room DB에서 오늘 날짜의 Todo 조회 → 위젯 표시
2. **위젯 클릭**: TodoActivity 열기
3. **Todo 추가/수정**: Repository → Room DB 저장 → UpdateManager로 위젯 갱신
4. **상태 토글**: Repository → Room DB 업데이트 → 위젯 갱신

## 주요 기능

### 위젯 (TodayTodoWidget)
- 오늘 날짜 Todo 표시 (최대 5개)
- 각 항목에 체크박스, 제목, 시간 표시
- 하단에 통계 정보 표시
- 클릭 시 TodoActivity 열기

### 액티비티 (TodoActivity)
- 날짜 네비게이션 (이전/다음/캘린더)
- 인라인 Todo 추가
- 상세 Todo 추가/수정 다이얼로그
- Todo 리스트 (완료 토글, 편집, 삭제)
- 저장 시 위젯 자동 업데이트

### Repository & Database
- Room을 사용한 로컬 데이터 저장
- Flow를 통한 반응형 데이터 관리
- TypeConverter로 TodoStatus enum 처리

## 등록

### WidgetComponentRegistry
- `initializeWidgetComponents()`에 등록됨 (line 32)

### AndroidManifest
- TodoActivity가 dialog 테마로 등록됨
- exported=false, launchMode=singleTop

## 사용 방법

1. 앱 빌드 및 설치
2. 홈 화면에서 위젯 추가
3. TodayTodo 위젯 선택
4. 위젯 클릭하여 Todo 추가/관리

