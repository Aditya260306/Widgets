package com.aether.widgets.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aether.widgets.data.KeyManager

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
    keyManager: KeyManager? = null,
    // AURA is strictly a dark-only design system
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val mood = remember(keyManager) {
        keyManager?.getDesignMood()?.let { 
            try { AuraDesignMood.valueOf(it.uppercase()) } catch(e: Exception) { AuraDesignMood.BALANCED }
        } ?: AuraDesignMood.BALANCED
    }

    // Tonal Layering logic based on Mood
    val baseColor = when(mood) {
        AuraDesignMood.MINIMAL -> Color(0xFF000000) // Pure black
        AuraDesignMood.BALANCED -> AuraBase
        AuraDesignMood.RICH -> Color(0xFF0C0C14) // Slightly softer indigo-black
    }

    CompositionLocalProvider(LocalAuraDesignMood provides mood) {
        MaterialTheme(
            colorScheme = AuraColorScheme.copy(background = baseColor),
            typography = Typography,
            content = content
        )
    }
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

