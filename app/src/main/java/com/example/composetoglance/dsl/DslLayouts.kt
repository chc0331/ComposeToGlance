package com.example.composetoglance.dsl

import android.R
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.dimensionDp
import com.example.dsl.Column
import com.example.dsl.Image
import com.example.dsl.Progress
import com.example.dsl.Row
import com.example.dsl.Spacer
import com.example.dsl.Text
import com.example.dsl.WidgetLayout
import com.example.dsl.padding
import com.example.dsl.proto.FontWeight.FONT_WEIGHT_BOLD
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_CENTER
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_CENTER
import com.example.dsl.proto.WidgetLayoutDocument

/**
 * DSL을 사용한 간단한 레이아웃 예시들
 *
 * 기존 빌더 API와 비교하여 훨씬 간결하고 읽기 쉬운 코드입니다.
 */


/**
 * 예시 1: 음악 플레이어 위젯
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
                drawableResId = R.drawable.ic_media_play,
                width = dimensionDp(200f),
                height = dimensionDp(200f),
                padding = padding(bottom = 16f),
                contentScale = com.example.dsl.proto.ContentScale.CONTENT_SCALE_CROP
            )

            // 노래 정보
            Text(
                "노래 제목",
                fontSize = 20f,
                fontWeight = FONT_WEIGHT_BOLD,
                textColor = Color.White.toArgb(),
                padding = padding(bottom = 4f)
            )
            Text(
                "아티스트 이름",
                fontSize = 14f,
                textColor = Color.LightGray.toArgb(),
                padding = padding(bottom = 16f)
            )

            // 재생 진행률 바
            Progress(
                maxValue = 100f,
                progressValue = 40f,
                height = dimensionDp(4f),
                padding = padding(bottom = 16f),
                progressColor = Color.White.toArgb(),
                backgroundColor = Color.Gray.toArgb()
            )

            // 컨트롤 버튼
            Row(
                verticalAlignment = V_ALIGN_CENTER,
                horizontalAlignment = H_ALIGN_CENTER
            ) {
                Image(
                    drawableResId = R.drawable.ic_media_previous,
                    width = dimensionDp(48f),
                    height = dimensionDp(48f)
                )
                Spacer(width = dimensionDp(16f))
                Image(
                    drawableResId = R.drawable.ic_media_pause,
                    width = dimensionDp(64f),
                    height = dimensionDp(64f)
                )
                Spacer(width = dimensionDp(16f))
                Image(
                    drawableResId = R.drawable.ic_media_next,
                    width = dimensionDp(48f),
                    height = dimensionDp(48f)
                )
            }
        }
    }
}

/**
 * 예시 2: 동적으로 생성된 비트맵을 표시하는 예제
 */
fun createBitmapImageDsl(): WidgetLayoutDocument {
    val bitmap = createRedBitmap(200, 100)
    return WidgetLayout {
        Column(
            horizontalAlignment = H_ALIGN_CENTER,
            padding = padding(all = 16f)
        ) {
            Text(
                "Bitmap Image Example",
                fontSize = 18f,
                fontWeight = FONT_WEIGHT_BOLD,
                padding = padding(bottom = 8f)
            )
            Image(
                bitmap = bitmap,
                width = dimensionDp(200f),
                height = dimensionDp(100f)
            )
        }
    }
}

private fun createRedBitmap(width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.RED)
    return bitmap
}
