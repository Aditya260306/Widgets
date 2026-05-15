package com.aether.widgets.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aether.widgets.ui.theme.AuraMint
import com.aether.widgets.ui.theme.AuraOutlineVariant
import com.aether.widgets.ui.theme.AuraPrimary

/**
 * CircularArc — Canvas arc for battery % and API usage displays.
 * Renders a track arc and a filled progress arc with optional animation.
 */
@Composable
fun CircularArc(
    progress: Float,           // 0.0 to 1.0
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    strokeWidth: Dp = 4.dp,
    progressColor: Color = AuraMint,
    trackColor: Color = AuraOutlineVariant.copy(alpha = 0.3f),
    startAngleDeg: Float = -90f,   // 12 o'clock
    sweepDeg: Float = 360f
) {
    Canvas(modifier = modifier.size(size)) {
        val strokePx = strokeWidth.toPx()
        val diameter = this.size.minDimension - strokePx
        val topLeft = Offset(strokePx / 2, strokePx / 2)

        // Track
        drawArc(
            color = trackColor,
            startAngle = startAngleDeg,
            sweepAngle = sweepDeg,
            useCenter = false,
            topLeft = topLeft,
            size = androidx.compose.ui.geometry.Size(diameter, diameter),
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )

        // Progress
        drawArc(
            color = progressColor,
            startAngle = startAngleDeg,
            sweepAngle = sweepDeg * progress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = topLeft,
            size = androidx.compose.ui.geometry.Size(diameter, diameter),
            style = Stroke(width = strokePx, cap = StrokeCap.Round)
        )
    }
}

/**
 * HalfArc — 180° arc used in StatusStrip for API call display.
 */
@Composable
fun HalfArc(
    progress: Float,
    modifier: Modifier = Modifier,
    progressColor: Color = AuraPrimary
) {
    CircularArc(
        progress = progress,
        modifier = modifier,
        size = 36.dp,
        strokeWidth = 3.dp,
        progressColor = progressColor,
        startAngleDeg = 180f,
        sweepDeg = 180f
    )
}

/**
 * Sparkline — 7-bar miniature chart, used in WidgetDetail.
 */
@Composable
fun Sparkline(
    values: List<Int>,
    modifier: Modifier = Modifier,
    barColor: Color = AuraPrimary,
    width: Dp = 120.dp,
    height: Dp = 32.dp
) {
    Canvas(modifier = modifier.size(width, height)) {
        if (values.isEmpty()) return@Canvas
        val maxVal = values.max().coerceAtLeast(1).toFloat()
        val barW = this.size.width / (values.size * 2f - 1f)
        values.forEachIndexed { i, v ->
            val barH = (v / maxVal) * this.size.height
            val x = i * 2 * barW
            drawRect(
                color = barColor,
                topLeft = Offset(x, this.size.height - barH),
                size = androidx.compose.ui.geometry.Size(barW, barH)
            )
        }
    }
}

/**
 * ShimmerBox — animated shimmer placeholder for skeleton states.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    Box(
        modifier = modifier
            .background(
                color = AuraOutlineVariant.copy(alpha = shimmerAlpha),
                shape = RoundedCornerShape(cornerRadius)
            )
    )
}
