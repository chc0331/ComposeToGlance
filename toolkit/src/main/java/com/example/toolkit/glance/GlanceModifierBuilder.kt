package com.example.toolkit.glance

import android.content.Context
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.unit.Dimension
import com.example.composetoglance.proto.ViewProperty
import com.example.toolkit.glance.converter.ActionConverter
import com.example.toolkit.glance.converter.DimensionConverter
import com.example.toolkit.glance.converter.PaddingConverter

/**
 * Proto ViewProperty를 GlanceModifier로 변환하는 빌더
 */
object GlanceModifierBuilder {
    /**
     * ViewProperty를 GlanceModifier로 변환
     * @param viewProperty Proto ViewProperty
     * @param context Context
     * @return GlanceModifier
     */
    fun buildModifier(viewProperty: ViewProperty, context: Context): GlanceModifier {
        var modifier: GlanceModifier = GlanceModifier

        // Width
        val width = DimensionConverter.toGlanceDimension(viewProperty.width)
        modifier = if (width == Dimension.Expand) modifier.fillMaxWidth()
        else if (width is Dimension.Dp) modifier.width(width.dp)
        else modifier // Wrap

        // Height
        val height = DimensionConverter.toGlanceDimension(viewProperty.height)
        modifier = when {
            height == Dimension.Expand -> modifier.fillMaxSize() // Glance는 fillMaxHeight가 없으므로 fillMaxSize 사용
            height is Dimension.Dp -> modifier.height(height.dp)
            else -> modifier // Wrap
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

        return modifier
    }
}

