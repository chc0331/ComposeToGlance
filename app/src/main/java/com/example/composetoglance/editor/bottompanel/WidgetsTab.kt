package com.example.composetoglance.editor.bottompanel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composetoglance.editor.widget.DragTargetWidgetItem
import com.example.widget.WidgetCategory
import com.example.widget.component.WidgetComponent
import kotlin.collections.filter
import kotlin.collections.find

@Composable
fun WidgetsList(
    widgetList: List<WidgetComponent>,
    categories: List<WidgetCategory>,
    modifier: Modifier = Modifier,
    onWidgetSelected: (WidgetComponent) -> Unit = {}
) {
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var activeWidget by remember { mutableStateOf<WidgetComponent?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AnimatedContent(
            targetState = selectedCategoryId,
            transitionSpec = {
                if (targetState != null) {
                    // 카테고리 -> 컴포넌트 리스트: 페이드 인
                    fadeIn() togetherWith fadeOut()
                } else {
                    // 컴포넌트 리스트 -> 카테고리: 왼쪽에서 슬라이드 인
                    (slideInHorizontally { -it } + fadeIn()) togetherWith
                            (slideOutHorizontally { it } + fadeOut())
                }
            },
            label = "category_transition",
            modifier = Modifier.fillMaxSize()
        ) { categoryName ->
            if (categoryName == null) {
                CategoryList(
                    categories = categories,
                    onCategoryClick = { selectedCategoryId = it },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                val selectedCategory = categories.find { it.name == categoryName }
                val filteredWidgets = widgetList.filter { it.getWidgetCategory().name == categoryName }
                val visibleItems = remember { mutableStateListOf<Int>() }

                // 카테고리 진입 시 위젯들을 순차적으로 표시
                LaunchedEffect(categoryName) {
                    visibleItems.clear()
                    filteredWidgets.forEachIndexed { index, _ ->
                        delay(index * 50L)
                        visibleItems.add(index)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    // 헤더 (뒤로가기 버튼과 카테고리 이름) - 즉시 표시
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
                    // 위젯 리스트 - 순차적으로 나타나는 애니메이션
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        contentPadding = PaddingValues(BottomPanelConstants.WIDGET_LIST_PADDING),
                        horizontalArrangement = Arrangement.spacedBy(BottomPanelConstants.WIDGET_LIST_SPACING),
                        verticalArrangement = Arrangement.spacedBy(BottomPanelConstants.WIDGET_LIST_SPACING)
                    ) {
                        itemsIndexed(filteredWidgets) { index, widget ->
                            AnimatedVisibility(
                                visible = visibleItems.contains(index),
                                enter = fadeIn(
                                    animationSpec = tween(durationMillis = 300)
                                ),
                                modifier = Modifier
                            ) {
                                DragTargetWidgetItem(
                                    data = widget,
                                    isClicked = activeWidget == widget,
                                    onComponentClick = {
                                        activeWidget = if (activeWidget == widget) null else widget
                                    },
                                    onAddClick = {
                                        onWidgetSelected(it)
                                        activeWidget = null
                                    },
                                    onDragStart = {
                                        activeWidget = null
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
