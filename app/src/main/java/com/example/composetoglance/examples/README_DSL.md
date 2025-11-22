# Widget DSL API 사용 가이드

## 개요

기존의 빌더 API는 유연하지만 코드가 장황합니다. 새로운 DSL API는 Compose 스타일의 선언적 문법을 제공하여 코드를 간결하고 읽기 쉽게 만듭니다.

## 코드 비교

### 예시: Column 안에 Text 두 개

#### 기존 빌더 API (약 50줄)
```kotlin
fun createSimpleColumnLayout(): WidgetLayoutDocument {
    val textNode1 = WidgetNode.newBuilder()
        .setText(
            textProperty(
                viewProperty = viewProperty(viewId = 1),
                text = textContent("Hello Glance!"),
                fontColor = colorProvider(color = color(0xFF000000.toInt())),
                fontSize = 18f,
                fontWeight = FONT_WEIGHT_BOLD
            )
        )
        .build()

    val textNode2 = WidgetNode.newBuilder()
        .setText(
            textProperty(
                viewProperty = viewProperty(
                    viewId = 2,
                    padding = padding(top = 8f)
                ),
                text = textContent("프로토 빌더 예시"),
                fontColor = colorProvider(color = color(0xFF666666.toInt())),
                fontSize = 14f,
                fontWeight = FONT_WEIGHT_NORMAL
            )
        )
        .build()

    val columnNode = WidgetNode.newBuilder()
        .setColumn(
            columnLayoutProperty(
                viewProperty = viewProperty(
                    viewId = 0,
                    width = matchParentDimension,
                    height = wrapContentDimension
                ),
                horizontalAlignment = H_ALIGN_CENTER
            )
        )
        .addChildren(textNode1)
        .addChildren(textNode2)
        .build()

    return WidgetLayoutDocument.newBuilder()
        .setRoot(columnNode)
        .build()
}
```

#### 새로운 DSL API (약 10줄)
```kotlin
fun createSimpleColumnLayoutDsl(): WidgetLayoutDocument {
    return widgetLayout {
        column(horizontalAlignment = H_ALIGN_CENTER) {
            text("Hello Glance!", fontSize = 18f, fontWeight = FONT_WEIGHT_BOLD)
            text("프로토 빌더 예시", fontSize = 14f, textColor = 0xFF666666.toInt(), padding = padding(top = 8f))
        }
    }
}
```

**코드량: 약 80% 감소**

## 주요 기능

### 1. 자동 viewId 관리
- 기존: 매번 `viewId`를 수동으로 지정
- DSL: 자동으로 고유한 `viewId` 생성

### 2. 간단한 색상 지정
- 기존: `colorProvider(color = color(0xFF000000.toInt()))`
- DSL: `textColor = 0xFF000000.toInt()`

### 3. 편리한 Padding
- 기존: `padding(start = 16f, top = 16f, end = 16f, bottom = 16f)`
- DSL: `padding(all = 16f)` 또는 `padding(horizontal = 16f, vertical = 8f)`

### 4. 선언적 구조
- 기존: 노드를 먼저 만들고 나중에 조합
- DSL: 중첩된 블록으로 자연스러운 구조 표현

## 사용 예시

### 기본 레이아웃
```kotlin
widgetLayout {
    column {
        text("Title", fontSize = 20f, fontWeight = FONT_WEIGHT_BOLD)
        text("Subtitle", fontSize = 14f)
    }
}
```

### 복잡한 레이아웃
```kotlin
widgetLayout {
    column(padding = padding(all = 16f)) {
        text("Title", fontSize = 24f, fontWeight = FONT_WEIGHT_BOLD)
        
        row(verticalAlignment = V_ALIGN_CENTER) {
            image(drawableResId = R.drawable.icon, width = dp(80f))
            column {
                text("Name", fontSize = 18f)
                text("Description", fontSize = 14f)
            }
        }
        
        button("Action", backgroundColor = 0xFF4CAF50.toInt())
    }
}
```

### Progress Bar
```kotlin
widgetLayout {
    column {
        text("Progress: 65%")
        progress(
            progressValue = 65f,
            maxValue = 100f,
            progressColor = 0xFF4CAF50.toInt()
        )
    }
}
```

