package com.aether.widgets.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import com.aether.widgets.ui.theme.AuraPrimary
import com.aether.widgets.ui.theme.AuraSurfaceHighest

// ─── Layout constants ─────────────────────────────────────────────────────────
private val TrackWidth   = 52.dp
private val TrackHeight  = 30.dp
private val ThumbSize    = 22.dp          // resting diameter
private val ThumbPadding =  4.dp          // gap between thumb edge and track edge
private val ThumbStretch = 28.dp          // width when morphed (pressed or traveling)

// Offset from start at which the thumb CENTER sits when checked
private val ThumbOffsetOff = ThumbPadding
private val ThumbOffsetOn  = TrackWidth - ThumbPadding - ThumbSize

/**
 * AuraSwitch - A high-fidelity toggle with spring physics and tactile feedback.
 * Part of the "Quiet Luxury" design system.
 */
@Composable
fun AuraSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    contentDescription: String = if (checked) "Switch on" else "Switch off"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // ── Track color ──────────────────────────────────────────────────────────
    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> AuraSurfaceHighest.copy(alpha = 0.38f)
            checked  -> AuraPrimary
            else     -> AuraSurfaceHighest
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMediumLow
        ),
        label = "aura_switch_track_color"
    )

    // ── Thumb horizontal offset ──────────────────────────────────────────────
    val rawThumbOffset by animateDpAsState(
        targetValue = if (checked) ThumbOffsetOn else ThumbOffsetOff,
        animationSpec = spring(
            dampingRatio = 0.58f,   // Satisfying snap with a tiny overshoot
            stiffness    = 900f
        ),
        label = "aura_switch_thumb_offset"
    )

    // Track bloom effect when toggled
    val bloomScale by animateFloatAsState(
        targetValue = if (checked) 1.1f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "bloomScale"
    )
    
    // Feedback haptic on toggle completion
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(checked) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Guard against spring bouncing outside safe bounds – prevents negative padding crash
    val thumbOffset: Dp by remember {
        derivedStateOf {
            rawThumbOffset.coerceIn(
                minimumValue = 0.dp,
                maximumValue = (TrackWidth - ThumbSize - ThumbPadding).coerceAtLeast(0.dp)
            )
        }
    }

    // ── Thumb width – stretches when pressed (rubbery morph) ─────────────────
    val thumbWidth by animateDpAsState(
        targetValue = if (isPressed) ThumbStretch else ThumbSize,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessHigh
        ),
        label = "aura_switch_thumb_width"
    )
    val safeThumbWidth: Dp by remember {
        derivedStateOf { thumbWidth.coerceIn(ThumbSize, ThumbStretch) }
    }

    // ── Whole-switch scale on press ──────────────────────────────────────────
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessHigh
        ),
        label = "aura_switch_scale"
    )

    // ── Thumb elevation shadow alpha ─────────────────────────────────────────
    val thumbShadowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.10f else 0.22f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "aura_switch_shadow"
    )

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .width(TrackWidth)
            .height(TrackHeight)
            .graphicsLayer {
                scaleX = scale * bloomScale
                scaleY = scale * bloomScale
                alpha  = if (enabled) 1f else 0.5f
            }
            .clip(CircleShape)
            .background(trackColor)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication        = null,
                        onClick           = { onCheckedChange(!checked) }
                    )
                } else {
                    Modifier   // disabled – no click, no ripple
                }
            )
            .semantics {
                this.contentDescription = contentDescription
                this.role               = Role.Switch
                this.toggleableState    = if (checked) ToggleableState.On else ToggleableState.Off
            }
    ) {
        // ── Thumb ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .width(safeThumbWidth)
                .height(ThumbSize)
                .graphicsLayer { shadowElevation = 6f * thumbShadowAlpha * density }
                .clip(RoundedCornerShape(percent = 50))   // stays pill/circle at all widths
                .background(Color.White)
        )
    }
}
