package com.example.composetoglance.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import com.example.toolkit.glance.GlanceRenderer
import com.example.toolkit.proto.WidgetLayoutDocument

// Glance 위젯 코드 - 실제 홈화면 위젯에서 동작할 수 있음
class DslSampleGlanceWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Content()
        }
    }

    @Composable
    fun Content() {
        val context = LocalContext.current
        val layout = createMusicPlayerWidgetDsl()
        RenderWidgetLayout(layout, context)
    }

    /**
     * DSL로 생성한 레이아웃을 Glance 위젯으로 렌더링하는 예제
     */
    @Composable
    fun RenderWidgetLayout(
        document: WidgetLayoutDocument,
        context: Context
    ) {
        val renderer = GlanceRenderer(context)
        renderer.render(document)
    }
}

class DslSampleGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DslSampleGlanceWidget()
}
