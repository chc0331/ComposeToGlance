package com.example.composetoglance.editor.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.composetoglance.editor.layout.ClickableLayoutComponent
import com.example.composetoglance.editor.layout.Layout
import kotlinx.coroutines.launch

@Composable
fun LayoutsTabContent(onLayoutSelected: (Layout) -> Unit) {
    var activeLayout by remember { mutableStateOf<Layout?>(null) }
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyRow(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                top = BottomPanelConstants.TAB_PADDING_TOP,
                start = BottomPanelConstants.TAB_PADDING_HORIZONTAL,
                end = BottomPanelConstants.TAB_PADDING_HORIZONTAL,
                bottom = BottomPanelConstants.TAB_PADDING_BOTTOM
            ),
        horizontalArrangement = Arrangement.spacedBy(BottomPanelConstants.LAYOUT_SPACING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(DefaultLayouts) { index, layout ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(BottomPanelConstants.LAYOUT_ITEM_SPACING)
            ) {
                Text(
                    text = layout.sizeType,
                    fontWeight = FontWeight.Bold,
                    fontSize = BottomPanelConstants.LAYOUT_TEXT_SIZE
                )
                ClickableLayoutComponent(
                    data = layout,
                    isClicked = activeLayout == layout,
                    onComponentClick = {
                        activeLayout = if (activeLayout == layout) null else layout
                        // 클릭한 레이아웃의 위치로 스크롤
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(index)
                        }
                    },
                    onAddClick = {
                        onLayoutSelected(it)
                        activeLayout = null
                    }
                )
            }
        }
    }
}
