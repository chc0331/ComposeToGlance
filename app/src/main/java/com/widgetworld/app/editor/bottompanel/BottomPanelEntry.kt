package com.widgetworld.app.editor.bottompanel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.widgetworld.widgetcomponent.LayoutType
import com.widgetworld.widgetcomponent.WidgetCategory
import com.widgetworld.widgetcomponent.component.WidgetComponent

@Composable
fun BottomPanelWithTabs(
    widgets: List<WidgetComponent>,
    categories: List<WidgetCategory>,
    onLayoutSelected: (LayoutType) -> Unit,
    modifier: Modifier = Modifier,
    onWidgetSelected: (WidgetComponent) -> Unit = {},
    selectedLayout: LayoutType? = null
) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("레이아웃", "위젯")

    // 레이아웃이 선택 해제되면 레이아웃 탭으로 자동 전환
    LaunchedEffect(selectedLayout) {
        if (selectedLayout == null && tabIndex == 1) {
            tabIndex = 0
        }
    }

    Column(modifier = modifier) {
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[tabIndex])
                        .fillMaxHeight()
                        .width(80.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                }
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
        AnimatedContent(
            targetState = tabIndex,
            transitionSpec = {
                fadeIn(tween(durationMillis = 400)) togetherWith fadeOut(tween(durationMillis = 200))
            }) { tabIndex ->
            when (tabIndex) {
                0 -> LayoutsTabContent(onLayoutSelected)
                1 -> WidgetsTabContent(
                    widgetList = widgets,
                    categories = categories,
                    onWidgetSelected = onWidgetSelected,
                    modifier = Modifier
                )
            }
        }
    }
}
