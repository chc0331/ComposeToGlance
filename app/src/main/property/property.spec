0) Base Property data structure
 0-1. Dp
 - dp: float
 0-2. Dimension
 - dp (Dp)
 - wrap_content (boolean)
 - match_parent (boolean)
 - weight (float)
 - resource_id (int)
 0-3. Component
  - package_name (string)
  - class_name (string)
 0-4. Action
 - activity (boolean)
 - service (boolean)
 - broadcast_receiver (boolean)
 - component (Component)
 0-5. CornerRadius
 - radius (float)
 0-6. Padding
 - start (Dp)
 - top (Dp)
 - end (Dp)
 - bottom (Dp)
 0-7. Semantics
  - content_description (string)
 0-8. FontWeight
  - enum { Normal, Medium, Bold }
 0-9. TextAlign
  - enum {Start, End, Center }
 0-10. Color
  - color (int)
 0-11. ColorProvider
  - res_id (int)
  - color (Color)
  - dark_color (Color)
 0-12. TextContent
  - res_id (int)
  - text (string)
 0-13. ImageProvider
  - drawable (int)
  - bitmap (bitmap)
  - uri (Uri)
  - icon (Icon)
 0-14. ContentScale
  - enum {Crop, Fit, FillBounds}
 0-15. ProgressType
  - enum {LinearProgress, CircularProgress}

1) Layout Property data structure
 1-1. Horizontal
  - enum (start, center_horizontally, end)
 1-2. Vertical
  - enum (top, center_vertically, end)
 1-3. Alignment
  - enum
     - top_start(Horizontal.start, Vertical.top)
     - top_center(Horizontal.center_horizontally, Vertical.top)
     - top_end(Horizontal.end, Vertical.top)
     - center_start(Horizontal.start, Vertical.center_vertically)
     - center(Horizontal.center_horizontally, Vertical.center_vertically)
     - center_end(Horizontal.end, Vertical.center_vertically)
     - bottom_start(Horizontal.start, Vertical.bottom)
     - bottom_center(Horizontal.center_horizontally, Vertical.bottom)
     - bottom_end(Horizontal.end, Vertical.bottom)
     - top(Vertical.top)
     - center_vertically(Vertical.center_vertically)
     - bottom(Vertical.bottom)
     - start(Horizontal.start)
     - center_horizontally(Horizontal.center_horizontally)
     - end(Horizontal.end)

2) View property data structure of ui component
 1-1. ViewProperty
 - view_id (int)
 - width (Dimension)
 - height (Dimension)
 - padding (Padding)
 - corner_radius (Corner Radius)
 - semantics (Semantics)
 - click_action (Action)

3) Property data structure of box layout component
  - view_property (ViewProperty)
  - content_alignment (Alignment)
  - children (list<int>)

4) Property data structure of row layout component
  - view_property (ViewProperty)
  - horizontal_alignment (Horizontal)
  - vertical_alignment (Vertical)
  - children (list<int>)

5) Property data structure of column layout component
  - view_property (ViewProperty)
  - horizontal_alignment (Horizontal)
  - vertical_alignment (Vertical)
  - children (list<int>)

6) Property of text ui component
  - view_property (ViewProperty)
  - text (TextContent)
  - maxLine (int)
  - font_color (ColorProvider)
  - font_size (float)
  - font_weight (FontWeight)

7) Property of image ui component
  - view_property (ViewProperty)
  - provider (ImageProvider)
  - tint_color (Color)
  - alpha (float)
  - content_scale (ContentScale)

8) Property of button ui component
  - view_property (ViewProperty)
  - text (TextContent)
  - maxLine (int)
  - font_color (ColorProvider)
  - font_size (float)
  - font_weight (FontWeight)

9) Property of spacer ui component
  - view_property (viewProperty)

10) Property of progress ui component
  - progress_type (ProgressType)
  - max_value (float)
  - progress_value (float)
  - progress_color (ColorProvider)
  - background_color (ColorProvider)