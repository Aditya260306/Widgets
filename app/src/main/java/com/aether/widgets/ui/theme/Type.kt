package com.aether.widgets.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

/**
 * AURA Typescale — Quiet Luxury
 *
 * Georgia     → FontFamily.Serif     (Display / Title)
 * Noto Serif  → FontFamily.Serif     (Body)
 * Roboto Mono → FontFamily.Monospace (Data / Metadata)
 *
 * Using system-bundled font families for zero-dependency offline build.
 */

private val GeorgiaFamily = FontFamily.Serif
private val NotoSerifFamily = FontFamily.Serif
private val RobotoMonoFamily = FontFamily.Monospace

val Typography = Typography(

    // ─── Display ─────────────────────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily = GeorgiaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp,
        color = AuraTextPrimary
    ),
    displayMedium = TextStyle(
        fontFamily = GeorgiaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp,
        color = AuraTextPrimary
    ),
    displaySmall = TextStyle(
        fontFamily = GeorgiaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        color = AuraTextPrimary
    ),

    // ─── Headline ────────────────────────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily = GeorgiaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        color = AuraTextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = GeorgiaFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        color = AuraTextPrimary
    ),
    headlineSmall = TextStyle(
        fontFamily = GeorgiaFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = AuraTextPrimary
    ),

    // ─── Title ───────────────────────────────────────────────────────────────
    titleLarge = TextStyle(
        fontFamily = GeorgiaFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = AuraTextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = AuraTextPrimary
    ),
    titleSmall = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = AuraTextPrimary
    ),

    // ─── Body ────────────────────────────────────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = AuraTextPrimary
    ),
    bodyMedium = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = AuraTextPrimary
    ),
    bodySmall = TextStyle(
        fontFamily = NotoSerifFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp,
        color = AuraTextSecondary
    ),

    // ─── Label (Roboto Mono — metadata) ──────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily = RobotoMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp,
        color = AuraTextTertiary
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.sp,
        color = AuraTextTertiary
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp,
        color = AuraTextTertiary
    )
)