## API 레퍼런스

### 레이아웃 함수

#### `column { }`
- `horizontalAlignment`: HorizontalAlignment (기본: H_ALIGN_START)
- `verticalAlignment`: VerticalAlignment (기본: V_ALIGN_TOP)
- `width`: Dimension (기본: matchParentDimension)
- `height`: Dimension (기본: wrapContentDimension)
- `padding`: Padding? (기본: null)

#### `row { }`
- `horizontalAlignment`: HorizontalAlignment (기본: H_ALIGN_START)
- `verticalAlignment`: VerticalAlignment (기본: V_ALIGN_TOP)
- `width`: Dimension (기본: matchParentDimension)
- `height`: Dimension (기본: wrapContentDimension)
- `padding`: Padding? (기본: null)

#### `box { }`
- `alignment`: AlignmentType (기본: ALIGNMENT_TYPE_START)
- `width`: Dimension (기본: matchParentDimension)
- `height`: Dimension (기본: wrapContentDimension)
- `padding`: Padding? (기본: null)

### 컴포넌트 함수

#### `text()`
- `text`: String (필수)
- `fontSize`: Float (기본: 14f)
- `fontWeight`: FontWeight (기본: FONT_WEIGHT_NORMAL)
- `textColor`: Int (기본: 0xFF000000)
- `textAlign`: TextAlign (기본: TEXT_ALIGN_START)
- `maxLine`: Int (기본: 1)
- `width`, `height`, `padding`: 선택적

#### `image()`
- `drawableResId`: Int? 또는 `uri`: String? (둘 중 하나 필수)
- `width`, `height`: Dimension
- `contentScale`: ContentScale (기본: CONTENT_SCALE_FIT)
- `tintColor`: Int? (기본: null)
- `alpha`: Float (기본: 1f)

#### `button()`
- `text`: String (필수)
- `fontSize`: Float (기본: 14f)
- `fontWeight`: FontWeight (기본: FONT_WEIGHT_MEDIUM)
- `textColor`: Int (기본: 0xFFFFFFFF)
- `backgroundColor`: Int (기본: 0xFF2196F3)
- `cornerRadius`: Float? (기본: null)

#### `progress()`
- `type`: ProgressType (기본: PROGRESS_TYPE_LINEAR)
- `maxValue`: Float (기본: 100f)
- `progressValue`: Float (기본: 0f)
- `progressColor`: Int (기본: 0xFF4CAF50)
- `backgroundColor`: Int (기본: 0xFFE0E0E0)

#### `spacer()`
- `width`: Dimension (기본: wrapContentDimension)
- `height`: Dimension (기본: wrapContentDimension)

### 편의 함수

#### `padding()`
```kotlin
padding(all = 16f)                    // 모든 방향
padding(horizontal = 16f)            // 좌우
padding(vertical = 8f)               // 상하
padding(start = 8f, top = 4f)       // 개별 지정
```

#### `dp()`
```kotlin
width = dp(200f)
height = dp(100f)
```

## 마이그레이션 가이드

### 기존 코드
```kotlin
val textNode = WidgetNode.newBuilder()
    .setText(
        textProperty(
            viewProperty = viewProperty(viewId = 1),
            text = textContent("Hello"),
            fontColor = colorProvider(color = color(0xFF000000.toInt())),
            fontSize = 16f
        )
    )
    .build()
```

### DSL 코드
```kotlin
text("Hello", fontSize = 16f, textColor = 0xFF000000.toInt())
```

## 장점

1. **코드 간결성**: 70-80% 코드 감소
2. **가독성**: Compose와 유사한 선언적 문법
3. **유지보수성**: 구조가 명확하고 수정이 쉬움
4. **타입 안전성**: Kotlin의 타입 시스템 활용
5. **자동화**: viewId 자동 관리, 기본값 제공

## 제한사항

1. **복잡한 커스터마이징**: 매우 복잡한 설정은 기존 빌더 API 사용 권장
2. **동적 생성**: 런타임에 동적으로 노드를 생성해야 하는 경우 기존 API가 더 적합할 수 있음

## 추가 예시

더 많은 예시는 `DslLayoutExamples.kt` 파일을 참고하세요.

