package com.example.widget.content

import android.R
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import com.example.dsl.builder.color
import com.example.dsl.builder.colorProvider
import com.example.dsl.builder.dimensionDp
import com.example.dsl.Column
import com.example.dsl.provider.DslLocalProvider
import com.example.dsl.provider.DslLocalSize
import com.example.dsl.Image
import com.example.dsl.Progress
import com.example.dsl.Row
import com.example.dsl.Spacer
import com.example.dsl.Text
import com.example.dsl.WidgetScope
import com.example.dsl.padding
import com.example.dsl.proto.ContentScale
import com.example.dsl.proto.FontWeight.FONT_WEIGHT_BOLD
import com.example.dsl.proto.HorizontalAlignment.H_ALIGN_CENTER
import com.example.dsl.proto.VerticalAlignment.V_ALIGN_CENTER

/**
 * 예시 1: 음악 플레이어 위젯
 */
fun WidgetScope.MusicWidget() {
    val currentSize = getLocal(DslLocalSize) as DpSize
    Log.i("heec.choi", "Dsl Size0 : $currentSize")
    Column(
        padding = padding(all = 16f),
        horizontalAlignment = H_ALIGN_CENTER,
        backgroundColor = colorProvider(color = color(Color.DarkGray.toArgb()))
    ) {
        Log.i("heec.choi", "Dsl Size1 : $currentSize")
        // 앨범 아트
        Image(
            drawableResId = R.drawable.ic_media_play,
            width = dimensionDp(200f),
            height = dimensionDp(200f),
            padding = padding(bottom = 16f),
            contentScale = ContentScale.CONTENT_SCALE_CROP
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

        Progress()
        // 컨트롤 버튼
        DslLocalProvider(DslLocalSize provides (currentSize/2)){
            Log.i("heec.choi","Dsl size2 : ${getLocal(DslLocalSize)}")
            Controller()
        }
    }


}

fun WidgetScope.Controller() {
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