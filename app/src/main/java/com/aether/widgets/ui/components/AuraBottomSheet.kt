package com.aether.widgets.ui.components

import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aether.widgets.ui.theme.*

/**
 * AuraBottomSheet — styled ModalBottomSheet wrapper.
 * 28dp top radius, drag handle, correct background tones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AuraSurfaceHigh,
        shape = ShapeSheet,
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(AuraOutline.copy(alpha = 0.4f))
                )
            }
        }
    ) {
        content()
    }
}

/**
 * ConfirmSheet — reusable destructive action confirmation bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmSheet(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: androidx.compose.ui.graphics.Color = AuraRose
) {
    val haptic = LocalHapticFeedback.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    AuraBottomSheet(onDismiss = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall, color = AuraTextPrimary)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = AuraTextSecondary)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onConfirm()
                    onDismiss() 
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor),
                shape = ShapeButton
            ) {
                Text(confirmLabel, color = AuraTextPrimary, style = MaterialTheme.typography.labelLarge)
            }
            TextButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel", color = AuraOutline, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

/**
 * SectionHeader — Georgia Bold, uppercase, 32/8 padding.
 */
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        color = AuraTextSecondary,
        modifier = modifier.padding(top = 32.dp, bottom = 8.dp)
    )
}

/**
 * StatusStrip — AURA wordmark + API calls counter + gear.
 */
@Composable
fun StatusStrip(
    apiUsed: Int,
    apiLimit: Int,
    onGearClick: () -> Unit,
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier,
    scrollOffset: Int = 0
) {
    val haptic = LocalHapticFeedback.current
    val weightProgress = (scrollOffset / 200f).coerceIn(0f, 1f)
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // AURA wordmark
        Text(
            "AURA",
            style = MaterialTheme.typography.headlineMedium,
            color = AuraPrimary,
            fontWeight = FontWeight(700 + (weightProgress * 200).toInt()),
            modifier = Modifier.graphicsLayer {
                // Subtle scale up as we scroll
                val s = 1f + (weightProgress * 0.05f)
                scaleX = s
                scaleY = s
            }
        )

        // API counter and gear
        Row(verticalAlignment = Alignment.CenterVertically) {
            HalfArc(
                progress = apiUsed.toFloat() / apiLimit.toFloat(),
                modifier = Modifier.size(28.dp),
                progressColor = AuraPrimary
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "$apiUsed / $apiLimit",
                style = MaterialTheme.typography.labelMedium,
                color = AuraTextSecondary
            )
            Spacer(Modifier.width(16.dp))
            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onGearClick()
                }, 
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = AuraOutline,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
