package com.example.composetoglance.editor.bottompanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.editor.widget.Category
import com.example.composetoglance.editor.widget.DragTargetWidgetItem
import com.example.composetoglance.editor.widget.Widget

@Composable
fun WidgetsList(
    widgetList: List<Widget>,
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        if (selectedCategoryId == null) {
            CategoryList(
                categories = categories,
                onCategoryClick = { categoryId ->
                    selectedCategoryId = categoryId
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            val selectedCategory = categories.find { it.id == selectedCategoryId }
            // 헤더 (뒤로가기 버튼과 카테고리 이름)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
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
            // 위젯 리스트
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
