package com.example.composetoglance.examples

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.toolkit.builder.color
import com.example.toolkit.builder.colorProvider
import com.example.toolkit.builder.dimensionDp
import com.example.toolkit.builder.dimensionWeight
import com.example.toolkit.builder.matchParentDimension
import com.example.toolkit.dsl.Box
import com.example.toolkit.dsl.Button
import com.example.toolkit.dsl.Column
import com.example.toolkit.dsl.Image
import com.example.toolkit.dsl.Progress
import com.example.toolkit.dsl.Row
import com.example.toolkit.dsl.Spacer
import com.example.toolkit.dsl.Text
import com.example.toolkit.dsl.WidgetLayout
import com.example.toolkit.dsl.padding
import com.example.toolkit.proto.AlignmentType.ALIGNMENT_TYPE_CENTER
import com.example.toolkit.proto.FontWeight.FONT_WEIGHT_BOLD
import com.example.toolkit.proto.HorizontalAlignment.H_ALIGN_CENTER
import com.example.toolkit.proto.HorizontalAlignment.H_ALIGN_START
import com.example.toolkit.proto.ProgressType
import com.example.toolkit.proto.TextAlign.TEXT_ALIGN_CENTER
import com.example.toolkit.proto.VerticalAlignment.V_ALIGN_CENTER
import com.example.toolkit.proto.WidgetLayoutDocument

/**
 * DSL을 사용한 간단한 레이아웃 예시들
 *
 * 기존 빌더 API와 비교하여 훨씬 간결하고 읽기 쉬운 코드입니다.
 */

/**
 * 예시 1: Column 안에 Text 두 개
 *
 * 기존 방식 (약 50줄) vs DSL 방식 (약 10줄)
 */
fun createSimpleColumnLayoutDsl(): WidgetLayoutDocument {
    return WidgetLayout {
        Column(
            horizontalAlignment = H_ALIGN_CENTER, backgroundColor = colorProvider(
                color = color(Color.Gray.toArgb())
            )
        ) {
            Text("Hello Glance!", fontSize = 18f, fontWeight = FONT_WEIGHT_BOLD)
            Text(
                "프로토 빌더 예시",
                fontSize = 14f,
                textColor = 0xFF666666.toInt(),
                padding = padding(top = 8f)
            )
        }
    }
}

/**
 * 예시 2: Box 안에 Image와 Text
 */
fun createBoxWithImageAndTextDsl(): WidgetLayoutDocument {
    return WidgetLayout {
        Box(
            width = dimensionDp(200f),
            height = dimensionDp(200f),
            alignment = ALIGNMENT_TYPE_CENTER
        ) {
            Image(
                drawableResId = android.R.drawable.ic_menu_gallery,
                width = dimensionDp(200f),
                height = dimensionDp(200f)
            )
            Text(
                "Overlay Text",
                fontSize = 16f,
                fontWeight = FONT_WEIGHT_BOLD,
                textColor = 0xFFFFFFFF.toInt(),
                textAlign = TEXT_ALIGN_CENTER
            )
        }
    }
}

/**
 * 예시 3: Row 안에 Text와 Button
 */
fun createRowWithButtonAndTextDsl(): WidgetLayoutDocument {
    return WidgetLayout {
        Row(
            horizontalAlignment = H_ALIGN_START,
            verticalAlignment = V_ALIGN_CENTER
        ) {
            Text(
                "Status: Active",
                fontSize = 16f,
                textColor = 0xFF2196F3.toInt(),
                padding = padding(horizontal = 16f, end = 8f)
            )
            Button(
                "Click Me",
                fontSize = 14f,
                fontWeight = FONT_WEIGHT_BOLD,
                textColor = 0xFFFFFFFF.toInt(),
                backgroundColor = 0xFF2196F3.toInt(),
                padding = padding(horizontal = 16f, vertical = 8f),
                cornerRadius = 8f
            )
        }
    }
}

/**
 * 예시 4: 복잡한 중첩 레이아웃
 *
 * 기존 방식 (약 100줄) vs DSL 방식 (약 30줄)
 */
