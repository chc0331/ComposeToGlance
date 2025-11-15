package com.example.composetoglance.draganddrop.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Layout(val type: String, val sizeType: String)
data class PositionedLayout(val layout: Layout, val offset: Offset)

@Composable
fun ClickableLayoutComponent(
    modifier: Modifier = Modifier,
    data: Layout,
    isClicked: Boolean,
    onComponentClick: () -> Unit,
    onAddClick: (Layout) -> Unit,
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .clickable { onComponentClick() },
        contentAlignment = Alignment.Center
    ) {
        LayoutComponent(data.type, data.sizeType, shouldAnimate = false, showText = true)
        if (isClicked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Button(
                    onClick = { onAddClick(data) },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("추가")
                }
            }
        }
    }
}

@Composable
fun LayoutComponent(
    type: String,
    layoutType: String,
    shouldAnimate: Boolean = false,
    showText: Boolean = true
) {
    val (width, height) = when (layoutType) {
        "Small" -> Pair(105.dp, 45.dp)
        "Medium" -> Pair(90.dp, 90.dp)
        "Large" -> Pair(180.dp, 90.dp)
        else -> Pair(105.dp, 45.dp) // Default to Small
    }

    Box(
        modifier = Modifier
            .size(width, height)
            .background(Color.LightGray)
            .border(1.dp, Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            "Full" -> Box(modifier = Modifier.fillMaxSize()) {
                if (showText) {
                    Text(type, Modifier.align(Alignment.Center))
                }
            }

            "1:1" -> Row {
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()) {
                    if (showText) {
                        Text("1", Modifier.align(Alignment.Center))
                    }
                }
                Divider(modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp), color = Color.DarkGray)
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()) {
                    if (showText) {
                        Text("1", Modifier.align(Alignment.Center))
                    }
                }
            }

            "1:N" -> Row {
                Box(
                    modifier = Modifier
                        .width(height) // Set width equal to height to make a square
                        .fillMaxHeight()
                ) {
                    if (showText) {
                        Text("1", Modifier.align(Alignment.Center))
                    }
                }
                Divider(modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp), color = Color.DarkGray)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f) // Fill remaining space
                ) {
                    if (showText) {
                        Text("N", Modifier.align(Alignment.Center))
                    }
                }
            }

            else -> if (showText) {
                Text(type)
            }
        }
    }
}
