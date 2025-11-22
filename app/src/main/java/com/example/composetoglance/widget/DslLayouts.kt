package com.example.composetoglance.widget

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.toolkit.builder.color
import com.example.toolkit.builder.colorProvider
import com.example.toolkit.builder.dimensionDp
import com.example.toolkit.dsl.Column
import com.example.toolkit.dsl.Image
import com.example.toolkit.dsl.Progress
import com.example.toolkit.dsl.Row
import com.example.toolkit.dsl.Spacer
import com.example.toolkit.dsl.Text
import com.example.toolkit.dsl.WidgetLayout
import com.example.toolkit.dsl.padding
import com.example.toolkit.proto.FontWeight.FONT_WEIGHT_BOLD
import com.example.toolkit.proto.HorizontalAlignment.H_ALIGN_CENTER
import com.example.toolkit.proto.VerticalAlignment.V_ALIGN_CENTER
import com.example.toolkit.proto.WidgetLayoutDocument

/**
 * DSL을 사용한 간단한 레이아웃 예시들
 *
 * 기존 빌더 API와 비교하여 훨씬 간결하고 읽기 쉬운 코드입니다.
 */

/**
 * 예시 7: 음악 플레이어 위젯
 */
fun createMusicPlayerWidgetDsl(): WidgetLayoutDocument {
    return WidgetLayout {
        Column(
            padding = padding(all = 16f),
            horizontalAlignment = H_ALIGN_CENTER,
            backgroundColor = colorProvider(color = color(Color.DarkGray.toArgb()))
        ) {
            // 앨범 아트
            Image(
                drawableResId = android.R.drawable.ic_media_play,
                width = dimensionDp(16f),
                height = dimensionDp(16f),
                contentScale = com.example.toolkit.proto.ContentScale.CONTENT_SCALE_CROP
            )

            // 노래 정보
            Text(
                "노래 제목",
                fontSize = 10f,
                fontWeight = FONT_WEIGHT_BOLD,
                textColor = Color.White.toArgb(),
                padding = padding(bottom = 4f)
            )
            Text(
                "아티스트 이름",
                fontSize = 8f,
                textColor = Color.LightGray.toArgb(),
                padding = padding(bottom = 16f)
            )

            // 재생 진행률 바
            Progress(
                maxValue = 100f,
                progressValue = 40f,
                height = dimensionDp(16f),
                padding = padding(bottom = 4f),
                progressColor = Color.Black.toArgb(),
                backgroundColor = Color.White.toArgb()
            )

            // 컨트롤 버튼
            Row(
                verticalAlignment = V_ALIGN_CENTER,
                horizontalAlignment = H_ALIGN_CENTER
            ) {
                Image(drawableResId = android.R.drawable.ic_media_previous, width = dimensionDp(48f), height = dimensionDp(48f))
                Spacer(width = dimensionDp(16f))
                Image(drawableResId = android.R.drawable.ic_media_pause, width = dimensionDp(64f), height = dimensionDp(64f))
                Spacer(width = dimensionDp(16f))
                Image(drawableResId = android.R.drawable.ic_media_next, width = dimensionDp(48f), height = dimensionDp(48f))
            }
        }
    }
}
