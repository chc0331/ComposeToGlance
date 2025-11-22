package com.example.composetoglance.mapper.node

import com.example.toolkit.proto.WidgetNode

interface EditorNode {
    fun toProto(): WidgetNode
}