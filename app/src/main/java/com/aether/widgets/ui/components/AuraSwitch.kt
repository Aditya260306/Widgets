package com.aether.widgets.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.aether.widgets.ui.theme.AuraPrimary
import com.aether.widgets.ui.theme.AuraSurfaceHighest

@Composable
fun AuraSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val trackColor by animateColorAsState(
        targetValue = if (checked) AuraPrimary else AuraSurfaceHighest,
        animationSpec = spring(
            dampingRatio = 0.6f, 
            stiffness = 800f
        ),
        label = "trackColor"
    )
    
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = 0.55f, // "Crunchy" snap
            stiffness = 1000f
        ),
        label = "thumbOffset"
    )
    
    // Coerce thumbOffset to avoid negative padding if spring bounces past 0
    val safeThumbOffset = thumbOffset.coerceAtLeast(0.dp)
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 1200f
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(52.dp, 32.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(trackColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onCheckedChange(!checked) }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = safeThumbOffset)
                .size(if (isPressed) 20.dp else 26.dp) // Thumb shrink on press
                .clip(CircleShape)
                .background(Color.White)
                .animateContentSize()
        )
    }
}
