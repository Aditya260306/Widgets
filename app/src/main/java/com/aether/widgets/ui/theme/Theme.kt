package com.aether.widgets.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ─── Shape tokens ─────────────────────────────────────────────────────────────
val ShapeCard    = RoundedCornerShape(16.dp)
val ShapeSheet   = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val ShapeButton  = RoundedCornerShape(12.dp)
val ShapeChip    = RoundedCornerShape(999.dp)
val ShapeSmall   = RoundedCornerShape(8.dp)
val ShapeTile    = RoundedCornerShape(12.dp)

// ─── Color scheme ─────────────────────────────────────────────────────────────
private val AuraColorScheme = darkColorScheme(
    primary         = AuraPrimary,
    onPrimary       = AuraTextPrimary,
    primaryContainer = AuraSurfaceHigh,
    onPrimaryContainer = AuraPrimaryLight,
    secondary       = AuraGold,
    onSecondary     = AuraBase,
    tertiary        = AuraMint,
    onTertiary      = AuraBase,
    error           = AuraRose,
    onError         = AuraTextPrimary,
    errorContainer  = AuraRose.copy(alpha = 0.15f),
    background      = AuraBase,
    onBackground    = AuraOnBackground,
    surface         = AuraSurface,
    onSurface       = AuraTextPrimary,
    surfaceVariant  = AuraSurfaceHigh,
    onSurfaceVariant = AuraOnSurfaceVariant,
    surfaceContainer = AuraSurfaceHigh,
    surfaceContainerHigh = AuraSurfaceHighest,
    surfaceContainerHighest = AuraSurfaceHighest,
    outline         = AuraOutline,
    outlineVariant  = AuraOutlineVariant,
    inverseSurface  = AuraTextPrimary,
    inverseOnSurface = AuraBase
)

@Composable
fun AetherTheme(
    // AURA is strictly a dark-only design system
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AuraColorScheme,
        typography = Typography,
        content = content
    )
}

// ─── Ghost border utility ────────────────────────────────────────────────────
fun Modifier.ghostBorder(cornerRadius: Int = 12) = this.border(
    width = 1.dp,
    color = AuraGhostBorder,
    shape = RoundedCornerShape(cornerRadius.dp)
)

// ─── Violet border (FRESH state) ─────────────────────────────────────────────
fun Modifier.freshBorder() = this.border(
    width = 1.dp,
    color = AuraPrimary.copy(alpha = 0.6f),
    shape = ShapeCard
)

