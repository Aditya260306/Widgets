package com.aether.widgets.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.aether.widgets.ui.theme.AuraPrimary
import kotlin.random.Random

// ── Reveal direction ──────────────────────────────────────────────────────────
enum class RevealDirection {
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP,
    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT
}

// ── Reveal state ─────────────────────────────────────────────────────────────
class RevealAnimationState {
    var isRevealing by mutableStateOf(false)
        private set

    fun reveal() {
        isRevealing = true
    }

    internal fun reset() {
        isRevealing = false
    }
}

@Composable
fun rememberRevealAnimationState(): RevealAnimationState {
    return remember { RevealAnimationState() }
}

// ── Internal shape — clips the incoming content to the revealed portion ───────
private class RevealClipShape(
    private val progress: Float,
    private val direction: RevealDirection
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val rect = when (direction) {
            RevealDirection.TOP_TO_BOTTOM -> Rect(
                left   = 0f,
                top    = 0f,
                right  = size.width,
                bottom = size.height * progress
            )
            RevealDirection.BOTTOM_TO_TOP -> Rect(
                left   = 0f,
                top    = size.height * (1f - progress),
                right  = size.width,
                bottom = size.height
            )
            RevealDirection.LEFT_TO_RIGHT -> Rect(
                left   = 0f,
                top    = 0f,
                right  = size.width * progress,
                bottom = size.height
            )
            RevealDirection.RIGHT_TO_LEFT -> Rect(
                left   = size.width * (1f - progress),
                top    = 0f,
                right  = size.width,
                bottom = size.height
            )
        }
        return Outline.Rectangle(rect)
    }
}

// ── The main composable ───────────────────────────────────────────────────────
@Composable
fun RevealTransition(
    state: RevealAnimationState,
    direction: RevealDirection = RevealDirection.TOP_TO_BOTTOM,
    durationMillis: Int = 1000,
    onRevealComplete: () -> Unit = {},
    modifier: Modifier = Modifier,
    behindContent: @Composable () -> Unit,
    revealedContent: @Composable () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue    = if (state.isRevealing) 1f else 0f,
        animationSpec  = tween(
            durationMillis = durationMillis,
            easing         = CubicBezierEasing(0.25f, 0.46f, 0.45f, 0.94f)
        ),
        label          = "reveal_progress"
    )

    // Dot particles for the disintegration effect
    val particles = remember {
        List(60) {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 1f,
                velocity = Random.nextFloat() * 0.5f + 0.2f,
                seed = Random.nextFloat()
            )
        }
    }

    LaunchedEffect(progress) {
        if (progress >= 1f && state.isRevealing) {
            onRevealComplete()
            state.reset()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Layer 1 — content sitting behind (old state)
        behindContent()

        // Layer 2 — incoming content clipped to revealed portion
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    clip  = true
                    shape = RevealClipShape(progress, direction)
                }
        ) {
            revealedContent()
        }

        // Layer 3 — Disintegration edge with glow dots
        if (state.isRevealing && progress > 0f && progress < 1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val edge = when (direction) {
                    RevealDirection.TOP_TO_BOTTOM -> progress * size.height
                    RevealDirection.BOTTOM_TO_TOP -> (1f - progress) * size.height
                    RevealDirection.LEFT_TO_RIGHT -> progress * size.width
                    RevealDirection.RIGHT_TO_LEFT -> (1f - progress) * size.width
                }

                drawRevealEdge(edge, direction, particles, progress)
            }
        }
    }
}

private fun DrawScope.drawRevealEdge(
    edge: Float,
    direction: RevealDirection,
    particles: List<Particle>,
    progress: Float
) {
    val glowColor = AuraPrimary.copy(alpha = 0.8f)
    
    // Draw the main glowing line (feathered)
    val strokeWidth = 8f
    val alphaMultiplier = (1f - progress).coerceAtLeast(0.2f)

    // Helper for drawing particles
    fun drawParticles(isVertical: Boolean, mainCoord: Float) {
        particles.forEach { p ->
            val drift = (progress * 300f * p.velocity)
            val fade = (1f - progress).coerceIn(0f, 1f)
            
            val px: Float
            val py: Float
            
            if (isVertical) {
                px = p.x * size.width
                py = mainCoord + (if (direction == RevealDirection.TOP_TO_BOTTOM) -drift else drift)
            } else {
                px = mainCoord + (if (direction == RevealDirection.LEFT_TO_RIGHT) -drift else drift)
                py = p.y * size.height
            }
            
            drawCircle(
                color = AuraPrimary.copy(alpha = 0.6f * fade),
                radius = p.size,
                center = Offset(px, py)
            )
        }
    }

    when (direction) {
        RevealDirection.TOP_TO_BOTTOM, RevealDirection.BOTTOM_TO_TOP -> {
            drawLine(glowColor.copy(alpha = 0.4f * alphaMultiplier), Offset(0f, edge - 2), Offset(size.width, edge - 2), strokeWidth = strokeWidth * 2)
            drawLine(glowColor, Offset(0f, edge), Offset(size.width, edge), strokeWidth = strokeWidth)
            drawParticles(true, edge)
        }
        RevealDirection.LEFT_TO_RIGHT, RevealDirection.RIGHT_TO_LEFT -> {
            drawLine(glowColor.copy(alpha = 0.4f * alphaMultiplier), Offset(edge - 2, 0f), Offset(edge - 2, size.height), strokeWidth = strokeWidth * 2)
            drawLine(glowColor, Offset(edge, 0f), Offset(edge, size.height), strokeWidth = strokeWidth)
            drawParticles(false, edge)
        }
    }
}

private data class Particle(val x: Float, val y: Float, val size: Float, val velocity: Float, val seed: Float)
