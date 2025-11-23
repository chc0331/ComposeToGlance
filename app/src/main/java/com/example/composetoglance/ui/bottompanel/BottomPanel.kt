package com.example.composetoglance.ui.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.ui.layout.ClickableLayoutComponent
import com.example.composetoglance.ui.layout.Layout
import com.example.composetoglance.ui.widget.Category
import com.example.composetoglance.ui.widget.DragTargetWidgetItem
import com.example.composetoglance.ui.widget.Widget

/**
 * 기본 레이아웃 리스트
 */
private val DefaultLayouts = listOf(
    Layout("Full", "Small"),
    Layout("Full", "Medium"),
    Layout("Full", "Large")
)

/**
 * BottomPanel 관련 상수
 */
private object BottomPanelConstants {
    val TAB_PADDING_TOP = 8.dp
    val TAB_PADDING_HORIZONTAL = 16.dp
    val TAB_PADDING_BOTTOM = 8.dp
    val LAYOUT_SPACING = 16.dp
    val LAYOUT_TEXT_SIZE = 16.sp
    val LAYOUT_ITEM_SPACING = 8.dp
    val WIDGET_LIST_PADDING = 16.dp
    val WIDGET_LIST_SPACING = 8.dp
}

@Composable
fun BottomPanelWithTabs(
    widgets: List<Widget>,
    categories: List<Category>,
    onLayoutSelected: (Layout) -> Unit,
    modifier: Modifier = Modifier
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("레이아웃", "위젯")

    Column(modifier = modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title) })
            }
        }
        when (tabIndex) {
            0 -> LayoutsTabContent(onLayoutSelected)
            1 -> WidgetsList(
                widgetList = widgets,
                categories = categories,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun LayoutsTabContent(onLayoutSelected: (Layout) -> Unit) {
    var activeLayout by remember { mutableStateOf<Layout?>(null) }

    LazyRow(
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
        items(DefaultLayouts) { layout ->
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

@Composable
fun WidgetsList(
    widgetList: List<Widget>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    
    Column(modifier = modifier.fillMaxSize()) {
        // 뒤로가기 버튼 (카테고리가 선택된 경우에만 표시)
        if (selectedCategoryId != null) {
            val selectedCategory = categories.find { it.id == selectedCategoryId }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                IconButton(
                    onClick = { selectedCategoryId = null },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로가기"
                    )
                }
                Text(
                    text = selectedCategory?.name ?: "",
                    modifier = Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
        
        // 카테고리 목록 또는 위젯 목록 표시
        if (selectedCategoryId == null) {
            // 카테고리 목록 표시
            CategoryList(
                categories = categories,
                onCategoryClick = { categoryId ->
                    selectedCategoryId = categoryId
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // 선택된 카테고리의 위젯 목록 표시
            val filteredWidgets = widgetList.filter { it.categoryId == selectedCategoryId }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(BottomPanelConstants.WIDGET_LIST_PADDING),
                horizontalArrangement = Arrangement.spacedBy(BottomPanelConstants.WIDGET_LIST_SPACING),
                verticalArrangement = Arrangement.spacedBy(BottomPanelConstants.WIDGET_LIST_SPACING)
            ) {
                items(filteredWidgets) { widget ->
                    DragTargetWidgetItem(
                        data = widget,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryList(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentPadding = PaddingValues(BottomPanelConstants.WIDGET_LIST_PADDING),
        horizontalArrangement = Arrangement.spacedBy(BottomPanelConstants.WIDGET_LIST_SPACING),
        verticalArrangement = Arrangement.spacedBy(BottomPanelConstants.WIDGET_LIST_SPACING)
    ) {
        items(categories) { category ->
            CategoryItem(
                category = category,
                onClick = { onCategoryClick(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
