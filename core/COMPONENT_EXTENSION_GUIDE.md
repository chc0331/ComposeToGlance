# 컴포넌트 확장 가이드

이 문서는 DSL -> Glance 매핑 시스템에 새로운 컴포넌트를 추가하는 방법을 설명합니다.

## 개요

새로운 컴포넌트를 추가하려면 다음 4단계를 거쳐야 합니다:

1. **Proto 정의**: `widget_dsl.proto`에 새 컴포넌트 타입 추가
2. **DSL 빌더**: DSL 빌더 클래스 및 최상위 함수 추가
3. **Renderer 구현**: `NodeRenderer` 인터페이스 구현
4. **등록**: `RendererInitializer`에 Renderer 등록

## 단계별 가이드

### Step 1: Proto 정의

`widget_dsl/src/main/proto/widget_dsl.proto` 파일을 수정합니다.

#### 1.1 WidgetNode에 새 필드 추가

```proto
message WidgetNode {
  oneof payload {
    // ... 기존 컴포넌트들
    BoxLayoutProperty box = 1;
    // ...
    NewComponentProperty new_component = 9;  // 새 필드 추가 (고유 번호 사용)
  }
  repeated WidgetNode children = 10;
}
```

**주의사항:**
- `oneof` 내의 필드 번호는 고유해야 합니다
- 기존 필드 번호와 충돌하지 않도록 확인

#### 1.2 새 컴포넌트 Property 메시지 정의

```proto
message NewComponentProperty {
  ViewProperty view_property = 1;  // 필수: 공통 속성
  // 컴포넌트별 속성들
  string custom_property = 2;
  int32 custom_number = 3;
}
```

**필수 사항:**
- `ViewProperty view_property = 1;` 필드는 반드시 포함해야 합니다
- 이는 모든 컴포넌트가 공통으로 가지는 속성입니다

### Step 2: DSL 빌더 클래스 생성

#### 2.1 Property DSL 빌더 생성 (선택사항)

복잡한 속성이 있는 경우 `builder/NewComponentPropertyDsl.kt` 파일을 생성합니다.

```kotlin
package com.widgetkit.core.proto

import com.example.dsl.proto.NewComponentProperty

class NewComponentPropertyDsl(
    private val builder: NewComponentProperty.Builder
) {
    fun viewProperty(block: ViewPropertyDsl.() -> Unit) {
        // ViewProperty 설정
    }

    var customProperty: String
        get() = builder.customProperty
        set(value) {
            builder.customProperty = value
        }

    fun build(): NewComponentProperty = builder.build()
}
```

#### 2.2 컴포넌트 DSL 클래스 생성

`component/ComponentDsl.kt` 파일에 새 DSL 클래스를 추가합니다.

```kotlin
class NewComponentDsl(
    private val scope: WidgetScope
) {
    private val propertyBuilder = NewComponentProperty.newBuilder()
    private val propertyDsl = NewComponentPropertyDsl(propertyBuilder)
    private var viewPropertySet = false

    fun ViewProperty(block: ViewPropertyDsl.() -> Unit) {
        viewPropertySet = true
        propertyDsl.viewProperty {
            if (viewId == 0) {
                viewId = scope.nextViewId()
            }
            block()
        }
    }

    var customProperty: String
        get() = propertyDsl.customProperty
        set(value) { propertyDsl.customProperty = value }

    internal fun build(): NewComponentProperty {
        if (!viewPropertySet) {
            propertyDsl.viewProperty {
                viewId = scope.nextViewId()
            }
        }
        return propertyBuilder.build()
    }
}
```

#### 2.3 최상위 DSL 함수 추가

`component/Components.kt` 파일에 최상위 함수를 추가합니다.

```kotlin
fun WidgetScope.NewComponent(block: NewComponentDsl.() -> Unit) {
    val dsl = NewComponentDsl(this)
    dsl.block()
    val node = WidgetNode.newBuilder()
        .setNewComponent(dsl.build())
        .build()
    addChild(node)
}
```

### Step 3: Renderer 구현

`glance/renderer/NewComponentRenderer.kt` 파일을 생성합니다.

```kotlin
package com.widgetkit.core.widget.renderer

import androidx.compose.runtime.Composable
import com.example.dsl.proto.WidgetNode
import com.example.dsl.glance.GlanceModifierBuilder
import com.example.dsl.glance.GlanceRenderer
import com.example.dsl.glance.RenderContext

/**
 * NewComponent 노드 렌더러
 */
object NewComponentRenderer : NodeRenderer {
    @Composable
    override fun render(
        node: WidgetNode,
        context: RenderContext,
        renderer: GlanceRenderer
    ) {
        if (!node.hasNewComponent()) {
            androidx.glance.layout.Box {}
            return
        }

        val componentProperty = node.newComponent
        val viewProperty = componentProperty.viewProperty

        // Modifier 생성
        val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
            .then(context.modifier)

        // 컴포넌트별 렌더링 로직
        // 예: Glance의 해당 컴포넌트로 렌더링
        androidx.glance.layout.Box(modifier = modifier) {
            // 렌더링 내용
        }
    }
}
```

**중요 사항:**
- `NodeRenderer` 인터페이스를 구현해야 합니다
- `hasNewComponent()` 메서드는 Proto에서 자동 생성됩니다
- `GlanceModifierBuilder`를 사용하여 공통 속성을 처리합니다

### Step 4: Renderer 등록

`glance/renderer/RendererInitializer.kt` 파일에 새 Renderer를 등록합니다.

```kotlin
object RendererInitializer {
    fun initialize() {
        // ... 기존 Renderer들
        
        // 새 컴포넌트 등록
        NodeRendererRegistry.register("new_component", NewComponentRenderer)
    }
    
    fun isInitialized(): Boolean {
        val requiredTypes = setOf(
            // ... 기존 타입들
            "new_component"  // 새 타입 추가
        )
        // ...
    }
}
```

**주의사항:**
- 타입 문자열은 `NodeRendererRegistry.getNodeType()`에서 사용하는 것과 일치해야 합니다
- `NodeRendererRegistry`는 자동으로 `hasNewComponent()`를 감지하여 "new_component" 타입으로 매핑합니다

## 완료 확인

새 컴포넌트가 올바르게 추가되었는지 확인:

1. **Proto 재생성**: Gradle 빌드를 실행하여 Proto 파일이 재생성되었는지 확인
2. **컴파일 확인**: 프로젝트가 오류 없이 컴파일되는지 확인
3. **초기화 확인**: `RendererInitializer.isInitialized()`가 `true`를 반환하는지 확인
4. **사용 테스트**: DSL에서 새 컴포넌트를 사용하여 정상 동작하는지 확인

## 예제: Card 컴포넌트 추가

전체 예제는 위 단계를 따라 Card 컴포넌트를 추가하는 것으로 이해할 수 있습니다.

## 확장성 개선 사항

현재 시스템은 `NodeRendererRegistry`를 사용하여 Renderer를 등록합니다. 이는 다음 장점이 있습니다:

- **OCP 준수**: 기존 코드 수정 없이 새 컴포넌트 추가 가능
- **명확한 등록**: 모든 Renderer가 한 곳에서 관리됨
- **타입 안전성**: 컴파일 타임에 타입 체크 가능

향후 개선 가능한 사항:
- Annotation 기반 자동 등록
- Plugin 시스템을 통한 외부 모듈 컴포넌트 추가

