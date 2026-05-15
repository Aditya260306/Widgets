package com.aether.widgets.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Background / Surface tiers ───────────────────────────────────────────────
val AuraBase            = Color(0xFF0A0A0F)   // Page background
val AuraSurface         = Color(0xFF131318)   // Card / tile background
val AuraSurfaceHigh     = Color(0xFF1B1B20)   // Elevated surface
val AuraSurfaceHighest  = Color(0xFF35343A)   // Highest surface (sheets)

// ─── Primary accent ───────────────────────────────────────────────────────────
val AuraPrimary         = Color(0xFF7C5CFC)   // Violet
val AuraPrimaryDark     = Color(0xFF5A3DCC)
val AuraPrimaryLight    = Color(0xFFCABEFF)

// ─── Semantic accent palette ──────────────────────────────────────────────────
val AuraMint            = Color(0xFF28DFB3)   // Success / Live / Fresh (Stitch Mint)
val AuraRose            = Color(0xFFFF5F87)   // Error / Destructive
val AuraGold            = Color(0xFFEFC13E)   // Insight / Default accented (Stitch Gold)

// Legacy aliases (keep so existing code compiles)
val AuraInsight         = AuraGold
val AuraSuccess         = AuraMint
val AuraError           = AuraRose

// ─── Text hierarchy ───────────────────────────────────────────────────────────
val AuraTextPrimary     = Color(0xFFF0F2F7)   // Headings / display
val AuraTextSecondary   = Color(0xFF6B7A99)   // Body captions
val AuraTextTertiary    = Color(0xFFB8C2D4)   // Metadata / labels

// ─── Legacy on-surface tokens ────────────────────────────────────────────────
val AuraOnBackground        = Color(0xFFE4E1E9)
val AuraOnSurfaceVariant    = Color(0xFFC9C4D8)
val AuraOutline             = Color(0xFF938EA1)
val AuraOutlineVariant      = Color(0xFF484555)

// ─── Tonal layering ghost border ──────────────────────────────────────────────
val AuraGhostBorder     = AuraOutlineVariant.copy(alpha = 0.5f)


