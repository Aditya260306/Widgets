package com.aether.widgets.widgets.weather

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aether.widgets.db.AetherDatabase
import com.aether.widgets.db.WidgetEntity
import com.aether.widgets.widgets.WidgetFidgetState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class WeatherWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WeatherWidgetEntryPoint {
        fun database(): AetherDatabase
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(appContext, WeatherWidgetEntryPoint::class.java)
        val db = entryPoint.database()
        val widgetId = 1 

        provideContent {
            val widgetState by db.widgetDao().getWidgetById(widgetId).collectAsState(initial = null)
            val prefs = currentState<androidx.datastore.preferences.core.Preferences>()
            val fontWeightIndex = prefs[WidgetFidgetState.FONT_WEIGHT_KEY] ?: 1
            val wipeCount = prefs[WidgetFidgetState.WIPE_COUNT_KEY] ?: 0
            
            WeatherWidgetContent(widgetState, fontWeightIndex, wipeCount)
        }
    }

    @Composable
    private fun WeatherWidgetContent(state: WidgetEntity?, weightIndex: Int, wipeCount: Int) {
        val fontWeight = when (weightIndex % 2) {
            0 -> FontWeight.Normal
            else -> FontWeight.Bold
        }

        // Simulating glassmorphism in Glance
        val backgroundColor = if (wipeCount >= 3) {
            ColorProvider(androidx.compose.ui.graphics.Color(0xCC7C5CFC)) // Clearer when wiped
        } else {
            ColorProvider(androidx.compose.ui.graphics.Color(0x337C5CFC)) // Frosted normally
        }

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
                .background(backgroundColor)
                .clickable(actionRunCallback<FidgetAction>())
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.Horizontal.Start,
                verticalAlignment = Alignment.Vertical.CenterVertically
            ) {
                Text(
                    text = "AURA",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = state?.content ?: "Analyzing atmosphere...",
                    style = TextStyle(
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White),
                        fontSize = 14.sp,
                        fontWeight = fontWeight
                    )
                )
            }
        }
    }
}

class FidgetAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            val currentWeight = prefs[WidgetFidgetState.FONT_WEIGHT_KEY] ?: 1
            val currentWipe = prefs[WidgetFidgetState.WIPE_COUNT_KEY] ?: 0
            
            prefs.toMutablePreferences().apply {
                this[WidgetFidgetState.FONT_WEIGHT_KEY] = currentWeight + 1
                this[WidgetFidgetState.WIPE_COUNT_KEY] = (currentWipe + 1) % 10 // Reset after 10 taps or so
            }
        }
        WeatherWidget().update(context, glanceId)
    }
}
