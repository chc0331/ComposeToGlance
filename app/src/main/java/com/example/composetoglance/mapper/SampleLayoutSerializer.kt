//package com.example.composetoglance.mapper
//
//import com.example.composetoglance.proto.Color
//import com.example.composetoglance.proto.ColorProvider
//import com.example.composetoglance.proto.ColumnLayoutProperty
//import com.example.composetoglance.proto.Dimension
//import com.example.composetoglance.proto.Dp
//import com.example.composetoglance.proto.FontWeight
//import com.example.composetoglance.proto.HorizontalAlignment
//import com.example.composetoglance.proto.Padding
//import com.example.composetoglance.proto.TextContent
//import com.example.composetoglance.proto.TextProperty
//import com.example.composetoglance.proto.VerticalAlignment
//import com.example.composetoglance.proto.ViewProperty
//import com.example.composetoglance.proto.WidgetLayoutDocument
//import com.example.composetoglance.proto.WidgetNode
//
///**
// * Simple in-memory representation of a Compose layout we want to serialize.
// */
//sealed interface EditorNode {
//    fun toProto(): WidgetNode
//}
//
//data class ColumnEditorNode(
//    val viewId: Int,
//    val children: List<EditorNode>,
//    val horizontalAlignment: HorizontalAlignment = HorizontalAlignment.H_ALIGN_CENTER,
//    val verticalAlignment: VerticalAlignment = VerticalAlignment.V_ALIGN_TOP,
//) : EditorNode {
//    override fun toProto(): WidgetNode {
//        val columnBuilder = ColumnLayoutProperty.newBuilder()
//            .setViewProperty(
//                ViewProperty.newBuilder()
//                    .setViewId(viewId)
//                    .setWidth(Dimension.newBuilder().setMatchParent(true))
//                    .setHeight(Dimension.newBuilder().setWrapContent(true))
//            )
//            .setHorizontalAlignment(horizontalAlignment)
//            .setVerticalAlignment(verticalAlignment)
//
//        val nodeBuilder = WidgetNode.newBuilder()
//            .setColumn(columnBuilder)
//
//        children.forEach { child ->
//            nodeBuilder.addChildren(child.toProto())
//        }
//
//        return nodeBuilder.build()
//    }
//}
//
//data class TextEditorNode(
//    val viewId: Int,
//    val text: String,
//) : EditorNode {
//    override fun toProto(): WidgetNode {
//        val textProperty = TextProperty.newBuilder()
//            .setViewProperty(
//                ViewProperty.newBuilder()
//                    .setViewId(viewId)
//                    .setWidth(Dimension.newBuilder().setWrapContent(true))
//                    .setHeight(Dimension.newBuilder().setWrapContent(true))
//                    .setPadding(
//                        Padding.newBuilder()
//                            .setStart(Dp.newBuilder().setValue(8f))
//                            .setEnd(Dp.newBuilder().setValue(8f))
//                    )
//            )
//            .setText(TextContent.newBuilder().setText(text))
//            .setFontColor(
//                ColorProvider.newBuilder()
//                    .setColor(Color.newBuilder().setArgb(0xFF000000.toInt()))
//            )
//            .setFontSize(16f)
//            .setFontWeight(FontWeight.FONT_WEIGHT_MEDIUM)
//
//        return WidgetNode.newBuilder()
//            .setText(textProperty)
//            .build()
//    }
//}
//
///**
// * Example: Compose UI
// *
// * Column(
// *   horizontalAlignment = Alignment.CenterHorizontally
// * ) {
// *   Text("Hello Glance!")
// *   Text("Drag widgets, save as proto")
// * }
// */
//fun sampleComposeLayoutToProto(): WidgetLayoutDocument {
//    val textNode1 = TextEditorNode(viewId = 2, text = "Hello Glance!")
//    val textNode2 = TextEditorNode(viewId = 3, text = "Drag widgets, save as proto")
//
//    val columnNode = ColumnEditorNode(
//        viewId = 1,
//        children = listOf(textNode1, textNode2)
//    )
//
//    return WidgetLayoutDocument.newBuilder()
//        .setRoot(columnNode.toProto())
//        .build()
//}
//
