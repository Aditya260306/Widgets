package com.aether.widgets.ui.theme

import androidx.compose.runtime.compositionLocalOf

enum class AuraDesignMood {
    MINIMAL,
    BALANCED,
    RICH
}

val LocalAuraDesignMood = compositionLocalOf { AuraDesignMood.BALANCED }
