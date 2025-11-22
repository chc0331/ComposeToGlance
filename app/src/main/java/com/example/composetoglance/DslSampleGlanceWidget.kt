package com.example.composetoglance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import com.example.composetoglance.examples.createSimpleColumnLayoutDsl
import com.example.composetoglance.examples.renderWidgetLayout

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
        val layout = createSimpleColumnLayoutDsl()
        renderWidgetLayout(layout, context)
    }
}

class DslSampleGlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DslSampleGlanceWidget()
}
