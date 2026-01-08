package com.widgetworld.widget.editor.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.widgetworld.core.WidgetLayout
import com.widgetworld.core.proto.WidgetMode
import com.widgetworld.core.widget.WidgetRenderer
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalContext
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalPreview
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalProvider
import com.widgetworld.core.widget.widgetlocalprovider.WidgetLocalSize
import com.widgetworld.widget.editor.draganddrop.DragTarget
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.WidgetComponentRegistry
import com.widgetworld.widgetcomponent.component.WidgetComponent
import com.widgetworld.widgetcomponent.util.getSystemBackgroundRadius
import com.widgetworld.widgetcomponent.view.AppWidgetView

@Composable
fun WidgetComponentContainer(
    data: WidgetComponent,
    modifier: Modifier = Modifier,
    isClicked: Boolean = false,
    onComponentClick: () -> Unit = {},
    onAddClick: (WidgetComponent) -> Unit = {},
    onDragStart: () -> Unit = {}
) {
    val context = LocalContext.current
    val cornerRadius = context.getSystemBackgroundRadius()

    DragTarget(
        context = context,
        modifier = modifier.wrapContentSize(),
        dataToDrop = data,
        onComponentClick = onComponentClick,
        onDragStart = onDragStart,
        dragContent = { WidgetComponent(data) }
    ) {
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            WidgetComponent(data)
            if (isClicked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(cornerRadius))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable { onAddClick(data) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "추가",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
internal fun WidgetComponent(
    data: WidgetComponent,
    modifier: Modifier = Modifier,
    layout: LayoutType? = null
) {
    val key = remember(data.getWidgetTag(), data.getSizeType()) {
        "${data.getWidgetTag()}_${data.getSizeType()}"
    }
    val size = remember(key) { data.getSizeInDp(layout) }
    val context = LocalContext.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(size.width)
                .height(size.height)
                .padding(4.dp)
                .clip(RoundedCornerShape(context.getSystemBackgroundRadius()))
                .background(Color(0xFFF5F5F5)), // Default surface variant color
            contentAlignment = Alignment.Center
        ) {
            val component = remember(key) {
                WidgetComponentRegistry.getComponent(data.getWidgetTag())
            }
            if (component != null) {
                // DSL 컴포넌트를 AppWidgetView로 렌더링
                // renderer를 remember로 캐싱하여 재렌더링 방지
                val renderer = remember(key) { WidgetRenderer(context) }
                // layout을 미리 생성하여 캐싱 (깜박임 방지)
                val layout = remember(key) {
                    WidgetLayout(mode = WidgetMode.WIDGET_MODE_PREVIEW) {
                        WidgetLocalProvider(
                            WidgetLocalPreview provides true,
                            WidgetLocalSize provides size,
                            WidgetLocalContext provides context
                        ) {
                            // 현재 scope에서 Content를 호출하여 locals에 접근 가능하도록 함
                            // this는 WidgetLocalProvider가 생성한 childScope를 가리킴
                            // Content()가 생성한 children은 WidgetLocalProvider가 자동으로 수집함
                            component.renderContent(this)
                        }
                    }
                }

                AppWidgetView(
                    size = size,
                    layout = layout,
                    renderer = renderer
                )
            } else {
                // 컴포넌트를 찾을 수 없을 때 기본 텍스트 표시
                DefaultWidgetContent(data)
            }
        }
    }
}

@Composable
private fun DefaultWidgetContent(data: WidgetComponent) {
    // 기본 색상 사용
    val titleColor = Color(0xFF616161) // onSurfaceVariant
    val bodyColor = Color(0xFF616161).copy(alpha = 0.7f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = data.getName(),
            color = titleColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = data.getSizeType().toString(),
            color = bodyColor
        )
    }
}
