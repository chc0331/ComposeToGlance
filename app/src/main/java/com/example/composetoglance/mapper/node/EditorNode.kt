package com.example.composetoglance.mapper.node

import com.example.composetoglance.proto.WidgetNode

interface EditorNode {
    fun toProto(): WidgetNode
}