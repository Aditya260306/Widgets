package com.aether.widgets.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun FidgetScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)) // Pure black as per fidget.html
    ) {
        // Canvas Area for Orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Violet Orb 1 (Large)
            val primaryColor = Color(0xFFCABEFF)
            drawCircle(
                color = primaryColor.copy(alpha = 0.8f),
                radius = 40.dp.toPx(),
                center = Offset(canvasWidth * 0.15f + 40.dp.toPx(), canvasHeight * 0.2f + 40.dp.toPx())
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(canvasWidth * 0.15f + 40.dp.toPx(), canvasHeight * 0.2f + 40.dp.toPx()),
                    radius = 60.dp.toPx()
                ),
                radius = 60.dp.toPx(),
                center = Offset(canvasWidth * 0.15f + 40.dp.toPx(), canvasHeight * 0.2f + 40.dp.toPx())
            )

            // Mint Orb 1 (Medium)
            val tertiaryColor = Color(0xFF28DFB3)
            drawCircle(
                color = tertiaryColor.copy(alpha = 0.8f),
                radius = 28.dp.toPx(),
                center = Offset(canvasWidth * 0.75f - 28.dp.toPx(), canvasHeight * 0.45f + 28.dp.toPx())
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(tertiaryColor.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(canvasWidth * 0.75f - 28.dp.toPx(), canvasHeight * 0.45f + 28.dp.toPx()),
                    radius = 40.dp.toPx()
                ),
                radius = 40.dp.toPx(),
                center = Offset(canvasWidth * 0.75f - 28.dp.toPx(), canvasHeight * 0.45f + 28.dp.toPx())
            )

            // Rose Orb 1 (Small)
            val errorColor = Color(0xFFFFB4AB)
            drawCircle(
                color = errorColor.copy(alpha = 0.8f),
                radius = 16.dp.toPx(),
                center = Offset(canvasWidth * 0.3f + 16.dp.toPx(), canvasHeight * 0.6f + 16.dp.toPx())
            )

            // Gold Orb 1 (Large)
            val secondaryColor = Color(0xFFEFC13E)
            drawCircle(
                color = secondaryColor.copy(alpha = 0.8f),
                radius = 36.dp.toPx(),
                center = Offset(canvasWidth * 0.9f - 36.dp.toPx(), canvasHeight * 0.1f + 36.dp.toPx())
            )

            // Violet Orb 2 (Medium)
            drawCircle(
                color = primaryColor.copy(alpha = 0.8f),
                radius = 24.dp.toPx(),
                center = Offset(canvasWidth * 0.1f + 24.dp.toPx(), canvasHeight * 0.7f - 24.dp.toPx())
            )
        }

        // Instruction Annotation
        Text(
            text = "Orbs respond to device tilt. Touch to drag and throw. Physics collision between orbs.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 64.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth()
                .align(androidx.compose.ui.Alignment.TopCenter)
        )
    }
}
