package com.example.composetoglance.ui.draganddrop.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Layout(val type: String, val sizeType: String)
data class PositionedLayout(val layout: Layout, val offset: Offset)

data class LayoutGridSpec(val rows: Int, val columns: Int)

private val layoutGridSpecs = mapOf(
    "Full" to mapOf(
        "Small" to LayoutGridSpec(rows = 1, columns = 2),
        "Medium" to LayoutGridSpec(rows = 2, columns = 2),
        "Large" to LayoutGridSpec(rows = 2, columns = 4)
    ),
    "1:1" to mapOf(
        "Small" to LayoutGridSpec(rows = 1, columns = 2),
        "Medium" to LayoutGridSpec(rows = 1, columns = 2)
    ),
    "1:N" to mapOf(
        "Medium" to LayoutGridSpec(rows = 1, columns = 2),
        "Large" to LayoutGridSpec(rows = 1, columns = 2)
    )
)

fun Layout.gridSpec(): LayoutGridSpec? = layoutGridSpecs[type]?.get(sizeType)

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
        LayoutComponent(data.type, data.sizeType, isPreview = true)
        if (isClicked) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
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

val WidgetLayoutComponentsize = mapOf<String, Pair<Dp, Dp>>(
    "Small" to Pair(155.dp, 80.dp),
    "Medium" to Pair(155.dp, 170.dp),
    "Large" to Pair(320.dp, 170.dp)
)
val PreviewLayoutComponentSize = mapOf<String, Pair<Dp, Dp>>(
    "Small" to Pair(105.dp, 45.dp),
    "Medium" to Pair(105.dp, 100.dp),
    "Large" to Pair(210.dp, 100.dp)
)

@Composable
fun LayoutComponent(
    type: String,
    layoutType: String,
    shouldAnimate: Boolean = false,
    showText: Boolean = false,
    isPreview: Boolean = false
) {
    val (width, height) = if (isPreview) PreviewLayoutComponentSize[layoutType] ?: Pair(
        105.dp,
        45.dp
    )
    else WidgetLayoutComponentsize[layoutType] ?: Pair(180.dp, 80.dp)

    Box(
        modifier = Modifier
            .size(width, height)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            "Full" -> {
                when (layoutType) {
                    "Small" -> {
                        Row(Modifier.fillMaxSize()) {
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                            Divider(
                                Modifier
                                    .fillMaxHeight()
                                    .width(1.dp),
                                color = MaterialTheme.colorScheme.outline
                            )
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }

                    "Medium" -> {
                        Column(Modifier.fillMaxSize()) {
                            Row(Modifier.weight(1f)) {
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                Divider(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                            Divider(
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp),
                                color = MaterialTheme.colorScheme.outline
                            )
                            Row(Modifier.weight(1f)) {
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                Divider(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }

                    "Large" -> {
                        Column(Modifier.fillMaxSize()) {
                            Row(Modifier.weight(1f)) {
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                Divider(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                Divider(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                Divider(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                            Divider(
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp),
                                color = MaterialTheme.colorScheme.outline
                            )
                            Row(Modifier.weight(1f)) {
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                Divider(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                Divider(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                                Divider(
                                    Modifier
                                        .fillMaxHeight()
                                        .width(1.dp),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Box(
                                    Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }
                }
            }

            "1:1" -> Row {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    if (showText) {
                        Text("1", Modifier.align(Alignment.Center))
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp), color = MaterialTheme.colorScheme.outline
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
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
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp), color = MaterialTheme.colorScheme.outline
                )
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
