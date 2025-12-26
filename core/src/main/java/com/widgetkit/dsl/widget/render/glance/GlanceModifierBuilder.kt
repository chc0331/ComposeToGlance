package com.widgetkit.dsl.widget.render.glance

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.HeightModifier
import androidx.glance.layout.WidthModifier
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentHeight
import androidx.glance.layout.wrapContentWidth
import androidx.glance.unit.Dimension
import com.widgetkit.dsl.R
import com.widgetkit.dsl.proto.ViewProperty
import com.widgetkit.dsl.proto.WidgetMode
import com.widgetkit.dsl.widget.node.RenderContext
import com.widgetkit.dsl.widget.render.glance.converter.ActionConverter
import com.widgetkit.dsl.widget.render.glance.converter.ColorConverter
import com.widgetkit.dsl.widget.render.glance.converter.DimensionConverter
import com.widgetkit.dsl.widget.render.glance.converter.PaddingConverter

/**
 * Proto ViewProperty를 GlanceModifier로 변환하는 빌더
 */
internal object GlanceModifierBuilder {
    /**
     * ViewProperty를 GlanceModifier로 변환
     * @param viewProperty Proto ViewProperty
     * @param context RenderContext
     * @return GlanceModifier
     */
    @SuppressLint("RestrictedApi")
    fun buildModifier(viewProperty: ViewProperty, context: RenderContext): GlanceModifier {
        var modifier: GlanceModifier = GlanceModifier.Companion

        // Width
        val width = DimensionConverter.toGlanceDimension(viewProperty.width)
        modifier = if (width == Dimension.Fill) modifier.fillMaxWidth()
        else if (width == Dimension.Expand) modifier.then(WidthModifier(Dimension.Expand))
        else if (width is Dimension.Dp) modifier.width(width.dp)
        else modifier.wrapContentWidth() // Wrap

        // Height
        val height = DimensionConverter.toGlanceDimension(viewProperty.height)
        modifier = when {
            height == Dimension.Fill -> modifier.fillMaxHeight() // Glance는 fillMaxHeight가 없으므로 fillMaxSize 사용
            height == Dimension.Expand -> modifier.then(HeightModifier(Dimension.Expand))
            height is Dimension.Dp -> modifier.height(height.dp)
            else -> modifier.wrapContentHeight() // Wrap
        }

        // Padding
        if (viewProperty.hasPadding() && !PaddingConverter.isEmpty(viewProperty.padding)) {
            val padding = PaddingConverter.toGlancePaddingValues(viewProperty.padding)
            modifier = modifier.padding(
                start = padding.start,
                top = padding.top,
                end = padding.end,
                bottom = padding.bottom
            )
        }

        // Click Action - PREVIEW 모드에서는 설정하지 않음
        if (viewProperty.hasClickAction() && context.document.widgetMode != WidgetMode.WIDGET_MODE_PREVIEW) {
            val action = ActionConverter.toGlanceAction(viewProperty.clickAction, context.context)
            action?.let {
                modifier = modifier.clickable(it, rippleOverride = R.drawable.click_effect)
            }
        }

        // Background color
        modifier =
            modifier.background(ColorConverter.toGlanceColorProvider(viewProperty.backgroundColor))

        // Coner radius
        modifier = modifier.cornerRadius(viewProperty.cornerRadius.radius.dp)

        return modifier
    }
}