package com.example.dsl.widget

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
import com.example.dsl.proto.ViewProperty
import com.example.dsl.widget.converter.ActionConverter
import com.example.dsl.widget.converter.ColorConverter
import com.example.dsl.widget.converter.DimensionConverter
import com.example.dsl.widget.converter.PaddingConverter

/**
 * Proto ViewProperty를 GlanceModifier로 변환하는 빌더
 */
internal object GlanceModifierBuilder {
    /**
     * ViewProperty를 GlanceModifier로 변환
     * @param viewProperty Proto ViewProperty
     * @param context Context
     * @return GlanceModifier
     */
    @SuppressLint("RestrictedApi")
    fun buildModifier(viewProperty: ViewProperty, context: Context): GlanceModifier {
        var modifier: GlanceModifier = GlanceModifier

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

        // Click Action
        if (viewProperty.hasClickAction()) {
            val action = ActionConverter.toGlanceAction(viewProperty.clickAction, context)
            action?.let {
                modifier = modifier.clickable(it)
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