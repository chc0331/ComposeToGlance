package com.widgetworld.app.editor.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.ui.unit.dp
import com.widgetworld.app.editor.widget.layout.LayoutComponentContainer
import com.widgetworld.widgetcomponent.LayoutType
import kotlinx.coroutines.launch

@Composable
fun LayoutsTabContent(onLayoutSelected: (LayoutType) -> Unit) {
    var activeLayout by remember { mutableStateOf<LayoutType?>(null) }
    val scrollState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    LazyVerticalGrid(
        state = scrollState,
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(
                top = 8.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(
            listOf(
                LayoutType.Small,
                LayoutType.Medium,
                LayoutType.Large,
                LayoutType.ExtraLarge
            )
        ) { index, layout ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = layout.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                LayoutComponentContainer(
                    modifier = Modifier.wrapContentSize(),
                    layout = layout,
                    isClicked = activeLayout == layout,
                    onLayoutClick = {
                        activeLayout = if (activeLayout == layout) null else layout
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
