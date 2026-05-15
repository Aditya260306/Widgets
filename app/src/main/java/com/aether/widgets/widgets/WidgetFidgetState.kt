package com.aether.widgets.widgets

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey

object WidgetFidgetState {
    val FONT_WEIGHT_KEY = intPreferencesKey("font_weight")
    val WIPE_COUNT_KEY = intPreferencesKey("wipe_count")
    val IS_CLEAR_KEY = booleanPreferencesKey("is_clear")
}

