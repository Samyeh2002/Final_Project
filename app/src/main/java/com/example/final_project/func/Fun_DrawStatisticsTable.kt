package com.example.final_project.func

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

// 用於針對當月的
@Composable
fun CircularStatistics_Month(percentages: List<Float>, colors: List<Color>) {
    val strokeWidth = 30f

    Canvas(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp)
    ) {

        // 該月沒有紀錄
        if (percentages.size == 1 && colors.size == 1) {

            // 繪製圓環
            drawArc(
                color = colors[0],
                startAngle = 270F,
                sweepAngle = 360F,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Butt)
            )

            // 該月有紀錄
        } else {

            // 設定起始位置
            var startAngle = -90f

            // 繪製圓環
            percentages.forEachIndexed { index, percentage ->
                val sweepAngle = percentage * 3.6f
                drawArc(
                    color = colors.getOrElse(index) { Color.Red },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(strokeWidth, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle
            }
        }
    }
}

// 用於針對當日的
@Composable
fun CircularStatistics_Today(percentages: List<Float>, colors: List<Color>) {
    val sweepAngles = percentages.map { it * 360 }

    Canvas(modifier = Modifier.size(200.dp)) {
        var startAngle = -90f
        sweepAngles.forEachIndexed { index, sweepAngle ->
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 30f)
            )
            startAngle += sweepAngle
        }
    }
}

// 用於旁邊文字的裝飾
@Composable
fun ColorIndicator(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}