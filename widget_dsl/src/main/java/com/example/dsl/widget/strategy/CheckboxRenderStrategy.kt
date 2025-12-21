package com.example.dsl.widget.strategy

import android.content.Context
import android.content.res.ColorStateList
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.layout.Box
import com.example.dsl.R
import com.example.dsl.proto.CheckboxProperty
import com.example.dsl.proto.ViewProperty
import com.example.dsl.proto.WidgetNode
import com.example.dsl.widget.RenderContext
import com.example.dsl.widget.WidgetRenderer
import com.example.dsl.widget.glance.GlanceModifierBuilder
import com.example.dsl.widget.remoteviews.RemoteViewsBuilder

/**
 * Checkbox 노드 렌더링 전략
 */
internal object CheckboxRenderStrategy {
    /**
     * Glance 전략
     */
    object Glance : GlanceRenderStrategy() {
        @Composable
        override fun renderGlance(
            node: WidgetNode,
            context: RenderContext,
            renderer: WidgetRenderer
        ) {
            if (!node.hasCheckbox()) {
                Box {}
                return
            }

            val checkboxProperty = node.checkbox
            val viewProperty = checkboxProperty.viewProperty

            // Modifier 생성
            val modifier = GlanceModifierBuilder.buildModifier(viewProperty, context.context)
                .then(context.modifier)

            // Glance의 Checkbox는 제한적이므로 RemoteViews를 사용
            val remoteViews = RemoteViews.renderCheckboxToRemoteViews(
                checkboxProperty,
                viewProperty,
                context.context
            )

            AndroidRemoteViews(
                modifier = modifier,
                remoteViews = remoteViews
            )
        }
    }

    /**
     * RemoteViews 전략
     */
    object RemoteViews : RemoteViewsRenderStrategy() {
        override fun createRemoteViews(
            node: WidgetNode,
            context: RenderContext
        ): android.widget.RemoteViews? {
            if (!node.hasCheckbox()) {
                return null
            }

            val checkboxProperty = node.checkbox
            val viewProperty = checkboxProperty.viewProperty

            return renderCheckboxToRemoteViews(
                checkboxProperty,
                viewProperty,
                context.context
            )
        }

        internal fun renderCheckboxToRemoteViews(
            checkboxProperty: CheckboxProperty,
            viewProperty: ViewProperty,
            context: Context
        ): android.widget.RemoteViews {
            val viewId = viewProperty.viewId
            // RemoteViews 생성 시 viewId를 전달하여 레이아웃의 CheckBox ID를 viewId로 설정
            val remoteViews = RemoteViews(
                context.packageName,
                R.layout.checkbox_component,
                viewId
            )

            // 체크 상태 설정
//            remoteViews.setChecked(viewId, checkboxProperty.checked)

            // 텍스트 설정
            val text = if (checkboxProperty.text.resId != 0) {
                context.getString(checkboxProperty.text.resId)
            } else {
                checkboxProperty.text.text
            }
            remoteViews.setTextViewText(viewId, text)

            // 색상 설정
            val checkedColor = if (checkboxProperty.checkedColor.resId != 0) {
                context.getColor(checkboxProperty.checkedColor.resId)
            } else {
                checkboxProperty.checkedColor.color.argb
            }
            val uncheckedColor = if (checkboxProperty.uncheckedColor.resId != 0) {
                context.getColor(checkboxProperty.uncheckedColor.resId)
            } else {
                checkboxProperty.uncheckedColor.color.argb
            }

            // 체크박스 색상 설정 (ButtonTintList 사용)
            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                ),
                intArrayOf(checkedColor, uncheckedColor)
            )
            remoteViews.setColorStateList(viewId, "setButtonTintList", colorStateList)

            // ViewProperty 속성 적용
            RemoteViewsBuilder.applyViewProperties(
                remoteViews,
                viewId,
                viewProperty,
                context
            )

            return remoteViews
        }
    }
}

