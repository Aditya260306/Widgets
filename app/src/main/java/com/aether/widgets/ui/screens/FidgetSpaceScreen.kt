package com.aether.widgets.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aether.widgets.ui.fake.FakeData
import com.aether.widgets.ui.theme.*
import kotlin.random.Random

/**
 * FidgetSpaceScreen — the "mental palate cleanser".
 * Swipe-down from any part of the screen to dismiss.
 * Floating particles, breathing orb, and a daily affirmation.
 */
@Composable
fun FidgetSpaceScreen(onDismiss: () -> Unit) {
    val ctx = FakeData.context

    // Breathing animation — inhale 4s, hold 1s, exhale 4s
    val breathTransition = rememberInfiniteTransition(label = "breath")
    val breathScale by breathTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 9000
                0.85f at 0 using CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
                1.15f at 4000 using LinearEasing
                1.15f at 5000 using CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
                0.85f at 9000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "breathScale"
    )

    val breathLabel by remember {
        derivedStateOf {
            when {
                breathScale < 0.95f -> "Breathe in..."
                breathScale > 1.10f -> "Hold..."
                else                -> "Breathe out..."
            }
        }
    }

    // Particle animations
    val particleCount = 8
    val particlePhases = remember { List(particleCount) { Random.nextFloat() * 6000f } }
    val particleAnims = particlePhases.map { phase ->
        val t = rememberInfiniteTransition(label = "p")
        t.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween((5000 + phase).toInt(), easing = LinearEasing)),
            label = "particle"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(AuraPrimary.copy(alpha = 0.12f), AuraBase),
                    radius = 800f
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, amount ->
                        change.consume()
                        if (amount.y > 40f) onDismiss()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Down arrow hint
        Box(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.KeyboardArrowDown, "Swipe down to close", tint = AuraOutline.copy(alpha = 0.5f), modifier = Modifier.size(28.dp))
        }

        // Context label top-right
        Column(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(ctx.timeString, style = MaterialTheme.typography.headlineSmall, color = AuraTextPrimary)
            Text(ctx.locationCity, style = MaterialTheme.typography.labelMedium, color = AuraOutline)
        }

        // Breathing orb
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(breathScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AuraPrimary.copy(alpha = 0.4f),
                            AuraPrimary.copy(alpha = 0.08f)
                        )
                    )
                )
                .border(1.dp, AuraPrimary.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                breathLabel,
                style = MaterialTheme.typography.labelMedium,
                color = AuraPrimary,
                textAlign = TextAlign.Center,
                fontSize = 11.sp
            )
        }

        // Affirmation
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Your focus sessions are 34% longer on days\nwhen you skip social media before 10 AM.",
                style = MaterialTheme.typography.bodySmall,
                color = AuraTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onDismiss) {
                Text("Return to AURA", style = MaterialTheme.typography.labelMedium, color = AuraOutline)
            }
        }
    }
}