fun createNestedLayoutDsl(): WidgetLayoutDocument {
    return WidgetLayout {
        Column(
            padding = padding(all = 16f),
            horizontalAlignment = H_ALIGN_START
        ) {
            // Title
            Text(
                "제목",
                fontSize = 24f,
                fontWeight = FONT_WEIGHT_BOLD,
                textAlign = TEXT_ALIGN_CENTER,
                padding = padding(bottom = 16f)
            )

            // Row with Image and Column
            Row(
                padding = padding(bottom = 16f),
                verticalAlignment = V_ALIGN_CENTER
            ) {
                Image(
                    drawableResId = android.R.drawable.ic_menu_info_details,
                    width = dimensionDp(80f),
                    height = dimensionDp(80f),
                    padding = padding(end = 16f)
                )

                Column(width = dimensionWeight(1f)) {
                    Text(
                        "부제목",
                        fontSize = 18f,
                        fontWeight = FONT_WEIGHT_BOLD,
                        textColor = 0xFF333333.toInt(),
                        padding = padding(bottom = 8f)
                    )
                    Text(
                        "설명 텍스트입니다.",
                        fontSize = 14f,
                        textColor = 0xFF666666.toInt()
                    )
                }
            }

            // Button
            Button(
                "액션 버튼",
                width = matchParentDimension,
                fontSize = 16f,
                fontWeight = FONT_WEIGHT_BOLD,
                textColor = 0xFFFFFFFF.toInt(),
                backgroundColor = 0xFF4CAF50.toInt(),
                padding = padding(vertical = 8f),
                cornerRadius = 8f
            )
        }
    }
}

/**
 * 예시 5: Progress Bar가 있는 카드 레이아웃
 */
fun createCardWithProgressDsl(): WidgetLayoutDocument {
    return WidgetLayout {
        Box(
            padding = padding(all = 16f),
            alignment = ALIGNMENT_TYPE_CENTER
        ) {
            Text(
                "작업 진행률",
                fontSize = 18f,
                fontWeight = FONT_WEIGHT_BOLD,
                padding = padding(bottom = 8f)
            )

            Progress(
                type = ProgressType.PROGRESS_TYPE_LINEAR,
                maxValue = 100f,
                progressValue = 65f,
                height = dimensionDp(8f),
                padding = padding(bottom = 8f),
                progressColor = 0xFF4CAF50.toInt(),
                backgroundColor = 0xFFE0E0E0.toInt()
            )

            Text(
                "65% 완료",
                fontSize = 14f,
                textColor = 0xFF666666.toInt()
            )
        }
    }
}

/**
 * 예시 6: 복잡한 프로필 카드
 */
fun createProfileCardDsl(): WidgetLayoutDocument {
    return WidgetLayout {
        Column(
            padding = padding(all = 16f),
            horizontalAlignment = H_ALIGN_CENTER
        ) {
            // 프로필 이미지
            Image(
                drawableResId = android.R.drawable.ic_menu_camera,
                width = dimensionDp(100f),
                height = dimensionDp(100f),
                padding = padding(bottom = 16f)
            )

            // 이름
            Text(
                "홍길동",
                fontSize = 24f,
                fontWeight = FONT_WEIGHT_BOLD,
                padding = padding(bottom = 8f)
            )

            // 직책
            Text(
                "소프트웨어 엔지니어",
                fontSize = 16f,
                textColor = 0xFF666666.toInt(),
                padding = padding(bottom = 16f)
            )

            // 구분선 (Spacer로 표현)
            Spacer(
                width = matchParentDimension,
                height = dimensionDp(1f)
            )

            // 통계 정보 Row
            Row(
                padding = padding(top = 16f),
                horizontalAlignment = H_ALIGN_CENTER
            ) {
                Column(horizontalAlignment = H_ALIGN_CENTER) {
                    Text("150", fontSize = 20f, fontWeight = FONT_WEIGHT_BOLD)
                    Text("팔로워", fontSize = 12f, textColor = 0xFF666666.toInt())
                }

                Spacer(width = dimensionDp(32f))

                Column(horizontalAlignment = H_ALIGN_CENTER) {
                    Text("50", fontSize = 20f, fontWeight = FONT_WEIGHT_BOLD)
                    Text("팔로잉", fontSize = 12f, textColor = 0xFF666666.toInt())
                }

                Spacer(width = dimensionDp(32f))

                Column(horizontalAlignment = H_ALIGN_CENTER) {
                    Text("200", fontSize = 20f, fontWeight = FONT_WEIGHT_BOLD)
                    Text("게시물", fontSize = 12f, textColor = 0xFF666666.toInt())
                }
            }

            // 액션 버튼들
            Row(
                padding = padding(top = 16f),
                horizontalAlignment = H_ALIGN_CENTER
            ) {
                Button(
                    "메시지",
                    padding = padding(horizontal = 16f, vertical = 8f),
                    cornerRadius = 8f
                )

                Spacer(width = dimensionDp(8f))

                Button(
                    "팔로우",
                    backgroundColor = 0xFF4CAF50.toInt(),
                    padding = padding(horizontal = 16f, vertical = 8f),
                    cornerRadius = 8f
                )
            }
        }
    }
}

